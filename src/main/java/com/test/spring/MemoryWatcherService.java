package com.test.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Memory watcher
 *
 * @author Kontcur Alex (bona)
 * @since 10.11.13
 */
@Service
public class MemoryWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(MemoryWatcherService.class);

    @Value("${memory-watcher.period}")
    private long watcherPeriod;

    private AtomicBoolean threadRunFlag;

    public MemoryWatcherService() {
        threadRunFlag = new AtomicBoolean();
    }

    @PostConstruct
    protected void start() {
        threadRunFlag = new AtomicBoolean(true);
        Runnable runnable = new WatcherRunnable();
        Thread thread = new Thread(runnable, "MemoryWatcherService");
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    protected void doStop() {
        threadRunFlag.set(false);
    }

    private class WatcherRunnable implements Runnable {
        private static final long MB_CONST = 1024 * 1024;

        @Override
        public void run() {
            while (threadRunFlag.get()) {
                logger.info(createIndicatorString());
                try {
                    TimeUnit.SECONDS.sleep(watcherPeriod);
                } catch (InterruptedException e) {
                    logger.error("Error", e);
                }
            }
        }

        private String createIndicatorString() {
            long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long maxMemory = Runtime.getRuntime().maxMemory();
            int percent = (int) ((float) 100 * allocatedMemory / maxMemory);

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                builder.append(i < percent ? '#' : '_');
            }
            StringBuilder detailsBuilder = new StringBuilder();
            detailsBuilder.append(percent).append("%");
            detailsBuilder.append(" (").append(allocatedMemory / MB_CONST).append(" MB)");
            detailsBuilder.append(", Max: ").append(maxMemory / MB_CONST).append(" MB");

            return builder.append(" ").append(detailsBuilder).toString();
        }
    }
}