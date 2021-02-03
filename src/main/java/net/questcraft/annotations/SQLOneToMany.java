package net.questcraft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For any One to many relationships you MUST annotate the given field with this annotation.
 *
 * Type must be of type {@code Collection}
 *
 * @since 1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SQLOneToMany {
    /**
     * The class of the relationship
     *
     * @return The specified class.
     */
    Class<?> value();
}
