package com.reswizard.Util;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.List;

public class ErrorsUtil {
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

