package net.questcraft.stmt.metadata.components;

import net.questcraft.exceptions.FatalORLayerException;

public class UpdateStmtComponent extends StatementComponent {
    public static final int IDENTIFIER = 5;

    private final String stmt = "UPDATE" + FEATURE_HOLDER;

    public UpdateStmtComponent(StmtComponentBuilder<?> builder) {
        super(builder);
    }

    @Override
    public String parse() {
        return replaceHolder(this.stmt, true);
    }

    @Override
    public int identifier() {
        return IDENTIFIER;
    }

    public static class UpdateStmtBuilder extends StatementComponent.StmtComponentBuilder<UpdateStmtBuilder> {
        @Override
        public UpdateStmtBuilder self() {
            return this;
        }

        @Override
        public UpdateStmtComponent build() throws FatalORLayerException {
            if (this.assertNull()) throw new FatalORLayerException("Builder member variables must be instantiated");
            return new UpdateStmtComponent(this);
        }
    }
}
