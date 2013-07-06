package com.github.nmorel.gw2.batch.config;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Documented
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface BatchDatabase
{
}
