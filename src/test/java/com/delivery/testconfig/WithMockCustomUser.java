package com.delivery.testconfig;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    long id() default 1L;
    String userName() default "testUser123";
    String nickName() default "mockCustomUser";
    String  phoneNumber() default "01012345678";
    String role() default "CUSTOMER";
}