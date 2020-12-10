package io.seata.core.context;

import io.netty.util.concurrent.FastThreadLocal;
import io.seata.common.loader.LoadLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Fast Thread local context core.
 *
 * @author ph3636
 */
@LoadLevel(name = "FastThreadLocalContextCore", order = Integer.MIN_VALUE + 1)
public class FastThreadLocalContextCore implements ContextCore {

    private FastThreadLocal<Map<String, String>> fastThreadLocal = new FastThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }
    };

    @Override
    public String put(String key, String value) {
        return fastThreadLocal.get().put(key, value);
    }

    @Override
    public String get(String key) {
        return fastThreadLocal.get().get(key);
    }

    @Override
    public String remove(String key) {
        return fastThreadLocal.get().remove(key);
    }

    @Override
    public Map<String, String> entries() {
        return fastThreadLocal.get();
    }
}