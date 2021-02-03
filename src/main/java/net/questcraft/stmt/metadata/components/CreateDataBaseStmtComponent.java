package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class CreateDataBaseStmtComponent extends StatementComponent {
    //                                              DBNameFeature
    private final String stmt = "CREATE DATABASE" + FEATURE_HOLDER;

    public static final int IDENTIFIER = 18;

    public CreateDataBaseStmtComponent(CreateDBStmtBuilder builder) {
        super(builder);
    }

    @Override
    public String parse() {
        return replaceHolder(stmt, true);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class CreateDBStmtBuilder extends StatementComponent.StmtComponentBuilder<CreateDBStmtBuilder> {
        @Override
        public CreateDBStmtBuilder self() {
            return this;
        }

        @Override
        public CreateDataBaseStmtComponent build() throws FatalORLayerException {
            return new CreateDataBaseStmtComponent(this);
        }
    }
}
