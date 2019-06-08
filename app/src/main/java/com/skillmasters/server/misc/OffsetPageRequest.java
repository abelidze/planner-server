package com.skillmasters.server.misc;

import java.io.Serializable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetPageRequest implements Pageable
{
  private static final long serialVersionUID = -25822477129613575L;

  private int limit;
  private long offset;
  private final Sort sort;

  public OffsetPageRequest(long offset, int limit, Sort sort)
  {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset index must not be less than zero!");
    }

    if (limit < 1) {
      throw new IllegalArgumentException("Limit must not be less than one!");
    }

    this.limit = limit;
    this.offset = offset;
    this.sort = sort;
  }

  public OffsetPageRequest(long offset, int limit, Sort.Direction direction, String... properties)
  {
    this(offset, limit, new Sort(direction, properties));
  }

  public OffsetPageRequest(long offset, int limit)
  {
    this(offset, limit, new Sort(Sort.Direction.ASC, "id"));
  }

  @Override
  public int getPageNumber()
  {
    return (int) this.offset / this.limit;
  }

  @Override
  public int getPageSize()
  {
    return this.limit;
  }

  @Override
  public long getOffset()
  {
    return this.offset;
  }

  @Override
  public Sort getSort()
  {
    return this.sort;
  }

  @Override
  public Pageable next()
  {
    return new OffsetPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
  }

  public OffsetPageRequest previous()
  {
    return hasPrevious() ? new OffsetPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
  }

  @Override
  public Pageable previousOrFirst()
  {
    return hasPrevious() ? previous() : first();
  }

  @Override
  public Pageable first()
  {
    return new OffsetPageRequest(0, getPageSize(), getSort());
  }

  @Override
  public boolean hasPrevious()
  {
    return this.offset > this.limit;
  }

  // @Override
  // public boolean equals(Object o)
  // {
  //     if (this == o) {
  //         return true;
  //     }

  //     if (!(o instanceof OffsetPageRequest)) {
  //         return false;
  //     }

  //     OffsetPageRequest that = (OffsetPageRequest) o;
  //     return new EqualsBuilder()
  //             .append(limit, that.limit)
  //             .append(offset, that.offset)
  //             .append(sort, that.sort)
  //             .isEquals();
  // }

  // @Override
  // public int hashCode()
  // {
  //     return new HashCodeBuilder(17, 37)
  //             .append(limit)
  //             .append(offset)
  //             .append(sort)
  //             .toHashCode();
  // }

  // @Override
  // public String toString()
  // {
  //     return new ToStringBuilder(this)
  //             .append("limit", limit)
  //             .append("offset", offset)
  //             .append("sort", sort)
  //             .toString();
  // }
}