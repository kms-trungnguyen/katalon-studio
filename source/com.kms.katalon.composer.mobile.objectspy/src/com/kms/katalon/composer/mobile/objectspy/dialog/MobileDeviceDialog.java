package com.kms.katalon.composer.mobile.objectspy.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.control.ScrollableComposite;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.element.MobileElement;
import com.kms.katalon.core.mobile.keyword.internal.GUIObject;

public class MobileDeviceDialog extends Dialog {

    private static final String DIALOG_TITLE = "Device View";

    private Image currentScreenShot;

    private Canvas canvas;

    public static final int DIALOG_WIDTH = 400;

    public static final int DIALOG_HEIGHT = 750;

    public static final String HIGHLIGHT_COLOR = "#76BF42";

    private List<Rectangle> highlightRects = new ArrayList<>();

    private boolean highlightPhase = false;

    private double hRatio;

    private boolean isDisposed;

    private Point initialLocation;

    private MobileElementInspectorDialog mobileInspetorDialog;

    private ScrolledComposite scrolledComposite;

    public MobileDeviceDialog(Shell parentShell, MobileElementInspectorDialog mobileInspectorDialog, Point location) {
        super(parentShell);
        this.mobileInspetorDialog = mobileInspectorDialog;
        this.initialLocation = location;
        setShellStyle(SWT.SHELL_TRIM | SWT.FILL | SWT.RESIZE);
        this.isDisposed = false;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        final GridLayout dialogAreaGridLayout = (GridLayout) dialogArea.getLayout();
        dialogAreaGridLayout.marginWidth = 0;
        dialogAreaGridLayout.marginHeight = 0;

        scrolledComposite = new ScrollableComposite(dialogArea, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite container = new Composite(scrolledComposite, SWT.BORDER);
        container.setLayout(new FillLayout());
        scrolledComposite.setContent(container);

        canvas = new Canvas(container, SWT.NONE);
        canvas.pack();

        canvas.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                if (currentScreenShot != null && !currentScreenShot.isDisposed()) {
                    e.gc.drawImage(currentScreenShot, 0, 0);

                    if (highlightPhase && highlightRects != null && highlightRects.size() > 0) {
                        Color oldForegroundColor = e.gc.getForeground();
                        e.gc.setForeground(ColorUtil.getColor(HIGHLIGHT_COLOR));
                        int oldLineWidth = e.gc.getLineWidth();

                        e.gc.setLineWidth(2);
                        highlightRects.forEach(rect -> {
                            e.gc.drawRectangle(safeRoundDouble(rect.x * hRatio), safeRoundDouble(rect.y * hRatio),
                                    safeRoundDouble(rect.width * hRatio), safeRoundDouble(rect.height * hRatio));
                        });

                        e.gc.setLineWidth(oldLineWidth);
                        e.gc.setForeground(oldForegroundColor);
                    }
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                if (e.button == 1) {
                    inspectElementAt(e.x, e.y);
                }
            }
        });

        return dialogArea;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void inspectElementAt(int x, int y) {
        Double realX = x / hRatio;
        Double realY = y / hRatio;
        mobileInspetorDialog.setSelectedElementByLocation(safeRoundDouble(realX), safeRoundDouble(realY));
    }

    private boolean isElementOnScreen(final Double x, final Double y, final Double width, final Double height) {
        Rectangle elementRect = new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
        return elementRect.intersects(getCurrentViewportRect());
    }

    private void scrollToElement(final Double x, final Double y) {
        scrolledComposite.setOrigin(x.intValue(), y.intValue());
    }

    private Rectangle getCurrentViewportRect() {
        ScrollBar verticalBar = scrolledComposite.getVerticalBar();
        ScrollBar horizontalBar = scrolledComposite.getHorizontalBar();
        int viewPortY = (verticalBar.isVisible()) ? verticalBar.getSelection() : 0;
        int viewPortX = (horizontalBar.isVisible()) ? horizontalBar.getSelection() : 0;
        Point viewPortSize = scrolledComposite.getSize();
        Rectangle viewPortRect = new Rectangle(viewPortX, viewPortY, viewPortSize.x, viewPortSize.y);
        return viewPortRect;
    }

    public void highlight(final double x, final double y, final double width, final double height) {
        Rectangle newHighlightRect = new Rectangle(safeRoundDouble(x), safeRoundDouble(y), safeRoundDouble(width),
                safeRoundDouble(height));
        highlightRect(newHighlightRect);
    }

    public void highlightRect(Rectangle rect) {
        List<Rectangle> newHighlightRects = new ArrayList<Rectangle>();
        newHighlightRects.add(rect);
        highlightRects(newHighlightRects);
    }

    public void highlightRects(List<Rectangle> rects) {
        if (rects == null || rects.size() == 0) {
            return;
        }

        Display.getCurrent().syncExec(new Runnable() {
            @Override
            public void run() {
                Rectangle firstRect = rects.size() > 0 ? rects.get(0) : null;
                if (firstRect == null) {
                    return;
                }
                double currentX = firstRect.x * hRatio;
                double currentY = firstRect.y * hRatio;
                double currentWidth = firstRect.width * hRatio;
                double currentHeight = firstRect.height * hRatio;
                if (!isElementOnScreen(currentX, currentY, currentWidth, currentHeight)) {
                    scrollToElement(currentX, currentY);
                }
            }
        });

        Thread highlightThread = new Thread(new Runnable() {
            @Override
            public void run() {
                highlightRects.addAll(rects);
                for (int i = 0; i < 9; i++) {
                    highlightPhase = i % 2 == 1;
                    try {
                        Thread.sleep(200L);
                    } catch (InterruptedException e) {}
                    UISynchronizeService.syncExec(() -> {
                        if (!canvas.isDisposed()) {
                            canvas.redraw();
                        }
                    });
                }
                highlightRects = Collections.synchronizedList(highlightRects);
                synchronized (highlightRects) {
                    highlightRects.removeAll(rects);
                }
            }
        });

        highlightThread.start();
    }

    private Image scaleImage(Image image, double newWidth, double newHeight) {
        Image scaled = new Image(Display.getDefault(), safeRoundDouble(newWidth), safeRoundDouble(newHeight));
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, safeRoundDouble(newWidth),
                safeRoundDouble(newHeight));
        gc.dispose();
        image.dispose();
        return scaled;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(DIALOG_WIDTH, DIALOG_HEIGHT + 57);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(DIALOG_TITLE);
    }

    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.RESIZE);
        setBlockOnOpen(false);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        // No need bottom Button bar
        return parent;
    }

    public void closeApp() {
        handleShellCloseEvent();
    }

    public void highlightElement(MobileElement selectedElement) {
        Map<String, String> attributes = selectedElement.getAttributes();
        if (attributes == null || !attributes.containsKey(GUIObject.X) || !attributes.containsKey(GUIObject.Y)
                || !attributes.containsKey(GUIObject.WIDTH) || !attributes.containsKey(GUIObject.HEIGHT)) {
            return;
        }
        double x = Double.parseDouble(attributes.get(GUIObject.X));
        double y = Double.parseDouble(attributes.get(GUIObject.Y));
        double w = Double.parseDouble(attributes.get(GUIObject.WIDTH));
        double h = Double.parseDouble(attributes.get(GUIObject.HEIGHT));
        highlight(x, y, w, h);
    }

    public void refreshDialog(File imageFile, MobileElement root) {
        try {
            ImageDescriptor imgDesc = ImageDescriptor.createFromURL(imageFile.toURI().toURL());
            Image img = imgDesc.createImage();

            // Save scaled version
            getShell().getDisplay().syncExec(() -> {
                double rootHeight = (double) img.getBounds().height;
                if (root != null) {
                    Map<String, String> attributes = root.getAttributes();
                    if (attributes.containsKey(GUIObject.HEIGHT) && attributes.containsKey(GUIObject.WIDTH)
                            && attributes.containsKey(GUIObject.X) && attributes.containsKey(GUIObject.Y)) {
                        rootHeight = Double.parseDouble(attributes.get(GUIObject.HEIGHT));
                    }
                }

                // Calculate scaled ratio
                double imageRatio = rootHeight / (double) img.getBounds().height;

                hRatio = (double) (canvas.getSize().y / rootHeight);

                currentScreenShot = scaleImage(img, ((double) img.getBounds().width) * imageRatio * hRatio,
                        ((double) img.getBounds().height) * hRatio * imageRatio);

                canvas.redraw();
            });

            refreshView();
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    private void refreshView() {
        if (scrolledComposite == null || currentScreenShot == null) {
            return;
        }
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scrolledComposite.setMinSize(currentScreenShot.getImageData().width,
                        currentScreenShot.getImageData().height);
            }
        });
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    public void dispose() {
        this.isDisposed = true;
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        if ((getShell().getStyle() & SWT.RESIZE) == 0) {
            return new Point(initialLocation.x, initialLocation.y + 5);
        }
        return initialLocation;
    }

    public static int safeRoundDouble(double d) {
        long rounded = Math.round(d);
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, rounded));
    }
}
