package io.seata.rm.datasource.undo;

import io.seata.sqlparser.SQLType;
import io.seata.rm.datasource.sql.struct.TableMeta;
import io.seata.rm.datasource.sql.struct.TableRecords;

/**
 * The type Sql undo log.
 *
 * @author sharajava
 */
public class SQLUndoLog {

    private SQLType sqlType;

    private String tableName;

    private TableRecords beforeImage;

    private TableRecords afterImage;

    /**
     * Sets table meta.
     *
     * @param tableMeta the table meta
     */
    public void setTableMeta(TableMeta tableMeta) {
        if (beforeImage != null) {
            beforeImage.setTableMeta(tableMeta);
        }
        if (afterImage != null) {
            afterImage.setTableMeta(tableMeta);
        }
    }

    /**
     * Gets sql type.
     *
     * @return the sql type
     */
    public SQLType getSqlType() {
        return sqlType;
    }

    /**
     * Sets sql type.
     *
     * @param sqlType the sql type
     */
    public void setSqlType(SQLType sqlType) {
        this.sqlType = sqlType;
    }

    /**
     * Gets table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets table name.
     *
     * @param tableName the table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets before image.
     *
     * @return the before image
     */
    public TableRecords getBeforeImage() {
        return beforeImage;
    }

    /**
     * Sets before image.
     *
     * @param beforeImage the before image
     */
    public void setBeforeImage(TableRecords beforeImage) {
        this.beforeImage = beforeImage;
    }

    /**
     * Gets after image.
     *
     * @return the after image
     */
    public TableRecords getAfterImage() {
        return afterImage;
    }

    /**
     * Sets after image.
     *
     * @param afterImage the after image
     */
    public void setAfterImage(TableRecords afterImage) {
        this.afterImage = afterImage;
    }
}
