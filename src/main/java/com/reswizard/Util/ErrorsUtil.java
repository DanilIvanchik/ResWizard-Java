package com.reswizard.Util;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.List;

public class ErrorsUtil {
    private ErrorsUtil() {
    }

    /**
     * Return validation errors to the client.
     *
     * @param bindingResult The BindingResult object containing validation errors.
     * @throws StorageFileNotFoundException with error details if validation fails.
     */
    public static void returnErrorsToClient(BindingResult bindingResult){
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError error: fieldErrors) {
            stringBuilder.append(error.getField())
                    .append("-")
                    .append(error.getDefaultMessage())
                    .append(";");
        }
        throw new StorageFileNotFoundException(stringBuilder.toString());
    }
}

