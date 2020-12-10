package io.seata.core.context;

import java.util.Optional;

import io.seata.common.loader.EnhancedServiceLoader;

/**
 * The type Context core loader.
 *
 * @author sharajava
 */
public class ContextCoreLoader {

    private ContextCoreLoader() {

    }

    private static class ContextCoreHolder {
        private static final ContextCore INSTANCE = Optional.ofNullable(EnhancedServiceLoader.load(ContextCore.class)).orElse(new ThreadLocalContextCore());
    }

    /**
     * Load context core.
     *
     * @return the context core
     */
    public static ContextCore load() {
        return ContextCoreHolder.INSTANCE;
    }

}
