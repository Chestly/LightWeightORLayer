package net.questcraft.stmt.metadata.features;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

public class TableColumnDataFeature implements Feature {
    private final List<ColumnModifier> modifiers;

    public TableColumnDataFeature(List<ColumnModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public TableColumnDataFeature() {
        this.modifiers = new ArrayList<>();
    }

    public TableColumnDataFeature addModifier(ColumnModifier modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    @Override
    public String parse() {
        return Joiner.on(" ").join(modifiers);
    }

    @Override
    public Object[] dataValues() {
        return new Object[0];
    }

    public enum ColumnModifier {
        UNIQUE("UNIQUE"),
        NOT_NULL("NOT NULL"),
        INCREMENT("AUTO_INCREMENT"),
        PRIMARY("PRIMARY KEY")
        ;

        //DEFAULT not included in this list as Default values will not be set to columns
        //This can instead be done in the manifest.sql

        private final String modifier;

        ColumnModifier(String modifier) {
            this.modifier = modifier;
        }

        @Override
        public String toString() {
            return modifier;
        }
    }
}
