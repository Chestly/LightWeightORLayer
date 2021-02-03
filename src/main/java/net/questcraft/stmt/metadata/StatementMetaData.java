package net.questcraft.stmt.metadata;


import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.metadata.components.StatementComponent;

import java.util.List;

public interface StatementMetaData {
    enum StatementMetaType {QUERY, MODIFY}

    StatementMetaData add(StatementComponent component);

    String parse();

   List<Object> values();

   StatementMetaType getType();

   boolean equals(Object o);
}
