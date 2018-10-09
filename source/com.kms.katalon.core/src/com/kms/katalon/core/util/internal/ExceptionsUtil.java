package com.kms.katalon.core.util.internal;

import org.codehaus.groovy.runtime.StackTraceUtils;

import com.google.common.base.Throwables;
import com.kms.katalon.core.constants.StringConstants;

import groovy.lang.MissingPropertyException;

public class ExceptionsUtil {
    public static String getMessageForThrowable(Throwable t) {
        if (t == null) {
            return "";
        }
        return getExceptionMessage(t);
    }

    private static String getExceptionMessage(Throwable throwable) {
        if (throwable instanceof MissingPropertyException) {
            return getExceptionMessage((MissingPropertyException) throwable);
        } else {
            return throwable.getClass().getName()
                    + (throwable.getMessage() != null ? (": " + throwable.getMessage()) : "");
        }
    }

    private static String getExceptionMessage(MissingPropertyException exception) {
        return "Variable '" + exception.getProperty() + "' is not defined for test case.";
    }

    public static String getStackTraceForThrowable(Throwable t) {
        t = StackTraceUtils.deepSanitize(t);
        String stackTrace = Throwables.getStackTraceAsString(t);
        return stackTrace;
    }

    public static String getStackTraceForThrowable(Throwable t, String testCaseId, String scriptName) {
        String stackTrace = getStackTraceForThrowable(t);
        stackTrace = stackTrace
                .replace(scriptName + "." + StringConstants.SCRIPT_FILE_EXT, testCaseId)
                .replace(scriptName, testCaseId);
        return stackTrace;
    }
}
