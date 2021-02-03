package net.questcraft.structuretests;

import net.questcraft.annotations.*;

import java.util.List;

@SQLNode("testTable3")
public class StructuredTestTable3 {
    @SQLPrimaryIndex
    private Long id;
    private String image;
    private Integer identifier;
    @SQLOneToMany(StructuredTestTable4.class)
    private List<StructuredTestTable4> friends;
    @SQLOneToMany(StructuredTestTable5.class)
    private List<StructuredTestTable5> blocks;

    public StructuredTestTable3(Long id, String image, Integer identifier, List<StructuredTestTable4> friends, List<StructuredTestTable5> blocks) {
        this.id = id;
        this.image = image;
        this.identifier = identifier;
        this.friends = friends;
        this.blocks = blocks;
    }

    public StructuredTestTable3() {
    }
}
