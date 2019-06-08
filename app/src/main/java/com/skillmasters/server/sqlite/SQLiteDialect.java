package com.skillmasters.server.sqlite;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import java.sql.Types;
import java.sql.SQLException;

import org.hibernate.type.StandardBasicTypes;
import org.hibernate.mapping.Column;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;
import org.hibernate.dialect.function.AbstractAnsiTrimEmulationFunction;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.dialect.function.VarArgsSQLFunction;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.exception.DataException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.JDBCException;
import org.hibernate.Hibernate;
import org.hibernate.ScrollMode;

public class SQLiteDialect extends Dialect
{
  private final UniqueDelegate uniqueDelegate;

  public SQLiteDialect()
  {
    registerColumnType(Types.BIT, "boolean");
    registerColumnType(Types.TINYINT, "tinyint");
    registerColumnType(Types.SMALLINT, "smallint");
    registerColumnType(Types.INTEGER, "integer");
    registerColumnType(Types.BIGINT, "bigint");
    registerColumnType(Types.FLOAT, "float");
    registerColumnType(Types.REAL, "real");
    registerColumnType(Types.DOUBLE, "double");
    registerColumnType(Types.NUMERIC, "numeric");
    registerColumnType(Types.DECIMAL, "decimal");
    registerColumnType(Types.CHAR, "char");
    registerColumnType(Types.VARCHAR, "varchar");
    registerColumnType(Types.LONGVARCHAR, "longvarchar");
    registerColumnType(Types.DATE, "date");
    registerColumnType(Types.TIME, "time");
    registerColumnType(Types.TIMESTAMP, "timestamp");
    // registerColumnType(Types.TIMESTAMP, "datetime");
    registerColumnType(Types.BINARY, "blob");
    registerColumnType(Types.VARBINARY, "blob");
    registerColumnType(Types.LONGVARBINARY, "blob");
    // registerColumnType(Types.NULL, "null");
    registerColumnType(Types.BLOB, "blob");
    registerColumnType(Types.CLOB, "clob");
    registerColumnType(Types.BOOLEAN, "integer");

    registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "", "||", "" ) );
    registerFunction( "mod", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1 % ?2" ) );
    registerFunction( "quote", new StandardSQLFunction( "quote", StandardBasicTypes.STRING ) );
    registerFunction( "random", new NoArgSQLFunction( "random", StandardBasicTypes.INTEGER ) );
    registerFunction( "round", new StandardSQLFunction( "round" ) );
    registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
    registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
    registerFunction( "trim", new AbstractAnsiTrimEmulationFunction() {
      protected SQLFunction resolveBothSpaceTrimFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1)" );
      }

      protected SQLFunction resolveBothSpaceTrimFromFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?2)" );
      }

      protected SQLFunction resolveLeadingSpaceTrimFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "ltrim(?1)" );
      }

      protected SQLFunction resolveTrailingSpaceTrimFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "rtrim(?1)" );
      }

      protected SQLFunction resolveBothTrimFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1, ?2)" );
      }

      protected SQLFunction resolveLeadingTrimFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "ltrim(?1, ?2)" );
      }

      protected SQLFunction resolveTrailingTrimFunction()
      {
        return new SQLFunctionTemplate( StandardBasicTypes.STRING, "rtrim(?1, ?2)" );
      }
    } );

    uniqueDelegate = new SQLiteUniqueDelegate( this );
  }

  @Override
  public String getCastTypeName(int code)
  {
    return super.getCastTypeName(code);
  }

  public boolean supportsIdentityColumns()
  {
    return true;
  }

  /*
  public boolean supportsInsertSelectIdentity()
  {
    return true; // As specify in NHibernate dialect
  }
  */

  public boolean hasDataTypeInIdentityColumn()
  {
    return false; // As specify in NHibernate dialect
  }

  /*
  public String appendIdentitySelectToInsert(String insertString) {
  return new StringBuffer(insertString.length()+30). // As specify in NHibernate dialect
    append(insertString).
    append("; ").append(getIdentitySelectString()).
    toString();
  }
  */

  public String getIdentityColumnString()
  {
    // return "integer primary key autoincrement";
    return "integer";
  }

  public String getIdentitySelectString()
  {
    return "select last_insert_rowid()";
  }

  public boolean supportsLimit()
  {
    return true;
  }

  protected String getLimitString(String query, boolean hasOffset)
  {
    return query + (hasOffset ? " limit ? offset ?" : " limit ?");
    // return new StringBuffer(query.length() + 20)
    //     .append(query)
    //     .append(hasOffset ? " limit ? offset ?" : " limit ?")
    //     .toString();
  }

  private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
    @Override
    public String processSql(String query, RowSelection selection)
    {
      final boolean hasOffset = LimitHelper.hasFirstRow( selection );
      return query + (hasOffset ? " limit ? offset ?" : " limit ?");
    }

    @Override
    public boolean supportsLimit()
    {
      return true;
    }

    @Override
    public boolean bindLimitParametersInReverseOrder()
    {
      return true;
    }
  };

  @Override
  public LimitHandler getLimitHandler()
  {
    return LIMIT_HANDLER;
  }

  public boolean supportsTemporaryTables()
  {
    return true;
  }

  public String getCreateTemporaryTableString()
  {
    return "create temporary table if not exists";
  }

  public boolean dropTemporaryTableAfterUse()
  {
    return false;
  }

  public boolean supportsCurrentTimestampSelection()
  {
    return true;
  }

  public boolean isCurrentTimestampSelectStringCallable()
  {
    return false;
  }

  public String getCurrentTimestampSelectString()
  {
    return "select current_timestamp";
  }

  public boolean supportsUnionAll()
  {
    return true;
  }

  public boolean hasAlterTable()
  {
    return false; // As specify in NHibernate dialect
  }

  public boolean qualifyIndexName()
  {
    return false;
  }

  public boolean dropConstraints()
  {
    return false;
  }

  public String getAddColumnString()
  {
    return "add column";
  }

  public String getForUpdateString()
  {
    return "";
  }

  @Override
  public boolean supportsOuterJoinForUpdate()
  {
    return false;
  }

  @Override
  public boolean supportsLockTimeouts()
  {
    return false;
  }

  public String getDropForeignKeyString()
  {
    throw new UnsupportedOperationException("No drop foreign key syntax supported by SQLiteDialect");
  }

  public String getAddForeignKeyConstraintString(String constraintName,
                           String[] foreignKey, String referencedTable, String[] primaryKey,
                           boolean referencesPrimaryKey)
  {
    throw new UnsupportedOperationException("No add foreign key syntax supported by SQLiteDialect");
  }

  public String getAddPrimaryKeyConstraintString(String constraintName)
  {
    throw new UnsupportedOperationException("No add primary key syntax supported by SQLiteDialect");
  }

  public boolean supportsIfExistsBeforeTableName()
  {
    return true;
  }

  public boolean supportsCommentOn()
  {
    return true;
  }

  public boolean supportsCascadeDelete()
  {
    return false;
  }

  @Override
  public boolean doesReadCommittedCauseWritersToBlockReaders()
  {
    return true;
  }

  public boolean doesRepeatableReadCauseReadersToBlockWriters()
  {
    return true;
  }

  @Override
  public boolean supportsTupleDistinctCounts()
  {
    return false;
  }

  public int getInExpressionCountLimit()
  {
    // Compile/runtime time option: http://sqlite.org/limits.html#max_variable_number
    return 1000;
  }

  private static final int SQLITE_BUSY = 5;
  private static final int SQLITE_LOCKED = 6;
  private static final int SQLITE_IOERR = 10;
  private static final int SQLITE_CORRUPT = 11;
  private static final int SQLITE_NOTFOUND = 12;
  private static final int SQLITE_FULL = 13;
  private static final int SQLITE_CANTOPEN = 14;
  private static final int SQLITE_PROTOCOL = 15;
  private static final int SQLITE_TOOBIG = 18;
  private static final int SQLITE_CONSTRAINT = 19;
  private static final int SQLITE_MISMATCH = 20;
  private static final int SQLITE_NOTADB = 26;

  @Override
  public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate()
  {
    return new SQLExceptionConversionDelegate() {
      @Override
      public JDBCException convert(SQLException sqlException, String message, String sql) {
        final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
        if (errorCode == SQLITE_TOOBIG || errorCode == SQLITE_MISMATCH) {
          return new DataException( message, sqlException, sql );
        } else if (errorCode == SQLITE_BUSY || errorCode == SQLITE_LOCKED) {
          return new LockAcquisitionException( message, sqlException, sql );
        } else if ((errorCode >= SQLITE_IOERR && errorCode <= SQLITE_PROTOCOL) || errorCode == SQLITE_NOTADB) {
          return new JDBCConnectionException( message, sqlException, sql );
        }
        return null;
      }
    };
  }

  public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter()
  {
    return EXTRACTER;
  }

  private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
    @Override
    protected  String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
      final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
      if (errorCode == SQLITE_CONSTRAINT) {
        return extractUsingTemplate( "constraint ", " failed", sqle.getMessage() );
      }
      return null;
    }
  };

  @Override
  public UniqueDelegate getUniqueDelegate()
  {
    return uniqueDelegate;
  }

  private static class SQLiteUniqueDelegate extends DefaultUniqueDelegate
  {
    public SQLiteUniqueDelegate(Dialect dialect)
    {
      super(dialect);
    }

    @Override
    public String getColumnDefinitionUniquenessFragment(Column column)
    {
      return " unique";
    }
  }

  @Override
  public String getSelectGUIDString()
  {
    return "select hex(randomblob(16))";
  }

  @Override
  public ScrollMode defaultScrollMode()
  {
    return ScrollMode.FORWARD_ONLY;
  }
}