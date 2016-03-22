package com.test.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * SpringWrapperApplication
 *
 * @author Kontcur Alex (bona)
 * @since 10.11.13
 */
public class SpringWrapperApplication {

    private static final String DEFAULT_CONTEXT_ROOT = "classpath*:beans.xml";

    private static final Logger logger = LoggerFactory.getLogger(SpringWrapperApplication.class);

    private ClassPathXmlApplicationContext context;

    public int stop(int code) {
        context.close();
        return code;
    }

    public static void main(String... args) {
        new SpringWrapperApplication().start(args);
    }

    public Integer start(String... args) {
        try {
            String contextRoot = null;
            List<String> profiles = new ArrayList<>();

            if (args.length > 1) {
                for (String arg : args) {
                    if (arg.startsWith("-C")) {
                        if (contextRoot == null) {
                            contextRoot = arg.substring(2);
                        } else {
                            logger.error(String.format("ERROR: Detected multiple context roots: [%s]", arg));
                            return -1;
                        }
                    } else if (arg.startsWith("-P")) {
                        profiles.add(arg.substring(2));
                    } else {
                        logger.error(String.format("ERROR: Wrong parameter specified: [%s]", arg));
                        return -1;
                    }
                }
            } else if (args.length == 1) {
                // allow to run with 1 parameter
                String arg = args[0];
                if (arg.startsWith("-C")) {
                    contextRoot = arg.substring(2);
                } else {
                    contextRoot = arg;
                }
            }

            if (contextRoot == null) {
                contextRoot = DEFAULT_CONTEXT_ROOT;
            }
            logger.info(String.format("Starting application from: [%s]", contextRoot));
            ClassPathXmlApplicationContext context;
            if (profiles.isEmpty()) {
                context = new ClassPathXmlApplicationContext(new String[]{contextRoot});
            } else {
                logger.info(String.format("Using specified profiles: %s", profiles));
                context = new ClassPathXmlApplicationContext(new String[]{contextRoot}, false);
                context.getEnvironment().setActiveProfiles(profiles.toArray(new String[profiles.size()]));
                context.refresh();
            }
            context.registerShutdownHook();
            this.context = context;

            logger.info("Application successfully started");
        } catch (Exception th) {
            logger.error("ERROR: Application failed to start", th);
            return -1;
        }

        return null;
    }
}