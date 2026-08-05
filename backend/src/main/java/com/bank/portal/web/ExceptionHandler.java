package com.bank.portal.web;
import java.lang.annotation.*;
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) public @interface ExceptionHandler { Class<? extends Throwable>[] value(); }
