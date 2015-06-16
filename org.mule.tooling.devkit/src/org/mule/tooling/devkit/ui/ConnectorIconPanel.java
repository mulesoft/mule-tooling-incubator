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

public class ConnectorIconPanel {

    private static final int SMALL_ICON_WIDTH = 24;
    private static final int BIG_ICON_WIDTH = 48;

    private static final int MAX_NAME_LENGTH = 9;

    private Canvas bigIcon;
    private Canvas smallIcon;
    private String imagePath = "";
    private Text connectorNameText;

    public void updateStatus() {
        try {
            if (bigIcon != null) {
                bigIcon.redraw();
            }
            if (smallIcon != null) {
                smallIcon.redraw();
            }

        } catch (Exception ex) {

        }
    }

    public void createControl(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Connector Image");

        GridLayoutFactory.swtDefaults().numColumns(3).applyTo(group);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).span(2, 1).applyTo(group);

        addLabelGroup(group);

        Button selectIcon = new Button(group, SWT.NONE);
        selectIcon.setText("Select Icon");
        selectIcon.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                dialog.setText("Select Icon");
                dialog.setFilterExtensions(new String[] { "*.png;*.gif;*.jpg;*.tiff", "*.*" });
                String result = dialog.open();
                if (result != null) {
                    imagePath = result;
                    updateStatus();
                }
            }
        });
        GridDataFactory.swtDefaults().grab(false, false).applyTo(selectIcon);

        Composite iconGroup = new Composite(group, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(iconGroup);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(false, false).applyTo(iconGroup);

        Label label = new Label(iconGroup, SWT.NULL);
        label.setText("Canvas Icon");
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).hint(SWT.DEFAULT, SWT.DEFAULT).create());

        bigIcon = new Canvas(iconGroup, SWT.NONE);
        bigIcon.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).create());
        bigIcon.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                Image flowBackground = getFlowBackgroundImage();
                Rectangle flowBounds = flowBackground.getBounds();
                e.gc.drawImage(flowBackground, 0, 0, flowBounds.width, flowBounds.height, 0, 0, 80, 80);
                Image background = getConnectorImage();
                Image icon = getConnectorIcon();
                Rectangle bounds = background.getBounds();
                e.gc.drawImage(background, 0, 0, bounds.width, bounds.height, 5, 25, BIG_ICON_WIDTH, 32);
                if (icon == null) {
                    Font font = new Font(e.display, "Arial", 8, SWT.BOLD | SWT.ITALIC);
                    e.gc.setForeground(new Color(e.display, 47, 153, 185));
                    e.gc.setFont(font);
                    FontMetrics metrics = e.gc.getFontMetrics();
                    int averageWidth = metrics.getAverageCharWidth();
                    int labelLength = MAX_NAME_LENGTH < getConnectorName().length() ? MAX_NAME_LENGTH : getConnectorName().length();
                    String name = getConnectorName().substring(0, labelLength);
                    e.gc.drawText(name, 5 + (47 - averageWidth * name.length()) / 2, 25 + (32 - metrics.getHeight()) / 2, true);
                    font.dispose();
                } else {
                    Rectangle iconBounds = icon.getBounds();
                    ImageData data = icon.getImageData();
                    if (data.transparentPixel == -1) {
                        data.transparentPixel = data.palette.getPixel(data.palette.getRGB(data.getPixel(0, 0)));
                    }
                    final Image transparentIdeaImage = new Image(e.display, data);
                    e.gc.drawImage(transparentIdeaImage, 0, 0, iconBounds.width, iconBounds.height, 5 + 9, 25 + 1, 30, 30);
                    icon.dispose();
                    transparentIdeaImage.dispose();
                }
            }
        });

        iconGroup = new Composite(group, SWT.NONE);
        GridLayoutFactory.swtDefaults().numColumns(1).applyTo(iconGroup);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(SWT.DEFAULT, SWT.DEFAULT).grab(false, false).applyTo(iconGroup);
        label = new Label(iconGroup, SWT.NULL);
        label.setText("Palette Icon");
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).hint(SWT.DEFAULT, SWT.DEFAULT).create());

        smallIcon = new Canvas(iconGroup, SWT.NONE);
        smallIcon.setLayoutData(GridDataFactory.swtDefaults().grab(false, false).create());
        smallIcon.addPaintListener(new PaintListener() {

            public void paintControl(PaintEvent e) {
                Image paletteBackground = getPaletteBackgroundImage();
                Rectangle flowBounds = paletteBackground.getBounds();
                e.gc.drawImage(paletteBackground, 0, 0, flowBounds.width, flowBounds.height, 0, 0, flowBounds.width / 2, flowBounds.height / 2);
                Image background = getConnectorImage();
                Image icon = getConnectorIcon();
                Rectangle bounds = background.getBounds();
                e.gc.drawImage(background, 0, 0, bounds.width, bounds.height, 18, 27, SMALL_ICON_WIDTH, 16);
                if (icon == null) {
                    Font font = new Font(e.display, "Arial", 4, SWT.BOLD | SWT.ITALIC);
                    e.gc.setForeground(new Color(e.display, 47, 153, 185));
                    e.gc.setFont(font);
                    FontMetrics metrics = e.gc.getFontMetrics();
                    int averageWidth = metrics.getAverageCharWidth();
                    int labelLength = MAX_NAME_LENGTH < getConnectorName().length() ? MAX_NAME_LENGTH : getConnectorName().length();
                    String name = getConnectorName().substring(0, labelLength);
                    e.gc.drawText(name, 18 + (23 - averageWidth * name.length()) / 2, 27 + (16 - metrics.getHeight()) / 2, true);
                    font.dispose();
                } else {
                    Rectangle iconBounds = icon.getBounds();
                    ImageData data = icon.getImageData();
                    if (data.transparentPixel == -1) {
                        data.transparentPixel = data.palette.getPixel(data.palette.getRGB(data.getPixel(0, 0)));
                    }
                    final Image transparentIdeaImage = new Image(e.display, data);
                    e.gc.drawImage(transparentIdeaImage, 0, 0, iconBounds.width, iconBounds.height, 18 + 5, 27 + 1, 14, 14);
                    icon.dispose();
                    transparentIdeaImage.dispose();
                }
            }

        });
        GridDataFactory.fillDefaults().applyTo(smallIcon);
        Button reset = new Button(group, SWT.NONE);
        reset.setText("Reset");
        reset.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                imagePath = null;
                connectorNameText.setText("Connector");
            }
        });
        GridDataFactory.swtDefaults().grab(false, false).applyTo(reset);
        updateStatus();
    }

    private void addLabelGroup(Group group) {
        Composite labelGroup = new Composite(group, SWT.NONE);
        Label label = new Label(labelGroup, SWT.NULL);
        label.setText("Label:");
        label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).hint(SWT.DEFAULT, SWT.DEFAULT).create());
        connectorNameText = new Text(labelGroup, SWT.BORDER);
        connectorNameText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        connectorNameText.setText("Connector");
        connectorNameText.setTextLimit(MAX_NAME_LENGTH);
        connectorNameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateStatus();

            }
        });
        Composite emptyComposite = new Composite(labelGroup, SWT.NONE);
        GridDataFactory.swtDefaults().grab(true, false).hint(0, 0).applyTo(emptyComposite);

        GridLayoutFactory.swtDefaults().numColumns(3).applyTo(labelGroup);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(5, 1).hint(SWT.DEFAULT, SWT.DEFAULT).grab(true, false).applyTo(labelGroup);
    }

    public String getConnectorName() {
        return connectorNameText.getText();
    }

    protected Image getConnectorImage() {
        return DevkitImages.getManagedImage("", "connector-48x32.png");
    }

    protected Image getFlowBackgroundImage() {
        return DevkitImages.getManagedImage("", "flow-container.png");
    }

    protected Image getPaletteBackgroundImage() {
        return DevkitImages.getManagedImage("", "palette.png");
    }

    protected Image getConnectorIcon() {
        if (StringUtils.isBlank(imagePath)) {
            return null;
        } else {
            return new Image(Display.getDefault(), imagePath);
        }
    }

    public void saveTo(String smallIconPath, String bigIconPath) {
        saveIcon(bigIcon, bigIconPath, BIG_ICON_WIDTH, 32, 8);
        saveIcon(smallIcon, smallIconPath, SMALL_ICON_WIDTH, 16, 4);
    }

    private void saveIcon(Canvas icon, String iconPath, int width, int height, int fontSize) {
        if (icon != null) {
            Image image = getScaledImage(width, height, fontSize);
            ImageLoader loader = new ImageLoader();
            ImageData data = image.getImageData();
            data.transparentPixel = data.transparentPixel = data.palette.getPixel(new RGB(255, 255, 255));
            loader.data = new ImageData[] { data };
            loader.save(iconPath, SWT.IMAGE_PNG);
            image.dispose();
        }

    }

    private Image getScaledImage(int width, int height, int fontSize) {
        Image image = null;
        Image source = getConnectorImage();

        image = initializeBackgroundImage(width, height, source);

        GC gc = new GC(image);

        scaleBackground(width, height, source, gc);

        Image icon = getConnectorIcon();
        if (icon == null) {
            writeConnectorName(width, height, fontSize, gc);
        } else {
            paintLogo(width, icon, gc);
        }
        gc.dispose();
        return image;
    }

    private void scaleBackground(int width, int height, Image source, GC gc) {
        Color transparentColor = source.getBackground();
        if (transparentColor != null) {
            gc.setBackground(transparentColor);
            gc.fillRectangle(0, 0, width, height);
        }
        gc.drawImage(source, 0, 0, source.getBounds().width, source.getBounds().height, 0, 0, width, height);
    }

    private Image initializeBackgroundImage(int width, int height, Image source) {
        Image image;
        ImageData sourceImageData = source.getImageData();
        if (sourceImageData.transparentPixel != -1) {
            ImageData id = new ImageData(width, height, sourceImageData.depth, sourceImageData.palette);
            id.transparentPixel = sourceImageData.transparentPixel;
            image = new Image(source.getDevice(), id);
        } else {
            image = new Image(source.getDevice(), width, height);
        }
        return image;
    }

    private void paintLogo(int width, Image icon, GC gc) {
        Rectangle iconBounds = icon.getBounds();
        ImageData data = icon.getImageData();
        if (data.transparentPixel == -1) {
            data.transparentPixel = data.palette.getPixel(data.palette.getRGB(data.getPixel(0, 0)));
        }
        final Image transparentIdeaImage = new Image(gc.getDevice(), data);
        if (width == SMALL_ICON_WIDTH) {
            gc.drawImage(transparentIdeaImage, 0, 0, iconBounds.width, iconBounds.height, 5, 1, 14, 14);
        } else {
            gc.drawImage(transparentIdeaImage, 0, 0, iconBounds.width, iconBounds.height, 9, 1, 30, 30);
        }
        icon.dispose();
        transparentIdeaImage.dispose();
    }

    private void writeConnectorName(int width, int height, int fontSize, GC gc) {
        Font font = new Font(gc.getDevice(), "Arial", fontSize, SWT.BOLD | SWT.ITALIC);
        gc.setForeground(new Color(gc.getDevice(), 47, 153, 185));
        gc.setFont(font);
        FontMetrics metrics = gc.getFontMetrics();
        int averageWidth = metrics.getAverageCharWidth();
        int labelLength = MAX_NAME_LENGTH < getConnectorName().length() ? MAX_NAME_LENGTH : getConnectorName().length();
        String name = getConnectorName().substring(0, labelLength);
        gc.drawText(name, ((width - 1) - averageWidth * name.length()) / 2, (height - metrics.getHeight()) / 2, true);
        font.dispose();
    }
}
