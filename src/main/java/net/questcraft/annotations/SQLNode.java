package net.questcraft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a Class that can be inputted into a SQL Table. All
 * Classes used as Member variable and the class root should be marked with
 * the {@code SQLNode} Annotation.
 *
 * @since 1.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SQLNode {
    //TODO create another annotation or include it as part of this to signify a child as a MUST(Inner join) or to have it be optional(null)(Left Join)

    /**
     * The table to be used for the given {@code SQLNode}
     *
     * @return The given table
     */
    String value();
}
