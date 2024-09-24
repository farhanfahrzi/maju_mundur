package com.maju_mundur.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor // butuh DI, untuk inject validatornva
public class Validationutil {
    private final Validator validator;

    public void validate(Object obj) {
        Set<ConstraintViolation<Object>> validate = validator
                .validate(obj);// ini ngumpulin validasinya atau kesalahannya
        if(!validate.isEmpty())
            throw new ConstraintViolationException(validate);
    }
}