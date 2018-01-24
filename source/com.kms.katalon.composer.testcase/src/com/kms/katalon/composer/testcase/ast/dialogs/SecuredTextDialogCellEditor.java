package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.testcase.dialogs.TextEncryptionDialog;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;

public class SecuredTextDialogCellEditor extends AbstractDialogCellEditor {
    private ConstantExpressionWrapper constantExpressionWrapper;
    
    public SecuredTextDialogCellEditor(Composite parent, ConstantExpressionWrapper constantExpressionWrapper) {
        super(parent);
        this.constantExpressionWrapper = constantExpressionWrapper;
    }

    @Override
    protected Object openDialogBox(Control cellEditorWindow) {
        TextEncryptionDialog dialog = new TextEncryptionDialog(Display.getCurrent().getActiveShell());
        dialog.setBlockOnOpen(true);
        dialog.open();
        String encryptedText = dialog.getEncryptedText();
        if (!StringUtils.isBlank(encryptedText)) {
            constantExpressionWrapper.setValue(dialog.getEncryptedText());
        }
        return constantExpressionWrapper;
    }
    
    public void applyEditingValue() {
        fireApplyEditorValue();
    }
}