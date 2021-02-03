package net.questcraft.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This marks a class as being the child of a one-to-many relation
 * ship. In this system and in general it does from a design point perspective
 * to have a class be a child and a non-direct(one-to-many) relationship child.
 *
 * To combat this the @OneToManyRelationshipChild annotation has been created
 * which should be used to mark all children(or a one-to-many relationship).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OneToManyRelationshipChild {

}
