package com.suvaathy.hrmanagement.controller;

import com.suvaathy.hrmanagement.exception.HrBusinessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({HrBusinessException.class, IllegalArgumentException.class, IllegalStateException.class})
    public String handleBusinessError(RuntimeException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        return "error";
    }
    @ExceptionHandler(Exception.class)
    public String handleUnexpectedError(Exception exception, Model model) {
        model.addAttribute("errorMessage", "The request could not be completed. Check the entered information and try again.");
        return "error";
    }
}