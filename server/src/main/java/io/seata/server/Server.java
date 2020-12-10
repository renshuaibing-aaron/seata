package io.seata.server;

import io.seata.common.XID;
import io.seata.common.thread.NamedThreadFactory;
import io.seata.common.util.NetUtil;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.rpc.netty.RpcServer;
import io.seata.core.rpc.netty.ShutdownHook;
import io.seata.server.coordinator.DefaultCoordinator;
import io.seata.server.metrics.MetricsManager;
import io.seata.server.session.SessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The type Server.
 *
 * @author slievrly
 */
public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final int MIN_SERVER_POOL_SIZE = 50;
    private static final int MAX_SERVER_POOL_SIZE = 500;
    private static final int MAX_TASK_QUEUE_SIZE = 20000;
    private static final int KEEP_ALIVE_TIME = 500;
    private static final ThreadPoolExecutor WORKING_THREADS = new ThreadPoolExecutor(MIN_SERVER_POOL_SIZE,
        MAX_SERVER_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(MAX_TASK_QUEUE_SIZE),
        new NamedThreadFactory("ServerHandlerThread", MAX_SERVER_POOL_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        //initialize the parameter parser
        //Note that the parameter parser should always be the first line to execute.
        //Because, here we need to parse the parameters needed for startup.
        //解析传入的参数配置
        ParameterParser parameterParser = new ParameterParser(args);

        //initialize the metrics
        //初始化metrics
        MetricsManager.get().init();

        //设置存储模式，支持文件、DB
        System.setProperty(ConfigurationKeys.STORE_MODE, parameterParser.getStoreMode());

        //初始化一个RpcServer，其实就是一个netty的server，但并不马上start
        RpcServer rpcServer = new RpcServer(WORKING_THREADS);
        //server port
        //设置端口
        rpcServer.setListenPort(parameterParser.getPort());
        //初始化UUID生成器
        UUIDGenerator.init(parameterParser.getServerNode());
        //log store mode : file, db
        //初始化Session管理器
        SessionHolder.init(parameterParser.getStoreMode());
        //初始化协调者模块
        DefaultCoordinator coordinator = new DefaultCoordinator(rpcServer);
        //初始化协调者
        coordinator.init();
        //协调者作为handler设置到netty server中
        rpcServer.setHandler(coordinator);
        // register ShutdownHook
        //添加ShutdownHook
        ShutdownHook.getInstance().addDisposable(coordinator);
        ShutdownHook.getInstance().addDisposable(rpcServer);

        //127.0.0.1 and 0.0.0.0 are not valid here.
        //127.0.0.1 和 0.0.0.0 算非法IP.此处设置Server的IP
        if (NetUtil.isValidIp(parameterParser.getHost(), false)) {
            XID.setIpAddress(parameterParser.getHost());
        } else {
            XID.setIpAddress(NetUtil.getLocalIp());
        }
        XID.setPort(rpcServer.getListenPort());

        try {
            //启动Server
            rpcServer.init();
        } catch (Throwable e) {
            LOGGER.error("rpcServer init error:{}", e.getMessage(), e);
            System.exit(-1);
        }

        System.exit(0);
    }
}
