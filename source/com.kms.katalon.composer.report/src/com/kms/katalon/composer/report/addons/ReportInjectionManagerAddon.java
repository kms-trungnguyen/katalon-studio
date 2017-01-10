package com.kms.katalon.composer.report.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.report.handlers.DeleteReportCollectionHandler;
import com.kms.katalon.composer.report.handlers.DeleteReportHandler;
import com.kms.katalon.composer.report.handlers.EvaluateIntegrationContributionViewHandler;
import com.kms.katalon.composer.report.handlers.OpenReportCollectionHandler;
import com.kms.katalon.composer.report.handlers.OpenReportHandler;
import com.kms.katalon.composer.report.handlers.RefreshReportHandler;
import com.kms.katalon.composer.report.handlers.RenameReportHandler;

public class ReportInjectionManagerAddon {

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(DeleteReportHandler.class, context);
        ContextInjectionFactory.make(DeleteReportCollectionHandler.class, context);
        ContextInjectionFactory.make(OpenReportHandler.class, context);
        ContextInjectionFactory.make(OpenReportCollectionHandler.class, context);
        ContextInjectionFactory.make(RefreshReportHandler.class, context);
        ContextInjectionFactory.make(EvaluateIntegrationContributionViewHandler.class, context);
        ContextInjectionFactory.make(RenameReportHandler.class, context);
    }
}
