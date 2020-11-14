package com.lumiomedical.record.source.register;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/13
 */
@IndexAnnotated
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexedSource
{
}
