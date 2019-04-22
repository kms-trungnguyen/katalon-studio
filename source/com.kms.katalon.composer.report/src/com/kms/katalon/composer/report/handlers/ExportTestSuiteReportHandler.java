package com.kms.katalon.composer.report.handlers;

import java.io.File;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.report.menu.ExportReportDynamicMenuContribution;
import com.kms.katalon.composer.report.menu.ExportReportMenuItemObject;
import com.kms.katalon.composer.report.platform.ExportReportProviderPlugin;
import com.kms.katalon.composer.report.platform.ExportReportProviderReflection;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.report.ReportEntity;

public class ExportTestSuiteReportHandler {

    @Inject
    ESelectionService selectionService;

    @Execute
    public void execute(@Optional MDirectMenuItem directMenuItem, Shell shell) {
        if (directMenuItem == null) {
            return;
        }
        ExportReportMenuItemObject menuItemObject = (ExportReportMenuItemObject) directMenuItem.getTransientData()
                .get(ExportReportDynamicMenuContribution.REPORT_ITEM_TRANSIENT_DATA);
        ExportReportProviderPlugin exportReportProviderPlugin = menuItemObject.getExportReportPlugin();
        DirectoryDialog directoryDialog = new DirectoryDialog(shell);
        directoryDialog.open();

        if (directoryDialog.getFilterPath() == null) {
            MessageDialog.openWarning(null, "Warning", "Directory not found.");
            return;
        }

        File exportDirectory = new File(directoryDialog.getFilterPath());
        if (exportDirectory != null && exportDirectory.exists() && exportDirectory.isDirectory()) {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            for (Object selectedObject : selectedObjects) {
                try {
                    if (selectedObject instanceof ReportTreeEntity
                            && ((ReportTreeEntity) selectedObject).getObject() instanceof ReportEntity) {
                        ReportEntity report = (ReportEntity) ((ReportTreeEntity) selectedObject).getObject();

                        File exportedFile = new File(exportDirectory,
                                report.getDisplayName() + getExtension(menuItemObject.getFormatType()));

                        ExportReportProviderReflection reflection = new ExportReportProviderReflection(
                                exportReportProviderPlugin);
                        reflection.exportTestSuite(report, menuItemObject.getFormatType(), exportedFile);
                        Program.launch(exportedFile.toURI().toString());
                    }
                } catch (Throwable e) {
                    LoggerSingleton.logError(e);
                    UISynchronizeService.syncExec(() ->
                        MessageDialog.openError(shell, "Error", "Unable to export report (" + e.getMessage() + ")"));
                }
            }
        }
    }

    private String getExtension(String formatType) {
        if (StringUtils.containsIgnoreCase(formatType, "html")) {
            return ".html";
        }

        if (StringUtils.containsIgnoreCase(formatType, "csv")) {
            return ".csv";
        }

        if (StringUtils.containsIgnoreCase(formatType, "pdf")) {
            return ".pdf";
        }

        if (StringUtils.containsIgnoreCase(formatType, "junit")) {
            return ".xml";
        }

        if (StringUtils.containsIgnoreCase(formatType, "xml")) {
            return ".xml";
        }

        if (StringUtils.containsIgnoreCase(formatType, "json")) {
            return ".json";
        }
        return "";
    }
}
