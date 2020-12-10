package io.seata.integration.http;

import io.seata.core.context.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Springmvc Intercepter.
 *
 * @author wangxb
 */
public class TransactionPropagationIntercepter extends HandlerInterceptorAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionPropagationIntercepter.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String xid = RootContext.getXID();
        String rpcXid = request.getHeader(RootContext.KEY_XID);

        //关键的地方 分布式事务在传递过程中 把xid需要传递过来
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("xid in RootContext[{}] xid in HttpContext[{}]", xid, rpcXid);
        }
        if (rpcXid != null) {
            RootContext.bind(rpcXid);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("bind[{}] to RootContext", rpcXid);
            }
        }


        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        XidResource.cleanXid(request.getHeader(RootContext.KEY_XID));
    }


}
