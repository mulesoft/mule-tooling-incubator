/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

package org.mule.tooling.properties.editors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.mule.tooling.properties.actions.EditPropertyAction;
import org.mule.tooling.properties.model.CommentedProperties;

public class GraphicalMulePropertiesEditor extends EditorPart implements
		IPropertiesEditor {

	private TableViewer tableViewer;
	private CommentedProperties model = new CommentedProperties();
	private boolean isDirty = false;
	
	public GraphicalMulePropertiesEditor() {
		super();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		setClean();

	}

	@Override
	public void doSaveAs() {
		setClean();

	}

	protected void setClean() {
		isDirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		
	}

	public void setContent(String propertiesValue) {
		try {

			model.clear();
			model.load(new ByteArrayInputStream(propertiesValue.getBytes()));
			getTableViewer().refresh();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getContent() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			model.store(out, null);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			return new String(out.toByteArray(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isDirty() {

		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(final Composite parent) {

		setPartName("Table editor");
		setTableViewer(new TableViewer(parent));
		getTableViewer().setContentProvider(
				new PropertiesTableContentProvider());
		
		getTableViewer().setLabelProvider(new PropertiesTableLabelProvider());
		final Table table = getTableViewer().getTable();
		table.setHeaderVisible(true);
		getTableViewer().setInput(model);
		Table propertiesTable = table;
		propertiesTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		// 2nd column with task Description
		final TableColumn column;
		column = new TableColumn(propertiesTable, SWT.LEFT, 0);
		column.setText("Name");
		column.setWidth(300);

		// 3rd column with task Owner
		final TableColumn typeColumn;
		typeColumn = new TableColumn(propertiesTable, SWT.LEFT, 1);
		typeColumn.setText("Value");
		typeColumn.setWidth(300);

		parent.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = parent.getClientArea();
				Point preferredSize = table.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int scrollBarWidth = 13;
				int width = area.width - 2 * table.getBorderWidth()
						- scrollBarWidth;
				if (preferredSize.y > area.height + table.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = table.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					typeColumn.setWidth(width / 2);
					column.setWidth(width - typeColumn.getWidth());
					table.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					table.setSize(area.width, area.height);
					typeColumn.setWidth(width / 2);
					column.setWidth(width - typeColumn.getWidth());
				}
			}
		});
		getTableViewer().addOpenListener(new IOpenListener() {
			
			@Override
			public void open(OpenEvent arg0) {
				new EditPropertyAction(new IPropertiesEditorAccessor() {
					
					@Override
					public IPropertiesEditor getPropertiesEditor() {
					
						return GraphicalMulePropertiesEditor.this;
					}
				}).run();				
			}
		});

	}

	@Override
	public void setFocus() {

	}

	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	public void addProperty(String key, Object value) {
		model.put(key, value);
		tableViewer.refresh();
		setDirty();
	}

	protected void setDirty() {
		isDirty = true;
		firePropertyChange(PROP_DIRTY);
	}

	public void removeProperty(String keyValue) {

		model.remove(keyValue);
		tableViewer.refresh();
		setDirty();

	}

	@Override
	public Entry getSelectedProperty() {
		StructuredSelection selection = (StructuredSelection) getTableViewer()
				.getSelection();
		if (selection != null)
			return (Entry) selection.getFirstElement();
		return null;
	}

	public void updatePartControl(IEditorInput editorInput) {

		IFile file = null;
		if (editorInput instanceof IFileEditorInput) {
			file = ((IFileEditorInput) editorInput).getFile();
		} else if (editorInput instanceof IAdaptable) {
			file = (IFile) ((IAdaptable) editorInput).getAdapter(IFile.class);
		}

		if (file != null) {

			try {
				model.clear();
				model.load(file.getContents());
				tableViewer.refresh();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		isDirty = false;
	}

	@Override
	public void updateProperty(String key, Object value) {
		model.put(key, value);
		tableViewer.refresh();
		setDirty();
		
	}

}
