package com.cisco.avature;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Authenticate extends Dialog {

	protected Object result;
	protected Shell shlLogin;
	private Text txtUsername;
	private Text txtPassword;
	private String username;
	private String password;
	private boolean cancelButton = false;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public Authenticate(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlLogin.open();
		shlLogin.layout();
		Display display = getParent().getDisplay();
		while (!shlLogin.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlLogin = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlLogin.setSize(450, 211);
		shlLogin.setText("Login");
		
		Label lblPassword = new Label(shlLogin, SWT.NONE);
		lblPassword.setBounds(10, 73, 55, 15);
		lblPassword.setText("Password:");
		
		Label lblUserName = new Label(shlLogin, SWT.NONE);
		lblUserName.setText("Username:");
		lblUserName.setBounds(10, 32, 55, 15);
		
		txtUsername = new Text(shlLogin, SWT.BORDER);
		txtUsername.setBounds(83, 19, 306, 35);
		
		txtPassword = new Text(shlLogin, SWT.BORDER);
		txtPassword.setBounds(83, 60, 306, 35);
		
		Button btnCancel = new Button(shlLogin, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelButton = true;
				shlLogin.close();
			}
		});
		btnCancel.setBounds(287, 124, 102, 35);
		btnCancel.setText("Cancel");
		
		Button btnLogIn = new Button(shlLogin, SWT.NONE);
		btnLogIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setUsername(txtUsername.getText());
				setPassword(txtPassword.getText());
				if ((!getUsername().isEmpty()) && (!getPassword().isEmpty())){
				shlLogin.close();

				}
			}
		});
		btnLogIn.setText("Set Login");
		btnLogIn.setBounds(83, 124, 102, 35);

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isCancelButton() {
		return cancelButton;
	}

	public void setCancelButton(boolean cancelButton) {
		this.cancelButton = cancelButton;
	}
}
