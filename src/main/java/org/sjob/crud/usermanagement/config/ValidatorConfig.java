package org.sjob.crud.usermanagement.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {

    @Bean
    public Validator entityValidator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }
}
