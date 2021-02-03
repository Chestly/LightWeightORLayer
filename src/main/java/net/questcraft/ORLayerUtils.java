package net.questcraft;

import net.questcraft.exceptions.FatalORLayerException;
import net.questcraft.exceptions.ORLayerException;
import net.questcraft.stmt.InnoDBStmtCreator;
import net.questcraft.structure.ClassTreeNodeGenerator;
import net.questcraft.structure.DataTreeNode;
import net.questcraft.structure.ObjectTreeNodeGenerator;
import net.questcraft.structure.TreeNodeGenerator;
import net.questcraft.structure.datastructure.*;
import net.questcraft.stmt.StmtCreator;
import net.questcraft.stmt.metadata.SQLStmtMetaData;
import net.questcraft.stmt.metadata.StatementMetaData;
import net.questcraft.stmt.metadata.components.PureSQLComponent;
import net.questcraft.stmt.metadata.features.PureSQLFeature;
import net.questcraft.transaction.InnoDBTransaction;
import net.questcraft.transaction.SQLTransaction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;

import static net.questcraft.transaction.SQLTransaction.SQLTransactionResults.TransactionResultType.SUCCESS;

public class ORLayerUtils {
    private final ORSetup.DBInformation information;

    protected ORLayerUtils(ORSetup.DBInformation information) {
        this.information = information;
    }

    /**
     * Takes the specified Objects and creates it in a SQL database. Note,
     * Your object has to follow the specified Annotation gu
     *
     * @param object The object to persist
     * @return boolean outcome(will be true or an error will be thrown)
     * @throws FatalORLayerException If it fails to persist
     */
    @SuppressWarnings("unchecked")
    public <T> boolean create(T object) throws FatalORLayerException {
        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);
        TreeNodeGenerator<T> generator = new ObjectTreeNodeGenerator<>();
        DataTreeNode<ObjectNode> treeNode = (DataTreeNode<ObjectNode>) generator.generate(object);

        SQLTransaction transaction = smtCreator.getCreate(treeNode);

        try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {
            if (execute.resultType().equals(SUCCESS)) return true;
            else throw execute.exception();
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    /**
     * Updates the given object targeting the primary index, Primary indices are
     * Immutable and will never change unless direct user action is taken
     *
     * @param object Object to update to
     * @return success of updating
     * @throws FatalORLayerException If persistence fails(will be rolled back)
     */
    @SuppressWarnings("unchecked")
    public <T> boolean update(T object) throws FatalORLayerException {
        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);
        TreeNodeGenerator<T> generator = new ObjectTreeNodeGenerator<>();
        DataTreeNode<ObjectNode> treeNode = (DataTreeNode<ObjectNode>) generator.generate(object);

        SQLTransaction transaction = smtCreator.getUpdate(treeNode, treeNode.getData().getPrimaryIndex().getValue().toString());

        try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {

            if (execute.resultType().equals(SUCCESS)) return true;
            else throw execute.exception();
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    /**
     * Queries the Data base and will hydrate the Given class to an object, If
     * no value is found the Object will have null member variables
     *
     * @param cls The class to hydrate from
     * @param key The primary key of the top root
     * @param <T> The genericized type that it will hydrate too
     * @return The hydrated object
     * @throws FatalORLayerException If a error is caught
     * @since 1.4 does not rely on Getters and Setters and directly modifies Any declared
     * and not Ignored fields
     */
    @Nullable
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <T> T query(Class<T> cls, Object key) throws FatalORLayerException {
        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);
        TreeNodeGenerator<Class<T>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> classTreeNode = (DataTreeNode<ClassNode>) generator.generate(cls);

        SQLTransaction transaction = smtCreator.getQuery(key, classTreeNode);

        try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {
            if (execute.resultType().equals(SUCCESS)) {
                TreeNodeGenerator<T> objectSG = new ObjectTreeNodeGenerator<>();
                return objectSG.fromSQL(cls, classTreeNode, execute.resultSet());
            } else {
                throw execute.exception();
            }
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    /**
     * Deletes the full tree of data from the given root key
     *
     * @param cls The tree to delete through
     * @param key The Root key to start deleting at
     * @param <T> The genericized type used internally
     * @return boolean of success
     * @throws FatalORLayerException if an error is caught
     */
    @SuppressWarnings("unchecked")
    public <T> boolean delete(Class<T> cls, String key) throws FatalORLayerException {
        StmtCreator smtCreator = new InnoDBStmtCreator(this.information);
        TreeNodeGenerator<Class<T>> generator = new ClassTreeNodeGenerator<>();
        DataTreeNode<ClassNode> treeNode = (DataTreeNode<ClassNode>) generator.generate(cls);

        SQLTransaction transaction = smtCreator.getDelete(key, treeNode);

        try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {

            if (execute.resultType().equals(SUCCESS)) return true;
            else throw execute.exception();
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    public ResultSet querySQL(String stmt, Object... values) throws ORLayerException {
        SQLTransaction transaction = new InnoDBTransaction(this.information)
                .addStmt(new SQLStmtMetaData(StatementMetaData.StatementMetaType.QUERY).
                        add(new PureSQLComponent.PureSQLStmtComponentBuilder().feature(new PureSQLFeature(stmt, values)).build()));

        try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {
            if (execute.resultType().equals(SUCCESS)) return execute.resultSet();
            else try {
                throw execute.exception();
            } catch (Exception e) {
                throw new FatalORLayerException(e);
            }
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }

    public boolean modifySQL(String stmt, Object... values) throws ORLayerException {
        SQLTransaction transaction = new InnoDBTransaction(this.information)
                .addStmt(new SQLStmtMetaData(StatementMetaData.StatementMetaType.MODIFY).
                        add(new PureSQLComponent.PureSQLStmtComponentBuilder().feature(new PureSQLFeature(stmt, values)).build()));

        try (SQLTransaction.SQLTransactionResults execute = transaction.execute()) {
            if (execute.resultType().equals(SUCCESS)) return true;
            else throw new FatalORLayerException(execute.exception());
        } catch (Exception e) {
            throw new FatalORLayerException(e);
        }
    }
}
