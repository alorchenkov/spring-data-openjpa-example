package com.cpwr.gdo.simulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Base JUnit test for tests that use PowerMock. It extends UnitTestBase with
 * initialisation of java logging.
 * <p/>
 * <ul>
 * <li>{@link TestWatcher}: adds logic at events during method execution;</li>
 * </ul>
 * <p/>
 * {@link UnitTestBase#methodLogger} generates log messages for each junit
 * tests. Example of log file: </br><code>
 * <br>2012-02-10 INFO  [UnitTestBase$1.<starting>] ---HandlerTest.testGetGroupId starting...---</br>
 * <br>2012-02-10 DEBUG [Handler.getGroupId:161]  groupId:101</br>
 * <br>2012-02-10 INFO  [UnitTestBase$1.<finished>] HandlerTest.testGetGroupId Passed.</br>
 * </code>
 *
 */
public abstract class UnitTestBase {
    /** The log manager. */
    protected static LogManager logManager;

    protected static String usrDir = System.getProperty("user.dir");

    protected static String fsep = System.getProperty("file.separator");

    protected static String testResourceDir = usrDir + fsep + "src" + fsep + "test" + fsep + "resources";

    protected static String testPropsDir = testResourceDir + fsep + "props";

    protected static Properties testPortalExtEnvProperties;

    @BeforeClass
    public static void init() throws Exception {
        initializeSlf4jConfig();
        initializeUtiLogConfig();
    }

    /**
     * Initialize util log config.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void initializeUtiLogConfig() throws IOException {
        /* Initialise Java Logging used in PowerMock tests */
        String loggingPropsFile = testPropsDir + fsep + "test-logging.properties";
        System.setProperty("java.util.logging.config.file", loggingPropsFile);

        logManager = LogManager.getLogManager();

        /*
         * force log manager to reread logging configuration from our properties
         * file
         */
        logManager.readConfiguration();
    }

    /**
     * Initializelog4j config.
     * 
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void initializeSlf4jConfig() throws FileNotFoundException, IOException {
        /* Initialize log4j */
        String logback = "file:" + testPropsDir + fsep + "logback-test.xml";
        // System.setProperty("log4j.configuration", log4jPropsFile);
        System.setProperty("logback.configurationFile", logback);
    }

    /**
     * The method returns log instance which is created in junit test classes.
     * 
     * @return the log
     */
    public abstract Logger getLog();

    /**
     * The method logger wraps junit test methods with log messages.
     * 
     * */
    @Rule
    public TestWatcher methodLogger = new TestWatcher() {

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    starting(description);
                    StopWatch timer = new StopWatch();

                    try {
                        timer.start();
                        base.evaluate();
                        timer.stop();
                        /* Invoked when a test method finishes and it passed */
                        finished(description, " Passed. Duration: " + timer.getTime() + " ms");
                    } catch (AssumptionViolatedException e) {
                        throw e;
                    } catch (Throwable t) {
                        /* Invoked when a test method finishes and it failed */
                        finished(description, " Test failed. Duration: " + timer.getTime() + " ms");
                        throw t;
                    } finally {
                        // finished(description, "");
                    }
                }
            };
        }

        /**
         * Invoked when a test method is about to start
         */
        public void starting(final Description descr) {
            if (getLog() != null) {
                final String logIntro = getShortTestMethodName(descr) + " starting...";
                getLog().info("------------- " + logIntro + "-------------");
            }
        }

        /**
         * Invoked to log message.
         */
        public void finished(final Description descr, final String logMessage) {
            if (getLog() != null) {
                getLog().info(getShortTestMethodName(descr) + logMessage);
            }
        };

        /**
         * The method returns name of junit test class from description object.
         * 
         * @return String class name
         */
        private String getShortTestMethodName(final Description descr) {
            String result = null;

            if (descr != null) {
                final String[] fullTestClassName = StringUtils.split(descr.getClassName(), ".");
                result = fullTestClassName[fullTestClassName.length - 1] + "." + descr.getMethodName();
            } else {
                getLog().log(Level.SEVERE, "Description must not be null!");
            }

            return result;
        }
    };
}
