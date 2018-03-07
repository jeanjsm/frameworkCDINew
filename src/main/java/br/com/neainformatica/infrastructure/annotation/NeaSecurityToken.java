package br.com.neainformatica.infrastructure.annotation;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by rodolpho.sotolani on 03/02/2017.
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface NeaSecurityToken {
}
