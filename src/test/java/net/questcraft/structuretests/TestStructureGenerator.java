package net.questcraft.structuretests;

import net.questcraft.annotations.SQLNode;
import net.questcraft.annotations.SQLOneToMany;
import net.questcraft.structure.ClassTreeNodeGenerator;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.ObjectTreeNodeGenerator;
import net.questcraft.structure.TreeNodeGenerator;
import net.questcraft.structure.aliasstructure.AliasedNode;
import net.questcraft.structure.aliasstructure.AliasedNodeGenerator;
import net.questcraft.structure.datastructure.*;
import net.questcraft.exceptions.FatalORLayerException;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestStructureGenerator {
    @Test
    public void testStructureGeneration() throws FatalORLayerException {
//        JavaStructureGenerator<StructuredTestTable2> generator = new ObjectStructureGenerator<>();
//        ObjectNode structuredTestRootTreeNode = (ObjectNode) generator.create(new StructuredTestTable2("Username", "okBUD", new StructuredTestTable3(12L, "image", 100, new ArrayList(){{
//            add(new StructuredTestTable4(10L, "", ""));
//        }}, new ArrayList<>()), new StructuredTestTable1(88L, 16)));
//
//        System.out.println(structuredTestRootTreeNode);
    }

    @Test
    public void isInstantiated() {
        StructuredTestTable2 testTable2 = new StructuredTestTable2("SomethingGood", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "Value", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 15, "intNow"));
                }}));

        System.out.println(this.isInstantiated(testTable2));
    }

    private boolean isInstantiated(Object object) {
        for (Field declaredField : object.getClass().getDeclaredFields()) {
            try {
                if (TreeNodeGenerator.usable(declaredField)) {
                    final Object o = this.retrieveValue(declaredField, object);
                    if (declaredField.getType().isAnnotationPresent(SQLNode.class) && o != null) {
                        if (this.isInstantiated(o)) return true;
                    } else if (declaredField.isAnnotationPresent(SQLOneToMany.class) && o != null) {
                        Collection<?> collection = (Collection<?>) o;
                        if (!collection.isEmpty() && !declaredField.isAnnotationPresent(SQLOneToMany.class) ) return true;
                    } else if (o != null) return true;
                }
            } catch (FatalORLayerException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage());
                return false;
            }
        }
        return false;
    }

    private <K> K retrieveValue(Field field, K obj) throws FatalORLayerException {
        try {
            if (!field.isAccessible()) field.setAccessible(true);
            return (K) field.get(obj);
        } catch (IllegalAccessException e) {
            Logger.getLogger("ClassStructureGenerator").log(Level.SEVERE, "Unable to obtain Value from field '" + field.getName() + "' In Class '" + obj.getClass().toString() + "'");
            throw new FatalORLayerException("Failed to access accessible field '" + e.getMessage() + "'");
        }
    }

//    @Test
//    public void testClassGeneration() throws FatalORLayerException {
//        JavaStructureGenerator<Class<StructuredTestTable2>> generator = new ClassStructureGenerator<>();
//        ClassNode structuredTestRootTreeNode = (ClassNode) generator.create(StructuredTestTable2.class);
//
//        System.out.println(structuredTestRootTreeNode);
//    }
//    new StructuredTestRoot(5, "HEyEy", new StructuredSubClass("Im A sUb", 5, 24), new StructuredTestRoot.StructuredInnerClass(100, "One Hundred", new StructuredSubClass("OK THING", 5, 10)))

    @Test
    public void testFirstReplace() {
        String str = "Hello there how are you?";

        System.out.println(str.replaceFirst("Hello", "GoodBye"));
    }

//    @Test
//    public void testStructuredIteration() throws FatalORLayerException {
//        JavaStructureGenerator<Class<StructuredTestTable1>> generator = new ClassStructureGenerator();
//        DataNode<?> structuredTestRootTreeNode = generator.create(StructuredTestTable1.class);
//
//        for (DataNode<?> treeNodes : structuredTestRootTreeNode) {
//            System.out.println("Yay here: " + treeNodes.getClass().toGenericString());
//        }
//    }

//    @Test
//    public void testAbsolutePath() throws FatalORLayerException {
//        JavaStructureGenerator<Class<StructuredTestTable2>> generator = new ClassStructureGenerator<>();
//        JavaStructureGenerator<Class<StructuredTestTable5>> structuredTestTable5ClassStructureGenerator = new ClassStructureGenerator<>();
//        DataNode<?> structuredTestRootTreeNode = generator.create(StructuredTestTable2.class);
//
//        JavaStructureGenerator.absolutePath((ClassNode) structuredTestRootTreeNode, (ClassNode) structuredTestRootTreeNode);
//    }
//
//    @Test
//    public void testAliasGeneration() throws FatalORLayerException {
//        JavaStructureGenerator<Class<StructuredTestTable2>> generator = new ClassStructureGenerator<>();
//        ClassNode structuredTestRootTreeNode = (ClassNode) generator.create(StructuredTestTable2.class);
//
//        AliasedNode aliasedTreeNode = new AbsolutePathAliasGenerator().generate(structuredTestRootTreeNode);
//        aliasedTreeNode.descendantOf("testTable2");
//        System.out.println("Please");
//    }
    @Test
    public void testDataStructure() throws FatalORLayerException {
        ObjectTreeNodeGenerator<StructuredTestTable2> generator = new ObjectTreeNodeGenerator<>();
        StructuredTestTable2 testTable2 = new StructuredTestTable2("SomethingGood", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "Value", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 15, "intNow"));
                }}));
        final DataTreeNode<ObjectNode> generate = generator.generate(testTable2);
        System.out.println("Yes i did this!");
    }

    @Test
    public void testHash() {
        long value = AliasedNodeGenerator.hash(10000L, "Hello there");
        System.out.println(value);
    }

    @Test
    public void testStream() {
        List<String> list = new ArrayList<>();
        list.add("hi");
        list.add("hello");
        list.add("you there");
        list.add("yes i mean u");
        list.add("GIMME");

        System.out.println(list.stream().noneMatch((string) -> string.equals("ayya")));

    }
}
