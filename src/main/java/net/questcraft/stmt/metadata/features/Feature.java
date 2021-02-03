package net.questcraft.stmt.metadata.features;

import net.questcraft.stmt.metadata.components.StatementComponent;

public interface Feature {
    String HOLDER = StatementComponent.FEATURE_HOLDER;

    /**
     * {@code Feature#parse()} Should create a SQL representation
     * of the feature.
     *
     * @return A parsed representation of this feature
     */
    String parse();

    /**
     * {@code Feature#dataValues()} Should return all values that need to be
     * parameterized by JDBC to disallow SQL-injections. This should NOT
     * be values like a column name etc.
     *
     * @return Values to be put in place by JDBC.
     */
    Object[] dataValues();
}
