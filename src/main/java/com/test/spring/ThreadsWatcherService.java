package com.test.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.collections.CollectionUtils.subtract;

/**
 * ThreadsWatcherService - monitors forked/removed threads within current JVM
 *
 * @author Kontcur Alex (bona)
 * @since 10.11.13
 */
@Service
public class ThreadsWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(ThreadsWatcherService.class);

    private List<Thread> lastList;

    @Value("${threads-watcher.period}")
    private long watcherPeriod;

    private AtomicBoolean threadRunFlag;

    public ThreadsWatcherService() {
        threadRunFlag = new AtomicBoolean();
    }

    @PostConstruct
    protected void start() {
        threadRunFlag.set(true);
        Runnable runnable = new WatcherRunnable();
        Thread thread = new Thread(runnable, "ThreadsWatcherService");
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    protected void stop() {
        threadRunFlag.set(false);
    }

    private class WatcherRunnable implements Runnable {
        @Override
        public void run() {
            while (threadRunFlag.get()) {
                logger.info("==================DUMP THREADS==================");
                List<Thread> list = dumpThreads();
                logger.info("================================================");
                dumpDifferences(list);
                try {
                    TimeUnit.SECONDS.sleep(watcherPeriod);
                } catch (InterruptedException e) {
                    logger.error("Error", e);
                }
            }
        }

        private List<Thread> dumpThreads() {
            List<Thread> list = new ArrayList<>();

            try {
                ThreadGroup rootThreadGroop = Thread.currentThread().getThreadGroup();
                ThreadGroup parent = rootThreadGroop.getParent();

                while (parent != null) {
                    rootThreadGroop = parent;
                    parent = rootThreadGroop.getParent();
                }

                printGroupInfo(rootThreadGroop, "", list);

                if (rootThreadGroop != null) {
                    logger.info("Number of the ThreadGroups = " + rootThreadGroop.activeGroupCount());
                    logger.info("Number of Threads = " + rootThreadGroop.activeCount());
                }
            } catch (SecurityException sec) {
                logger.error("Error", sec);
            }

            return list;
        }

        private void printGroupInfo(ThreadGroup g, String ident, List<Thread> list) {
            if (g == null) {
                return;
            }
            int numThreads = g.activeCount();
            int numGroups = g.activeGroupCount();

            Thread[] threads = new Thread[numThreads];
            ThreadGroup[] groups = new ThreadGroup[numGroups];

            g.enumerate(threads, false);
            g.enumerate(groups, false);

            logger.info(ident + "ThreadGroup: " + '"' + g.getName() + '"' + ";" +
                " Max Priority: " + g.getMaxPriority() + ";" +
                (g.isDaemon() ? " Daemon" : ""));

            for (int i = 0; i < numThreads; i++) {
                printThreadInfo(threads[i], ident + "   ");
                list.add(threads[i]);
            }

            for (int i = 0; i < numGroups; i++) {
                printGroupInfo(groups[i], ident + "   ", list);
            }
        }

        private void printThreadInfo(Thread t, String ident) {
            if (t == null) {
                return;
            }
            logger.info(ident + "Thread: " + '"' + t.getName() + '"' + ";" +
                " Class: " + t.getClass() + ";" +
                " Priority: " + t.getPriority() + ";" +
                (t.isDaemon() ? " Daemon" : "") + ";" +
                (t.isAlive() ? "" : " Not Alive"));

        }

        @SuppressWarnings("unchecked")
        private void dumpDifferences(List<Thread> list) {

            if (lastList != null) {
                Collection<Thread> newed = subtract(list, lastList);
                if (!newed.isEmpty()) {
                    logger.info("===================NEW THREADS===================");
                    for (Thread thread : newed) {
                        printThreadInfo(thread, "");
                    }
                    logger.info("===================          ===================");
                }
                Collection<Thread> deleted = subtract(lastList, list);
                if (!deleted.isEmpty()) {
                    logger.info("===================DELETED THREADS===============");
                    for (Thread thread : deleted) {
                        printThreadInfo(thread, "");
                    }
                    logger.info("===================          ===================");
                }
            }
            lastList = list;
        }
    }
}