package com.example.sepm_assignment.config;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoHandlerFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("statusCode", 404);
        mav.addObject("errorMessage", "Page Not Found");
        mav.addObject("errorDetails", "The page you are looking for does not exist: " + ex.getRequestURL());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("statusCode", 403);
        mav.addObject("errorMessage", "Access Denied");
        mav.addObject("errorDetails", "You don't have permission to access this resource.");
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("statusCode", 400);
        mav.addObject("errorMessage", "Bad Request");
        mav.addObject("errorDetails", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("statusCode", 500);
        mav.addObject("errorMessage", "Internal Server Error");
        mav.addObject("errorDetails", "Something went wrong: " + ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneralException(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("statusCode", 500);
        mav.addObject("errorMessage", "Unexpected Error");
        mav.addObject("errorDetails", "An unexpected error occurred. Please try again later.");
        mav.addObject("timestamp", LocalDateTime.now());
        return mav;
    }
}
