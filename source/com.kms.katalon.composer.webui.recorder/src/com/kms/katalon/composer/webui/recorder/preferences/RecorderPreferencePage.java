package com.kms.katalon.composer.webui.recorder.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.webui.recorder.constants.RecorderPreferenceConstants;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;

public class RecorderPreferencePage extends FieldEditorPreferencePage {

    public RecorderPreferencePage() {
        setPreferenceStore(
                PreferenceStoreManager.getPreferenceStore(RecorderPreferenceConstants.WEBUI_RECORDER_QUALIFIER));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addField(new ComboFieldEditor(RecorderPreferenceConstants.WEBUI_RECORDER_DEFAULT_BROWSER,
                StringConstants.PREF_LBL_DEFAULT_BROWSER, getDefaultBrowserOptions(), composite));

        initialize();
        checkState();
        return composite;
    }

    private String[][] getDefaultBrowserOptions() {
        List<String> browsers = new ArrayList<>(
                Arrays.asList(RecorderPreferenceDefaultValueInitializer.SUPPORTED_BROWSERS));
        if (!Platform.OS_WIN32.equals(Platform.getOS())) {
            browsers.remove(WebUIDriverType.IE_DRIVER.toString());
        }
        List<String[]> options = new ArrayList<>();
        browsers.forEach(browser -> options.add(new String[] { browser, browser }));
        return options.toArray(new String[][] {});
    }

    @Override
    protected void createFieldEditors() {
        // do nothing because we overload the create contents method instead
    }
}
