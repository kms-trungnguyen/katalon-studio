package com.kms.katalon.composer.webservice.soap.response.body;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.webservice.response.body.RawEditor;
import com.kms.katalon.composer.webservice.response.body.ResponseBodyEditor;
import com.kms.katalon.core.testobject.ResponseObject;

public class SoapResponseBodyEditorsComposite extends Composite {

    private Map<SoapEditorMode, ResponseBodyEditor> bodyEditors = new HashMap<>();

    private Map<SoapEditorMode, Button> bodySelectionButtons = new HashMap<>();

    private Button prettyRadio;

    private Button rawRadio;

    private StackLayout slBodyContent;

    private ResponseObject responseObject;

    private SoapEditorMode selectedEditorMode;

    private enum SoapEditorMode {
        PRETTY, RAW
    };

    public SoapResponseBodyEditorsComposite(Composite parent, int style) {

        super(parent, style);
        setLayout(new GridLayout());

        Composite bodyTypeComposite = new Composite(this, SWT.NONE);
        bodyTypeComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout glBodyType = new GridLayout(2, false);
        glBodyType.marginWidth = glBodyType.marginHeight = 0;
        bodyTypeComposite.setLayout(glBodyType);

        Composite bodyContentComposite = new Composite(this, SWT.NONE);
        bodyContentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        slBodyContent = new StackLayout();
        bodyContentComposite.setLayout(slBodyContent);

        // Pretty Mode
        Composite tbBodyType = new Composite(bodyTypeComposite, SWT.NONE);
        tbBodyType.setLayout(new GridLayout(3, false));
        prettyRadio = new Button(tbBodyType, SWT.RADIO);
        prettyRadio.setText(SoapEditorMode.PRETTY.toString().toLowerCase());
        bodySelectionButtons.put(SoapEditorMode.PRETTY, prettyRadio);

        SoapPrettyEditor mirrorEditor = new SoapPrettyEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put(SoapEditorMode.PRETTY, mirrorEditor);

        // Raw Mode
        rawRadio = new Button(tbBodyType, SWT.RADIO);
        rawRadio.setText(SoapEditorMode.RAW.toString().toLowerCase());
        bodySelectionButtons.put(SoapEditorMode.RAW, rawRadio);

        RawEditor rawEditor = new RawEditor(bodyContentComposite, SWT.NONE);
        bodyEditors.put(SoapEditorMode.RAW, rawEditor);

        handleControlModifyListeners();
    }

    public void setInput(ResponseObject responseOb) {
        this.responseObject = new ResponseObject();
        this.responseObject.setResponseText(responseOb.getResponseText());

        this.selectedEditorMode = SoapEditorMode.PRETTY;

        // Init body content.
        for (ResponseBodyEditor childEditor : bodyEditors.values()) {
            childEditor.setContentBody(responseObject);
        }
        Composite selectedEditor = (Composite) bodyEditors.get(selectedEditorMode);
        slBodyContent.topControl = selectedEditor;
        selectedEditor.getParent().layout();
    }

    private void handleControlModifyListeners() {
        SelectionAdapter bodyTypeSelectedListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button source = (Button) e.getSource();
                if (source.getSelection()) {
                    selectedEditorMode = SoapEditorMode.valueOf(source.getText().toUpperCase());
                    ResponseBodyEditor editorComposite = bodyEditors.get(selectedEditorMode);
                    editorComposite.switchModeContentBody(responseObject);

                    slBodyContent.topControl = (Composite) editorComposite;
                    ((Composite) editorComposite).getParent().layout();
                }
            };
        };

        bodySelectionButtons.values().forEach(button -> {
            button.addSelectionListener(bodyTypeSelectedListener);
        });

        bodyEditors.values().forEach(editor -> {
            ((Composite) editor).addListener(SWT.Modify, event -> {
                responseObject.setContentType(editor.getContentType());
            });
        });

    }
}
