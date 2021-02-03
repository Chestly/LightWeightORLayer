package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.stmt.InnoDBStmtCreator;
import net.questcraft.stmt.StmtCreator;
import net.questcraft.structure.*;
import net.questcraft.structure.datastructure.ClassNode;
import net.questcraft.structure.datastructure.ObjectNode;
import net.questcraft.structuretests.*;
import net.questcraft.transaction.SQLTransaction;
import net.questcraft.utils.TestingManagerUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpeedTest {
    private final ORSetup.DBInformation information = TestingManagerUtils.testingDatabaseEnvironment();
    private final ORLayerUtils util = ORSetup.configureORLayerUtils(information);

    public SpeedTest() throws FatalORLayerException {
    }

    @FunctionalInterface
    private interface SpeedTestable {
        void handle() throws Exception;
    }

    private void runSpeedTest(SpeedTestable test, int iterations) {
        long start = System.currentTimeMillis();

        long totalTime = 0;

        for (int iteration = 0; iteration < iterations; iteration++) {
            long beginTime = System.currentTimeMillis();

            try {
                test.handle();
            } catch (Exception e) {
                Logger.getLogger("SpeedTest").log(Level.SEVERE, "Failed speed testing at iteration: " + iteration + " exception: " + e.getMessage());
                return;
            }

            long timeTaken = System.currentTimeMillis() - beginTime;
            System.out.println("Its taken: " + timeTaken + " millis to complete this iteration(" + iteration + 1 + "/" + iterations + ", " + (int) (((float) iteration / (float) iterations) * 100) + "%)");

            totalTime = totalTime + timeTaken;
        }

        System.out.println("The Average was: " + totalTime / iterations);
        System.out.println("The total was: " + (System.currentTimeMillis() - start));
    }

    /**
     * Query Data:
     * <p>
     * -All measurements in millis.
     * <p>
     * Iterations: 1
     * - total: 574
     * - Average: 574
     * <p>
     * Iterations: 10
     * - total: 511
     * - Average: 51
     * <p>
     * Iterations: 100
     * - total: 2236
     * - Average: 22
     * <p>
     * Iterations: 1,000
     * - total: 12566
     * - Average: 12
     * <p>
     * Iterations: 10,000
     * - total: 108380
     * - Average: 10
     * <p>
     * Iterations: 100,000
     * - total: 1076193
     * - Average: 10
     */
    @Test
    public void testQuerySpeed() {
        this.runSpeedTest(() -> util.query(StructuredTestTable2.class, "Username"), 10);
    }

    /**
     * ClassTreeNode Creation Data:
     * <p>
     * Iterations: 10
     * - Average: 5
     * - Total: 55
     * Iterations: 100
     * - Average: 1
     * - Total: 146
     * Iterations: 1,000
     * - Average: <0
     * - Total: 473
     * Iterations: 100,000
     * - Average: <0
     * - Total: 11998
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testClassTreeCreationSpeed() {
        this.runSpeedTest(() -> {
            TreeNodeGenerator<Class<StructuredTestTable2>> generator = new ClassTreeNodeGenerator<>();
            DataTreeNode<ClassNode> classTreeNode = (DataTreeNode<ClassNode>) generator.generate(StructuredTestTable2.class);
        }, 100000);
    }

    /**
     * ObjectTreeNode Creation Data:
     * <p>
     * Iterations: 10
     * - Average: 4
     * - Total: 46
     * Iterations: 100
     * - Average: 1
     * - Total: 185
     * Iterations: 1,000
     * - Average: <0
     * - Total: 612
     * Iterations: 100,000
     * - Average: <0
     * - Total: 13095
     */

    @Test
    @SuppressWarnings("unchecked")
    public void testObjectTreeCreationSpeed() {
        StructuredTestTable2 test = new StructuredTestTable2("SomethingGood", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "Value", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 15, "intNow"));
                }}));

        this.runSpeedTest(() -> {
            TreeNodeGenerator<StructuredTestTable2> generator = new ObjectTreeNodeGenerator<>();
            DataTreeNode<ObjectNode> treeNode = (DataTreeNode<ObjectNode>) generator.generate(test);
        }, 100000);
    }

    /**
     * 1 Million iterations takes about 4 seconds. Assuming it would
     * be faster outside of an IDE as there are no safe guards there.
     */
    @Test
    public void testVanillaIterations() {
        runSpeedTest(() -> {
        }, 1000000);
    }

    /**
     * Object to ClassNode Translation Data:
     * <p>
     * Iterations: 10
     * - Average: 2
     * - Total: 25
     * Iterations: 100
     * - Average: <0
     * - Total: 122
     * Iterations: 1,000
     * - Average: <0
     * - Total: 832
     * Iterations: 100,000
     * - Average: <0
     * - Total: 14491
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testObjectToClassNodeSpeed() throws FatalORLayerException {
        StructuredTestTable2 test = new StructuredTestTable2("SomethingGood", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "Value", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 15, "intNow"));
                }}));

        TreeNodeGenerator<StructuredTestTable2> generator = new ObjectTreeNodeGenerator<>();
        DataTreeNode<ObjectNode> treeNode = (DataTreeNode<ObjectNode>) generator.generate(test);

        this.runSpeedTest(((MutableDataTreeNode<?>) treeNode)::toClassNode, 100000);
    }


    /**
     * QueryStatement Creation Data:
     * <p>
     * Iterations: 10
     * - Average: 5
     * - Total: 57
     * Iterations: 100
     * - Average: 1
     * - Total: 188
     * Iterations: 1,000
     * - Average: <0 ~0.8
     * - Total: 843
     * Iterations: 100,000
     * - Average: <0 ~0.2
     * - Total: 18316
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testQueryStmtCreationSpeed() throws FatalORLayerException {
        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);

        TreeNodeGenerator<Class<StructuredTestTable2>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> classTreeNode = (DataTreeNode<ClassNode>) generator.generate(StructuredTestTable2.class);
        this.runSpeedTest(() -> {
            SQLTransaction transaction = smtCreator.getQuery("Username", classTreeNode);
        }, 100000);
    }

    /**
     * Update Statement Creation Data:
     * <p>
     * Iterations: 10
     * - Average: 109
     * - Total: 1098
     * Iterations: 100
     * - Average: 66
     * - Total: 6636
     * Iterations: 1,000
     * - Average: 63
     * - Total: 63305
     * Iterations: 100,000
     * - Not doing 100,000 as it would take ~3 hours
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateStmtCreationSpeed() throws FatalORLayerException {
        StructuredTestTable2 test = new StructuredTestTable2("SomethingGood", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "Value", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 15, "intNow"));
                }}));

        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);

        TreeNodeGenerator<StructuredTestTable2> generator = new ObjectTreeNodeGenerator<>();
        DataTreeNode<ObjectNode> treeNode = (DataTreeNode<ObjectNode>) generator.generate(test);
        this.runSpeedTest(() -> {
            SQLTransaction transaction = smtCreator.getUpdate(treeNode, "Username");
        }, 1000);
    }

    /**
     * Delete Statement Creation Data:
     * <p>
     * Iterations: 10
     * - Average: 47
     * - Total: 474
     * Iterations: 100
     * - Average: 18
     * - Total: 1873
     * Iterations: 1,000
     * - Average: 11
     * - Total: 11468
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteStmtCreationSpeed() throws FatalORLayerException {
        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);

        TreeNodeGenerator<Class<StructuredTestTable2>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> treeNode = (DataTreeNode<ClassNode>) generator.generate(StructuredTestTable2.class);
        this.runSpeedTest(() -> {
            SQLTransaction transaction = smtCreator.getDelete("Username", treeNode);
        }, 100000);
    }

    /**
     * Delete Creation Data:
     * <p>
     * Iterations: 10
     * - Average: 21
     * - Total: 218
     * Iterations: 100
     * - Average: 1
     * - Total: 154
     * Iterations: 1,000
     * - Average: <0
     * - Total: 1050
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testCreateStmtCreationSpeed() throws FatalORLayerException {
        StructuredTestTable2 test = new StructuredTestTable2("SomethingGood", "Sure bud",
                new StructuredTestTable1(10L, 20),
                new StructuredTestTable3(20L, "Testing something", 10,
                        new ArrayList<StructuredTestTable4>() {{
                            add(new StructuredTestTable4(17L, "Value", "randomValueThing"));
                        }}, new ArrayList<StructuredTestTable5>() {{
                    add(new StructuredTestTable5(23L, 15, "intNow"));
                }}));

        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);

        TreeNodeGenerator<StructuredTestTable2> generator = new ObjectTreeNodeGenerator<>();
        DataTreeNode<ObjectNode> treeNode = (DataTreeNode<ObjectNode>) generator.generate(test);
        this.runSpeedTest(() -> {
            SQLTransaction transaction = smtCreator.getCreate(treeNode);
        }, 1000);
    }
}
