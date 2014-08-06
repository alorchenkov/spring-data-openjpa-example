package com.cpwr.gdo.simulator;

import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom logging formatter for tests that use PowerMock. It converts default
 * format to similar one in test-log4j.properties.
 * 
 */
public final class UnitBaseUtilJavaLoggingFormatter extends Formatter {

    private static final MessageFormat messageFormat = new MessageFormat(
            "{0,date,yyyy-MM-dd HH:mm:ss,SSS} [{1}] {2} [{3}.<{4}>] {5} \n");

    public UnitBaseUtilJavaLoggingFormatter() {
        super();
    }

    @Override
    public String format(LogRecord record) {
        Object[] arguments = new Object[6];
        arguments[0] = new Date(record.getMillis());
        arguments[1] = Thread.currentThread().getName();
        arguments[2] = record.getLevel();
        String className = record.getSourceClassName();
        arguments[3] = className.substring(className.lastIndexOf('.') + 1);
        arguments[4] = record.getSourceMethodName();
        arguments[5] = record.getMessage();

        return messageFormat.format(arguments);
    }

}
