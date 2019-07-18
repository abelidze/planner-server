// package com.skillmasters.server.validation;
 
// import javax.validation.ConstraintValidator;
// import javax.validation.ConstraintValidatorContext;
 
// import org.springframework.beans.factory.annotation.Autowired;
 
// public class RecurrenceRuleValidator implements ConstraintValidator<RecurrenceRule, String>
// {
//   @Override
//   public void initialize(RecurrenceRule rule)
//   {
//   }

//   @Override
//   public boolean isValid(String ruleStr, ConstraintValidatorContext ctx)
//   {
//     if (personService
//         .findByItn(person.getItn())
//         .map(p -> p.getId())
//         .filter(id -> !id.equals(person.getId()))
//         .isPresent()
//     ) {
//       ctx.disableDefaultConstraintViolation();
//       ctx.buildConstraintViolationWithTemplate("{com.skillmasters.server.validation.RecurrenceRule}")
//           .addPropertyNode("itn")
//           .addConstraintViolation();
//       return false;
//     }
//     return true;
//   }
// }