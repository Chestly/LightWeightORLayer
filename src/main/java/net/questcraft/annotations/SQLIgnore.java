package net.questcraft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a class with Ignore so it will not be included in
 * SQL Persistence. Additionally Fields marked with {@code Modifiers} of
 * types {@code transient} or {@code static} will be also be ignored.
 *
 * @since 1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLIgnore {
}
