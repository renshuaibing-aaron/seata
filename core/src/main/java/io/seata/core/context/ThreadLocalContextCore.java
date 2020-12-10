package io.seata.core.context;

import java.util.HashMap;
import java.util.Map;

import io.seata.common.loader.LoadLevel;

/**
 * The type Thread local context core.
 *
 * @author slievrly
 */
@LoadLevel(name = "ThreadLocalContextCore", order = Integer.MIN_VALUE)
public class ThreadLocalContextCore implements ContextCore {

    private ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<String, String>();
        }

    };

    @Override
    public String put(String key, String value) {
        return threadLocal.get().put(key, value);
    }

    @Override
    public String get(String key) {
        return threadLocal.get().get(key);
    }

    @Override
    public String remove(String key) {
        return threadLocal.get().remove(key);
    }

    @Override
    public Map<String, String> entries() {
        return threadLocal.get();
    }
}
