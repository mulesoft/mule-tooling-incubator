/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.mule.tooling.studio.ui.editor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.mule.tooling.editor.annotation.ClassPicker;
import org.mule.tooling.studio.ui.widget.ComboPart;

import com.google.common.base.CaseFormat;

/**
 * @author dejan
 *
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GenericDetailsPage<T> extends AbstractBaseEditorDetailsPage {

    private T input;
    private Class<?> inputClass;
    private Section section;
    private Map<String, Text> textFields;
    private Map<String, Button> booleanFields;
    private Map<String, Spinner> intergerFields;
    private Map<String, ComboPart> enumFields;
    private Boolean onLoad = false;

    public GenericDetailsPage(Class<?> inputClass) {
        this.inputClass = inputClass;
        textFields = new HashMap<String, Text>();
        booleanFields = new HashMap<String, Button>();
        intergerFields = new HashMap<String, Spinner>();
        enumFields = new HashMap<String, ComboPart>();
    }

    @Override
    public void initialize(IManagedForm mform) {
        this.mform = mform;
    }

    @Override
    public void createContents(Composite parent) {
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);

        FormToolkit toolkit = mform.getToolkit();
        section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
        section.marginWidth = 10;
        section.setText("Details"); //$NON-NLS-1$
        section.setDescription("Description"); //$NON-NLS-1$
        TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        section.setLayoutData(td);
        Composite client = toolkit.createComposite(section);
        GridLayout glayout = new GridLayout();
        glayout.marginWidth = glayout.marginHeight = 0;
        glayout.numColumns = 2;
        client.setLayout(glayout);

        createSpacer(toolkit, client, 2);

        List<Field> fields = new ArrayList<Field>();
        getAllFields(fields, inputClass);
        Comparator<Field> comparator = new Comparator<Field>() {

            public int compare(Field c1, Field c2) {
                return c1.getName().compareTo(c2.getName());
            }
        };

        Collections.sort(fields, comparator);
        for (final Field field : fields) {
            if (isSupported(field)) {
                addField(toolkit, client, field);
            }
        }
        createSpacer(toolkit, client, 2);

        toolkit.paintBordersFor(section);
        section.setClient(client);
    }

    private boolean isSupported(final Field field) {
        return field.getType().isPrimitive() || String.class.equals(field.getType()) || Boolean.class.equals(field.getType()) || field.getType().isEnum()
                || Integer.class.equals(field.getType());
    }

    private void addField(FormToolkit toolkit, Composite client, final Field field) {
        if (Integer.class.equals(field.getType())) {
            addIntegerField(toolkit, client, field);
        } else if (Boolean.class.equals(field.getType())) {
            addCheckBoxField(toolkit, client, field);
        } else if (field.getType().isEnum()) {
            addEnumField(toolkit, client, field);
        } else {
            addTextField(toolkit, client, field);
        }
    }

    private void addEnumField(FormToolkit toolkit, Composite client, final Field field) {
        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (!onLoad) {
                        field.setAccessible(true);
                        String stringValue = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, ((Combo) e.getSource()).getText().replace(" ", ""));
                        Object updatedValue = null;
                        for (Object value : field.getType().getEnumConstants()) {
                            if (value.toString().equals(stringValue)) {
                                updatedValue = value;
                                break;
                            }
                        }
                        field.set(input, updatedValue);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
        field.setAccessible(true);
        enumFields.put(field.getName(), createEnumTextField(toolkit, client, field.getName(), field, listener));

    }

    private void addTextField(FormToolkit toolkit, Composite client, final Field field) {
        ModifyListener listener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                try {
                    if (!onLoad) {
                        field.setAccessible(true);
                        field.set(input, ((Text) e.getSource()).getText());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };
        field.setAccessible(true);
        if (field.isAnnotationPresent(ClassPicker.class)) {
            textFields.put(field.getName(), this.createClassPickerTextField(toolkit, client, field.getName(), listener, field.getAnnotation(ClassPicker.class)));
        } else {
            textFields.put(field.getName(), this.createTextField(toolkit, client, field.getName(), listener));
        }
    }

    private void addCheckBoxField(FormToolkit toolkit, Composite client, final Field field) {
        SelectionListener listener = new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (!onLoad) {
                        field.setAccessible(true);
                        field.set(input, Boolean.valueOf(((Button) e.getSource()).getSelection()));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                try {
                    if (!onLoad) {
                        field.setAccessible(true);
                        field.set(input, Boolean.valueOf(((Button) e.getSource()).getSelection()));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        };
        booleanFields.put(field.getName(), this.createCheckBoxField(toolkit, client, field.getName(), listener));
    }

    private void addIntegerField(FormToolkit toolkit, Composite client, final Field field) {
        ModifyListener listener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                try {
                    if (!onLoad) {
                        field.setAccessible(true);
                        field.set(input, Integer.valueOf(((Spinner) e.getSource()).getSelection()));
                    }
                } catch (IllegalArgumentException | IllegalAccessException e1) {
                }
            }
        };
        this.intergerFields.put(field.getName(), this.createSpinnerField(toolkit, client, field.getName(), listener));
    }

    private void createSpacer(FormToolkit toolkit, Composite parent, int span) {
        Label spacer = toolkit.createLabel(parent, ""); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        spacer.setLayoutData(gd);
    }

    protected void update() {
        onLoad = true;
        List<Field> fields = new ArrayList<Field>();
        getAllFields(fields, inputClass);
        for (Spinner spin : intergerFields.values()) {
            spin.setSelection(0);
        }
        for (Text stringField : textFields.values()) {
            stringField.setText("");
        }
        for (ComboPart stringField : enumFields.values()) {
            stringField.setText("");
        }
        for (Button button : booleanFields.values()) {
            button.setSelection(false);
        }

        for (Field field : fields) {
            if (isSupported(field)) {
                try {
                    field.setAccessible(true);
                    if (Integer.class.equals(field.getType())) {
                        if (input != null && field.get(input) != null) {
                            this.intergerFields.get(field.getName()).setSelection(Integer.valueOf((String) String.valueOf(field.get(input))));
                        }
                    } else if (Boolean.class.equals(field.getType())) {
                        if (field.get(input) != null) {
                            booleanFields.get(field.getName()).setSelection(Boolean.valueOf(field.get(input).toString()));
                        }
                    } else if (field.getType().isEnum()) {
                        if (field.get(input) != null) {
                            String value = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(

                            CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, field.get(input).toString())

                            ), ' ');
                            enumFields.get(field.getName()).setText(value);
                        }
                    } else {
                        if (field.get(input) != null) {
                            textFields.get(field.getName()).setText(field.get(input).toString());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        onLoad = false;
    }

    @SuppressWarnings("unchecked")
    public void selectionChanged(IFormPart part, ISelection selection) {
        IStructuredSelection ssel = (IStructuredSelection) selection;
        if (ssel.size() == 1) {
            input = (T) ssel.getFirstElement();
            section.setText(input.getClass().getSimpleName() + " Page");
        } else
            input = null;
        update();
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}
