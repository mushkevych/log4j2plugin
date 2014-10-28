package org.log4j2plugin;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.EventLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.junit.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LogRotateTest {
    protected static String[] MESSAGE_TYPES = new String[]{"alpha", "beta", "gama", "delta", "epsilon", "zeta"};
    protected static String MESSAGE_VALUES = "v";
    protected static String ALPHA_LOGGER = "AlphaLogger";
    protected static String ALPHA_LOG_FOLDER = "alpha_log";

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestUtils.cleanLogFolder();

        LogRotateThread.initializeAppenders(Arrays.asList(MESSAGE_TYPES));
        File logFolder = TestUtils.getLoggingFolder();
        FileFilter fileFilter = new WildcardFileFilter("*.log");
        File[] files = logFolder.listFiles(fileFilter);
        Assert.assertTrue(files.length > 0);
        for (File file : files) {
            Assert.assertTrue(file.length() == 0);
        }
    }

    @AfterClass
    public static void tearDownClass() {
        // one-time cleanup code
    }

    @Before
    public void setUp() throws Exception {
        TestUtils.cleanLogFolder();
    }

    @After
    public void tearDown() throws Exception {
        TestUtils.cleanLogFolder();
    }

    protected void performLogging(String tableName) {
        for (int i = 0; i < 250; i++) {
            StructuredDataMessage msg = new StructuredDataMessage("", "", tableName);
            msg.put(MESSAGE_VALUES, "csv,values," + i);
            EventLogger.logEvent(msg);
        }
    }

    @Test
    public void testLogRotateChecks() throws Exception {
        LogRotateThread.initializeAppenders(Arrays.asList(MESSAGE_TYPES));

        for (int i = 0; i < 250; i ++) {
            LogRotateThread logRotateThread = new LogRotateThread(true, true);
            logRotateThread.start();
        }

        File logFolder = TestUtils.getLoggingFolder();
        FileFilter fileFilter = new WildcardFileFilter("*.log");
        File[] files = logFolder.listFiles(fileFilter);
        Assert.assertTrue(files.length > 0);
        for (File file : files) {
            Assert.assertTrue(file.length() == 0);
        }
    }

    //    @Ignore("UT is failing on Ubuntu box after 17:00 PDT due to improper time casting PDT->UTC")
    @Test
    public void testEventLogger() throws Exception {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (final String msgType : MESSAGE_TYPES) {
            performLogging(msgType);

            File logFolder = TestUtils.getLoggingFolder();
            logFolder = new File(logFolder, format.format(calendar.getTime()));
            File[] existingFiles = logFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String fileName = name.toLowerCase();
                    return fileName.contains(msgType);
                }
            });

            Assert.assertNotNull(existingFiles);
            Assert.assertTrue("Can not find any *" + msgType + "* files in: " + logFolder.toString(), existingFiles.length >= 1);
        }
    }

    @Test
    public void testAlphaLoggerFilter() throws Exception {
        Logger loggerAlpha = LogManager.getLogger(ALPHA_LOGGER);
        for (int i = 0; i < 250; i++) {
            loggerAlpha.info("");
            loggerAlpha.warn("");
            loggerAlpha.error("");
        }

        File logFolder = TestUtils.getLoggingFolder();
        FileFilter fileFilter = new WildcardFileFilter("alpha_log.log");
        File[] files = logFolder.listFiles(fileFilter);
        for (File file : files) {
            Assert.assertTrue(file.length() == 0);
        }
    }

    //    @Ignore("UT is failing on Ubuntu box after 17:00 PDT due to improper time casting PDT->UTC")
    @Test
    public void testAlphaLogger() throws Exception {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        int i = 0;
        for (final String msgType : MESSAGE_TYPES) {
            Logger loggerAlpha = LogManager.getLogger(ALPHA_LOGGER);

            for (int j = i; j < i + 10; j++) {
                loggerAlpha.info("errortype={} xml={}", j++, RandomStringUtils.randomAlphanumeric(1024).toUpperCase());
            }
            i += 10;

            final Date reportingDate = calendar.getTime();

            for (int j = i; j < i + 10; j ++) {
                loggerAlpha.info("errortype={} xml={}", j++, RandomStringUtils.randomAlphanumeric(1024).toUpperCase());
            }
            i += 10;

            File logFolder = TestUtils.getLoggingFolder();
            logFolder = new File(logFolder, ALPHA_LOG_FOLDER);
            File[] existingFiles = logFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    String fileName = name.toLowerCase();
                    return fileName.contains(format.format(reportingDate));
                }
            });

            Assert.assertNotNull(existingFiles);
            Assert.assertTrue(existingFiles.length >= 1);
        }
    }
}
