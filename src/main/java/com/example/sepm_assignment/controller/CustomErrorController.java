package com.example.sepm_assignment.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        String errorMessage = "An unexpected error occurred";
        String errorDetails = "";
        int statusCode = 500;

        if (status != null) {
            statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "Page Not Found";
                errorDetails = "The page you are looking for does not exist.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Access Denied";
                errorDetails = "You don't have permission to access this resource.";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                errorMessage = "Unauthorized";
                errorDetails = "Please login to access this resource.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Internal Server Error";
                errorDetails = "Something went wrong on our end. Please try again later.";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                errorMessage = "Bad Request";
                errorDetails = "The request could not be understood by the server.";
            } else if (statusCode == HttpStatus.METHOD_NOT_ALLOWED.value()) {
                errorMessage = "Method Not Allowed";
                errorDetails = "The request method is not supported for this endpoint.";
            } else if (statusCode == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
                errorMessage = "Unsupported Media Type";
                errorDetails = "The request format is not supported.";
            }
        }

        if (message != null && !message.toString().isEmpty()) {
            errorDetails = message.toString();
        }

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorDetails", errorDetails);
        model.addAttribute("timestamp", java.time.LocalDateTime.now());

        return "error";
    }
}
