package net.questcraft.structuretests;

import net.questcraft.annotations.*;

@SQLNode("testTable4")
@OneToManyRelationshipChild
public class StructuredTestTable4 {
    @SQLPrimaryIndex
    private Long id;
    @SQLChildRelationalColumn
    private String value;
    @SQLColumnName("third")
    private String randomValueThing;

    public StructuredTestTable4(Long id, String value, String randomValueThing) {
        this.id = id;
        this.value = value;
        this.randomValueThing = randomValueThing;
    }

    public StructuredTestTable4() {
    }
}
