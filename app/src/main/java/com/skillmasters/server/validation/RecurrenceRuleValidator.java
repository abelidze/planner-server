package com.skillmasters.server.validation;

import biweekly.ICalVersion;
import biweekly.ValidationWarning;
import biweekly.parameter.ICalParameters;
import biweekly.io.scribe.property.RecurrenceRuleScribe;
import biweekly.io.ParseContext;

import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RecurrenceRuleValidator implements ConstraintValidator<RecurrenceRule, String>
{
  @Lazy
  @Autowired
  RecurrenceRuleScribe scribe;

  @Lazy
  @Autowired
  ParseContext context;

  @Override
  public void initialize(RecurrenceRule rule)
  {
  }

  @Override
  public boolean isValid(String ruleStr, ConstraintValidatorContext ctx)
  {
    if (ruleStr == null || scribe == null || context == null) {
      // ctx.disableDefaultConstraintViolation();
      return true;
    }
    MyRecurrenceRule rule = new MyRecurrenceRule( scribe.parseText(ruleStr, null, new ICalParameters(), context) );
    return rule.isValid();
  }

  private class MyRecurrenceRule extends biweekly.property.RecurrenceRule
  {
    public MyRecurrenceRule(biweekly.property.RecurrenceRule rule)
    {
      super(rule);
    }

    public boolean isValid()
    {
      List<ValidationWarning> warnings = new ArrayList<>();
      validate(new ArrayList<>(), ICalVersion.V2_0, warnings);
      return warnings.size() == 0;
    }
  }
}