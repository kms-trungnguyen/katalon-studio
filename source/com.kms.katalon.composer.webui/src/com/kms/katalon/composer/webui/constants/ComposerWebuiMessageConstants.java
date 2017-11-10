package com.kms.katalon.composer.webui.constants;

import org.eclipse.osgi.util.NLS;

public class ComposerWebuiMessageConstants extends NLS {
    private static final String BUNDLE_NAME = "com.kms.katalon.composer.webui.constants.composerWebuiMessages";

    public static String DIA_REMOTE_SERVER_URL_TITLE;

    public static String DIA_REMOTE_SERVER_URL_MESSAGE;

    public static String PREF_ERROR_MSG_VAL_MUST_BE_AN_INT_BIGGER_FROM_X;

    public static String PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT;

    public static String LBL_ACTION_DELAY;

    public static String LBL_REMOTE_EXECUTION_MENU_ITEM;

    public static String LBL_REMOTE_SERVER_URL;

    public static String LBL_REMOTE_SERVER_TYPE;

    public static String LBL_DEBUG_PORT;

    public static String LBL_DEBUG_HOST;

    public static String LBL_DESIRED_CAP;

    public static String LBL_DLG_REMOTE_DRIVER_TYPE;

    public static String LBL_DLG_REMOTE_SERVER_URL;

    public static String PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT;

    public static String PREF_LBL_CUSTOM_PAGE_LOAD_TIMEOUT;

    public static String PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT;

    public static String PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION;

    public static String LBL_IE_EXECUTION_MENU_ITEM;

    public static String LBL_EDGE_EXECUTION_MENU_ITEM;

    public static String LBL_CHROME_EXECUTION_MENU_ITEM;

    public static String LBL_FIREFOX_EXECUTION_MENU_ITEM0;

    public static String LBL_HEADLESS_EXECUTION_MENU_ITEM;

    public static String LBL_SAFARI_EXECUTION_MENU_ITEM;

    public static String DIA_CONFIRM_INSTALL_WEBDRIVER;

    public static String DIA_CONFIRM_INSTALL_WEBDRIVER_TITLE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ComposerWebuiMessageConstants.class);
    }

    private ComposerWebuiMessageConstants() {
    }
}
