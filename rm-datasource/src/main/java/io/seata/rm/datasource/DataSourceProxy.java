package io.seata.rm.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.seata.common.thread.NamedThreadFactory;
import io.seata.config.ConfigurationFactory;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.model.BranchType;
import io.seata.core.model.Resource;
import io.seata.rm.DefaultResourceManager;
import io.seata.rm.datasource.sql.struct.TableMetaCacheFactory;
import io.seata.rm.datasource.util.JdbcUtils;

import static io.seata.core.constants.DefaultValues.DEFAULT_CLIENT_TABLE_META_CHECK_ENABLE;

/**
 * The type Data source proxy.
 *
 * 数据源代理 牛逼的方法
 * @author sharajava
 */
public class DataSourceProxy extends AbstractDataSourceProxy implements Resource {

    private String resourceGroupId;

    private static final String DEFAULT_RESOURCE_GROUP_ID = "DEFAULT";

    private String jdbcUrl;

    private String dbType;

    /**
     * Enable the table meta checker
     */
    private static boolean ENABLE_TABLE_META_CHECKER_ENABLE = ConfigurationFactory.getInstance().getBoolean(
        ConfigurationKeys.CLIENT_TABLE_META_CHECK_ENABLE, DEFAULT_CLIENT_TABLE_META_CHECK_ENABLE);

    /**
     * Table meta checker interval
     */
    private static final long TABLE_META_CHECKER_INTERVAL = 60000L;


    private final ScheduledExecutorService tableMetaExcutor = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("tableMetaChecker", 1, true));
    /**
     * Instantiates a new Data source proxy.
     *
     * @param targetDataSource the target data source
     */
    public DataSourceProxy(DataSource targetDataSource) {
        this(targetDataSource, DEFAULT_RESOURCE_GROUP_ID);
    }

    /**
     * Instantiates a new Data source proxy.
     *
     * @param targetDataSource the target data source
     * @param resourceGroupId  the resource group id
     */
    public DataSourceProxy(DataSource targetDataSource, String resourceGroupId) {
        super(targetDataSource);
        init(targetDataSource, resourceGroupId);
    }

    private void init(DataSource dataSource, String resourceGroupId) {

        //资源组ID 如果初始化的时候构造方法没有传 会使用"default"这个默认值
        this.resourceGroupId = resourceGroupId;

        try (Connection connection = dataSource.getConnection()) {
            //根据原始数据源得到JDBC连接和数据库类型
            jdbcUrl = connection.getMetaData().getURL();
            dbType = JdbcUtils.getDbType(jdbcUrl);
        } catch (SQLException e) {
            throw new IllegalStateException("can not init dataSource", e);
        }

        //todo  注册资源 这个类实现了Resource接口
        DefaultResourceManager.get().registerResource(this);

        //如果配置开关打开，会定时线程池不断更新表的元数据信息
        if (ENABLE_TABLE_META_CHECKER_ENABLE) {
            tableMetaExcutor.scheduleAtFixedRate(() -> {
                try (Connection connection = dataSource.getConnection()) {
                    TableMetaCacheFactory.getTableMetaCache(DataSourceProxy.this.getDbType())
                        .refresh(connection, DataSourceProxy.this.getResourceId());
                } catch (Exception ignore) {
                }
            }, 0, TABLE_META_CHECKER_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Gets plain connection.
     *
     * @return the plain connection
     * @throws SQLException the sql exception
     */
    public Connection getPlainConnection() throws SQLException {
        return targetDataSource.getConnection();
    }

    /**
     * Gets db type.
     *
     * @return the db type
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * 注意这个地方获取
     * 返回一个ConnectionProxy，而不是原生的Connection
     * @return
     * @throws SQLException
     */
    @Override
    public ConnectionProxy getConnection() throws SQLException {
        Connection targetConnection = targetDataSource.getConnection();
        return new ConnectionProxy(this, targetConnection);
    }

    @Override
    public ConnectionProxy getConnection(String username, String password) throws SQLException {
        Connection targetConnection = targetDataSource.getConnection(username, password);
        return new ConnectionProxy(this, targetConnection);
    }

    @Override
    public String getResourceGroupId() {
        return resourceGroupId;
    }

    @Override
    public String getResourceId() {
        if (jdbcUrl.contains("?")) {
            return jdbcUrl.substring(0, jdbcUrl.indexOf('?'));
        } else {
            return jdbcUrl;
        }
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.AT;
    }
}
