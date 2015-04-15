package org.mule.tooling.devkit.ui;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.devkit.DevkitImages;
import org.mule.tooling.ui.MuleUiConstants;

public class ConnectorIconPanel {

    private Canvas bigIcon;
    private Canvas smallIcon;
    private String imagePath = "";
    private Text connectorNameText;

    public void updateStatus() {
        try {
            if (bigIcon != null) {
                bigIcon.redraw();
                GC gc = new GC(bigIcon);
                Image image = new Image(bigIcon.getDisplay(), 48, 32);
                gc.copyArea(image, 0, 0);
                ImageLoader loader = new ImageLoader();
                ImageData data = image.getImageData();
                data.transparentPixel = data.transparentPixel =
                        data.palette.getPixel(new RGB(255,255,255));
                loader.data = new ImageData[] { data };
                loader.save("/Users/pablocabrera/Pictures/swt.png", SWT.IMAGE_PNG);
                image.dispose();
                gc.dispose();
            }
            if (smallIcon != null) {
                smallIcon.redraw();
                GC gc = new GC(smallIcon);
                Image image = new Image(smallIcon.getDisplay(), 24, 16);
                gc.copyArea(image, 0, 0);
                ImageLoader loader = new ImageLoader();
                ImageData data = image.getImageData();
                data.transparentPixel = data.transparentPixel =
                        data.palette.getPixel(new RGB(255,255,255));
                loader.data = new ImageData[] { data };
                loader.save("/Users/pablocabrera/Pictures/swt2.png", SWT.IMAGE_PNG);
                image.dispose();
                gc.dispose();
            }

        } catch (Exception ex) {

        }
    }

    public void createControl(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Connector Image");

        GridLayoutFactory.swtDefaults().numColumns(4).applyTo(group);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).span(4, 1).applyTo(group);

        addLabelGroup(group);
        
        bigIcon = new Canvas(group, SWT.NONE);
        bigIcon.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).create());
        bigIcon.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                Image background = getConnectorImage();
                Image icon = getConnectorIcon();
                Rectangle bounds = background.getBounds();
                e.gc.drawImage(background, 0, 0, bounds.width, bounds.height, 0, 0, 48, 32);
                if (icon == null) {
                    Font font = new Font(e.display, "Arial", 8, SWT.BOLD | SWT.ITALIC);
                    e.gc.setForeground(new Color(e.display, 47, 153, 185));
                    e.gc.setFont(font);
                    FontMetrics metrics = e.gc.getFontMetrics();
                    int averageWidth = metrics.getAverageCharWidth();
                    String name = getConnectorName();
                    e.gc.drawText(getConnectorName(), (47 - averageWidth * name.length()) / 2, (32 - metrics.getHeight()) / 2, true);
                    font.dispose();
                } else {
                    Rectangle iconBounds = icon.getBounds();
                    ImageData data = icon.getImageData();
                    data.transparentPixel = data.palette.getPixel(new RGB(255, 255, 255));
                    final Image transparentIdeaImage = new Image(e.display, data);
                    e.gc.drawImage(transparentIdeaImage, 0, 0, iconBounds.width, iconBounds.height, 9, 1, 30, 30);
                    icon.dispose();
                    transparentIdeaImage.dispose();
                }
            }
        });

        smallIcon = new Canvas(group, SWT.NONE);
        smallIcon.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).create());
        smallIcon.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                Image background = getConnectorImage();
                Image icon = getConnectorIcon();
                Rectangle bounds = background.getBounds();
                e.gc.drawImage(background, 0, 0, bounds.width, bounds.height, 0, 0, 24, 16);
                if (icon == null) {
                    Font font = new Font(e.display, "Arial", 4, SWT.BOLD | SWT.ITALIC);
                    e.gc.setForeground(new Color(e.display, 47, 153, 185));
                    e.gc.setFont(font);
                    FontMetrics metrics = e.gc.getFontMetrics();
                    int averageWidth = metrics.getAverageCharWidth();
                    String name = getConnectorName();
                    e.gc.drawText(getConnectorName(), (23 - averageWidth * name.length()) / 2, (16 - metrics.getHeight()) / 2, true);
                    font.dispose();
                } else {
                    Rectangle iconBounds = icon.getBounds();
                    ImageData data = icon.getImageData();
                    data.transparentPixel = data.palette.getPixel(new RGB(255, 255, 255));
                    final Image transparentIdeaImage = new Image(e.display, data);
                    e.gc.drawImage(transparentIdeaImage, 0, 0, iconBounds.width, iconBounds.height, 5, 1, 14, 14);
                    icon.dispose();
                    transparentIdeaImage.dispose();
                }
            }
        });

        Button browse = new Button(group, SWT.NONE);
        browse.setText("Browse");
        browse.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                dialog.setText("Select Image");
                dialog.setFilterExtensions(new String[] { "*.png;*.gif;*.jpg;*.tiff", "*.*" });
                String result = dialog.open();
                if (result != null) {
                    imagePath = result;
                    updateStatus();
                }
            }
        });
        browse.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).create());
    }

    private void addLabelGroup(Group group) {
        Composite labelGroup = new Composite(group,SWT.NONE);
        Label label = new Label(labelGroup, SWT.NULL);
        label.setText("Label:");
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).hint(SWT.DEFAULT, SWT.DEFAULT).create());
        connectorNameText = new Text(labelGroup, SWT.BORDER);
        connectorNameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        connectorNameText.setText("Connector");
        connectorNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateStatus();

            }
        });
        
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(labelGroup);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).applyTo(labelGroup);
    }

    public String getConnectorName() {
        return connectorNameText.getText();
    }

    protected Image getConnectorImage() {
        return DevkitImages.getManagedImage("", "connector-48x32.png");
    }

    protected Image getConnectorIcon() {
        if (StringUtils.isBlank(imagePath)) {
            return null;
        } else {
            return new Image(Display.getDefault(), imagePath);
        }
    }
}
