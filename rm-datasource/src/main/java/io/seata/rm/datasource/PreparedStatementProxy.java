package io.seata.rm.datasource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import io.seata.rm.datasource.exec.ExecuteTemplate;
import io.seata.sqlparser.ParametersHolder;

/**
 * The type Prepared statement proxy.
 *
 * @author sharajava
 */
public class PreparedStatementProxy extends AbstractPreparedStatementProxy
    implements PreparedStatement, ParametersHolder {

    @Override
    public ArrayList<Object>[] getParameters() {
        return parameters;
    }

    /**
     * Instantiates a new Prepared statement proxy.
     *
     * @param connectionProxy the connection proxy
     * @param targetStatement the target statement
     * @param targetSQL       the target sql
     * @throws SQLException the sql exception
     */
    public PreparedStatementProxy(AbstractConnectionProxy connectionProxy, PreparedStatement targetStatement,
                                  String targetSQL) throws SQLException {
        super(connectionProxy, targetStatement, targetSQL);
    }

    @Override
    public boolean execute() throws SQLException {
        return ExecuteTemplate.execute(this, (statement, args) -> statement.execute());
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return ExecuteTemplate.execute(this, (statement, args) -> statement.executeQuery());
    }

    @Override
    public int executeUpdate() throws SQLException {
        return ExecuteTemplate.execute(this, (statement, args) -> statement.executeUpdate());
    }
}
