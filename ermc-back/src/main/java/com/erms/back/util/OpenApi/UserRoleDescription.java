package com.erms.back.util.OpenApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Annotation to display required Role in Open-api documentation
 * <br>
 * <b>Should be placed above method mapping!!!!</b>
*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRoleDescription {

}