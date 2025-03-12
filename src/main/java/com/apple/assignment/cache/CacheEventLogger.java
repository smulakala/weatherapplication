package com.apple.assignment.cache;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

/**
 * Captures the cache events and just logs it.
 */
@Slf4j
public class CacheEventLogger  implements CacheEventListener<Object, Object> {
    /**
     * Takes event and logs it.
     * @param cacheEvent
     */
    @Override
    public void onEvent(
            CacheEvent<? extends Object, ? extends Object> cacheEvent) {
        log.info("Cache Event Type : {} : {} : {} : {}",
                cacheEvent.getType().name(),cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
    }
}
