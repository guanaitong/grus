/*
 * Copyright 2007-2021, CIIC Guanaitong, Co., Ltd.
 * All rights reserved.
 */

package com.ciicgat.grus.boot.autoconfigure.validation.hibernate;

import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanchongyang
 * @date 2020/5/9 4:54 下午
 */
@RestController
@RequestMapping("/validate")
@Validated
public class ValidateController {
    @Autowired
    private ValidateService validateService;

    @PostMapping("/testRequestParams")
    public String testRequestParams(@NotNull Integer personId,
                                    @NotNull @Length(min = 5, max = 50) String personName) {
        return personId + personName;
    }

    @PostMapping("/testValidateService")
    public String testValidateService(Integer personId, String personName) {
        validateService.save(personId, personName);
        return personId + personName;
    }

    @PostMapping("/testFormData")
    public ResponseEntity<List<String>> testFormData(@Validated PersonRequest personRequest,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMsgList = bindingResult.getAllErrors().stream()
                    .map(objectError -> {
                        if (objectError instanceof FieldError) {
                            FieldError fieldError = (FieldError) objectError;
                            return fieldError.getField() + fieldError.getDefaultMessage();
                        }

                        return objectError.getDefaultMessage();
                    })
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errorMsgList, HttpStatus.OK);
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @PostMapping("/testRequestBody")
    public List<String> testRequestBody(@Validated @RequestBody PersonRequest personRequest,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return bindingResult.getAllErrors().stream()
                    .map(objectError -> {
                        if (objectError instanceof FieldError) {
                            FieldError fieldError = (FieldError) objectError;
                            return fieldError.getField() + fieldError.getDefaultMessage();
                        }

                        return objectError.getDefaultMessage();
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
