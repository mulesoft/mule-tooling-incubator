/**
 * $Id: LicenseManager.java 10480 2007-12-19 00:47:04Z moosa $
 * --------------------------------------------------------------------------------------
 * (c) 2003-2008 MuleSource, Inc. This software is protected under international copyright
 * law. All use of this software is subject to MuleSource's Master Subscription Agreement
 * (or other master license agreement) separately entered into in writing between you and
 * MuleSource. If such an agreement is not in place, you may not use the software.
 */

/**
 * 
 */
package org.mule.tooling.properties.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mule.tooling.properties.editors.IPropertiesEditorAccessor;



/**
 * @author seba
 * 
 */
public class AddPropertyDialog extends TitleAreaDialog {

	private Text keyText;
	private Text valueText;
	private String value;
	private String key;
	private Button encrypt;
	private IPropertiesEditorAccessor holder;

	public AddPropertyDialog(Shell parentShell,
			IPropertiesEditorAccessor provider) {
		super(parentShell);
		this.holder = provider;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		parent = (Composite) super.createDialogArea(parent);

		setTitle("Add a new property");
		setMessage("Type the key and the value of the property to create.");

		Composite textPanel = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().equalWidth(false).numColumns(2)
				.applyTo(textPanel);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL)
				.applyTo(textPanel);

		Label label = new Label(textPanel, SWT.NONE);
		label.setText("Key:");
		GridDataFactory labelDataFactory = GridDataFactory.fillDefaults()
				.align(SWT.END, SWT.CENTER);
		labelDataFactory.applyTo(label);

		GridDataFactory textDataFactory = GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false);
		keyText = new Text(textPanel, SWT.BORDER);
		textDataFactory.applyTo(keyText);

		Label label1 = new Label(textPanel, SWT.NONE);
		label1.setText("Value:");
		labelDataFactory.applyTo(label1);

		valueText = new Text(textPanel, SWT.BORDER);
		textDataFactory.applyTo(valueText);

		if (getKey() != null) {
			keyText.setText(getKey());
		}

		if (getValue() != null) {
			valueText.setText(getValue());
		}
		// Add listener after seting initial value

		valueText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				setValue(valueText.getText());

			}
		});

		keyText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {

				setKey(keyText.getText());

				getButton(IDialogConstants.OK_ID).setEnabled(
						!keyText.getText().isEmpty());

			}
		});

//		encrypt = new Button(textPanel, SWT.NULL);
//		if (EncryptionUtils.isEncrypted(getValue())) {
//			encrypt.setText("Decrypt");
//		} else {
//			encrypt.setText("Encrypt");
//		}
//		encrypt.addSelectionListener(new SelectionListener() {
//
//			@Override
//			public void widgetSelected(SelectionEvent arg0) {
//				if (EncryptionUtils.isEncrypted(getValue())) {
//					if (getEncryptionInformation() != null) {
//						decryptValue();
//					} else {
//						new EncryptPropertyAction(holder).run();
//						if (getEncryptionInformation() != null) {
//							decryptValue();
//						}
//					}
//				} else {
//					if (getEncryptionInformation() != null) {
//						encryptValue();
//					} else {
//						new EncryptPropertyAction(holder).run();
//						if (getEncryptionInformation() != null) {
//							encryptValue();
//						}
//					}
//				}
//
//				if (EncryptionUtils.isEncrypted(getValue())) {
//					encrypt.setText("Decrypt");
//				} else {
//					encrypt.setText("Encrypt");
//				}
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.horizontalSpan = 2;
//		encrypt.setLayoutData(gridData);

		return parent;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		Control result = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(
				!keyText.getText().isEmpty());
		return result;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

//	private void decryptValue() {
//		try {
//			valueText.setText(EncryptionUtils.decrypt(
//					getEncryptionInformation(), getValue()));
//		} catch (MuleEncryptionException e) {
//			getEncryptionInformationHolder().setEncryptionInformation(null);
//			
//			UIUtils.showException("Error while decrypt", "Error while decrypt.\nVerify the algorithm and key are correct.", e);
//		}
//	}

//	private void encryptValue() {
//		try {
//			valueText.setText(EncryptionUtils.encrypt(
//					getEncryptionInformation(), getValue()));
//		} catch (MuleEncryptionException e) {
//			getEncryptionInformationHolder().setEncryptionInformation(null);
//			UIUtils.showException("Error while encrypting", "Error while encrypt", e);
//		}
//	}

//	private EncryptionInformation getEncryptionInformation() {
//		return getEncryptionInformationHolder().getEncryptionInformation();
//	}
//
//	private IEncryptionInformationHolder getEncryptionInformationHolder() {
//		return holder.getPropertiesEditor().getEncryptionInformationHolder();
//	}

}
