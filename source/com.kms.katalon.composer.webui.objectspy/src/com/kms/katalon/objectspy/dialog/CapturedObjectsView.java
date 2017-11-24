package com.kms.katalon.objectspy.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolTip;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.listener.EventListener;
import com.kms.katalon.composer.components.impl.listener.EventManager;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.WebElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.WebElementTreeContentProvider;

public class CapturedObjectsView extends Composite implements EventHandler, EventManager<ObjectSpyEvent> {

    private TreeViewer treeViewer;

    private WebElement selectedObject;

    private Label lblInfo;

    private ToolTip infoTooltip;
    
    private IEventBroker eventBroker;

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

    public CapturedObjectsView(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);

        this.eventBroker = eventBroker;
        
        setLayoutAndLayoutData();

        createControls();

        addControlListeners();

        subscribeEvents();
    }

    private void setLayoutAndLayoutData() {
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        setLayout(mainLayout);
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        layoutData.heightHint = 150;
        setLayoutData(layoutData);
    }

    private void createControls() {
        Composite capturedObjectsComposite = new Composite(this, SWT.NONE);
        capturedObjectsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        GridLayout glCapturedObjectsComposite = new GridLayout(2, false);
        glCapturedObjectsComposite.marginWidth = 0;
        glCapturedObjectsComposite.marginHeight = 0;
        capturedObjectsComposite.setLayout(glCapturedObjectsComposite);

        Label lblCapturedObjects = new Label(capturedObjectsComposite, SWT.NONE);
        lblCapturedObjects.setFont(ControlUtils.getFontBold(lblCapturedObjects));
        lblCapturedObjects.setText(StringConstants.DIA_LBL_CAPTURED_OBJECTS);

        lblInfo = new Label(capturedObjectsComposite, SWT.NONE);

        GridData gdLblInfo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdLblInfo.heightHint = 16;
        lblInfo.setLayoutData(gdLblInfo);
        lblInfo.setImage(ImageConstants.IMG_16_HELP);

        infoTooltip = new ToolTip(this.getShell(), SWT.BALLOON);
        infoTooltip.setMessage(StringConstants.TOOLTIP_CAPTURED_OBJECTS_HELP);

        treeViewer = new TreeViewer(this, SWT.BORDER | SWT.MULTI);
        treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        treeViewer.setContentProvider(new WebElementTreeContentProvider());
        treeViewer.setLabelProvider(new WebElementLabelProvider());
    }

    private void addControlListeners() {
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!(event.getSelection() instanceof TreeSelection)) {
                    return;
                }

                TreeSelection treeSelection = (TreeSelection) event.getSelection();
                Object selection = treeSelection.getFirstElement();
                if (!(selection instanceof WebElement)) {
                    return;
                }

                invoke(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED, selection);
            }
        });

        lblInfo.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                Point location = e.display.getCursorLocation();
                infoTooltip.setLocation(location.x, location.y - e.y + lblInfo.getSize().y);
                infoTooltip.setVisible(true);
            }

            @Override
            public void mouseExit(MouseEvent e) {
                infoTooltip.setVisible(false);
            }
        });
    }

    private void setTreeDataInput(Object input) {
        if (treeViewer != null && ControlUtils.isReady(treeViewer.getControl())) {
            treeViewer.setInput(input);
        }
    }

    public void setInput(WebPage[] input) {
        setTreeDataInput(input);
    }

    public void setInput(List<WebPage> input) {
        setTreeDataInput(input);
    }

    public WebElement getSelectedObject() {
        return selectedObject;
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    public IStructuredSelection getSelection() {
        return treeViewer.getStructuredSelection();
    }

    public void refreshTree(Object object) {
        treeViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        treeViewer.refresh(object);
        for (Object element : expandedElements) {
            treeViewer.setExpandedState(element, true);
        }
        treeViewer.getControl().setRedraw(true);
    }

    private void subscribeEvents() {
        eventBroker.subscribe(EventConstants.RECORDER_ACTION_SELECTED, this);

    }

    private void unsubscribeEvents() {
        // TODO Unsubscribe events

    }

    @Override
    public void dispose() {
        unsubscribeEvents();
        super.dispose();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public Iterable<EventListener<ObjectSpyEvent>> getListeners(ObjectSpyEvent event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<ObjectSpyEvent> listener, Iterable<ObjectSpyEvent> events) {
        events.forEach(e -> {
            Set<EventListener<ObjectSpyEvent>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });
    }

    @Override
    public void handleEvent(Event event) {
        String eventType = event.getTopic();
        if (eventType == EventConstants.RECORDER_ACTION_SELECTED) {
            WebElement selectedElement = (WebElement) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            treeViewer.setSelection(new StructuredSelection(selectedElement), true);
        }
    }

}
