package com.example.demo.rule;


import org.passay.*;
import org.passay.UppercaseCharacterRule;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = createPasswordValidator();
        RuleResult result = validator.validate(new PasswordData(password));

        if (!result.isValid()) {
            List<String> errorMessages = validator.getMessages(result);
            String errorMessage = String.join(",", errorMessages);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private PasswordValidator createPasswordValidator() {
        return new PasswordValidator(
                Arrays.asList(
                        new LengthRule(8, 30),
                        new DigitCharacterRule(1),
                        new WhitespaceRule()
                )
        );
    }
}
