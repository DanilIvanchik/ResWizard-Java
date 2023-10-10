package com.reswizard.Util;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle MaxUploadSizeExceededException.
     *
     * @param e MaxUploadSizeExceededException instance.
     * @return A view name to be displayed for handling the exception, e.g., "FileSizeExceptionPage".
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleError2(MaxUploadSizeExceededException e) {
        return "FileSizeExceptionPage";
    }

}
