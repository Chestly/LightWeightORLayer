package net.questcraft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a Primary Key/Index for a table, TWO should NOT be present
 * in one SQLNode table, But if a second is provided the first will be
 * overwritten
 *
 * @since 1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLPrimaryIndex {
//    /**
//     * The optional Column name for the given Index. If value is not provided
//     * The index will default to the name of the field.
//     *
//     * @return The column name value
//     */
//    String value() default "";
}