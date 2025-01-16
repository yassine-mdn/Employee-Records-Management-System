package com.erms.back.util.OpenApi;

import io.swagger.v3.oas.models.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class OperationNotesResourcesReader implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {


        var annotation = handlerMethod.getMethodAnnotation(UserRoleDescription.class);
        if (annotation != null) {
            var securedAnnotation = handlerMethod.getMethodAnnotation(PreAuthorize.class);
            if(securedAnnotation != null) {
                String description = operation.getDescription()==null ? "" : (operation.getDescription()+"\n");
                operation.setDescription(description + "Required role: "+ getRoleString(securedAnnotation.value()));
            }
        }
        return operation;
    }

    private String getRoleString(String input) {
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        if (input == null || input.isEmpty())
            return "NONE";
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {

            String content = matcher.group(1);
            String[] roles = content.replace("'", "").split("\\s*,\\s*");
            for (int i = 0; i < roles.length; i++) {
                roles[i] = "**" + roles[i] + "**";
            }
            return String.join(" or ", roles);
        }
        return "NONE";
    }
}