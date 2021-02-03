package net.questcraft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to give an {@code Field} a SQL Column name that is
 * something different then its Field name.
 *
 * @since 1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLColumnName {
    /**
     * @return The provided column name
     */
    String value();
}
