package de.codecentric.mjl.hello;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface HelloSource {

    public Source value();

    public static enum Source {SESSION, DATABASE, EJB}

}
