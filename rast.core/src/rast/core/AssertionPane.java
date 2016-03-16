package rast.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.accessibility.Accessible;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.UIManager;

import sun.awt.AppContext;

/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 *
 */
public class AssertionPane extends JComponent implements Accessible {

	private static final long serialVersionUID = 1L;
	private boolean assertionResult;

	private static final Object sharedFrameKey = AssertionPane.class;
	private static final Object sharedOwnerFrameKey = new StringBuffer(
			"SwingUtilities.sharedOwnerFrame");

	public AssertionPane() {
		updateUI();
	}

	public static boolean showAssertTrueDialog(Component parentComponent,
			Component message, String title) {
		AssertionPane pane = new AssertionPane();
		pane.setComponentOrientation(((parentComponent == null) ? getRootFrame()
				: parentComponent).getComponentOrientation());

		AssertionDialog dialog = pane.createDialog(parentComponent, message, title);
		dialog.setVisible(true);
		dialog.dispose();

		return pane.getAssertionResult(); // InputValue();
	}

	/**
	 * http://kickjava.com/src/javax/swing/JOptionPane.java.htm
	 */
	private AssertionDialog createDialog(Component parentComponent, Component message, String title) {
		final AssertionDialog dialog;

		Window window = getWindowForComponent(parentComponent);
		if (window instanceof Frame) {
			dialog = new AssertionDialog((Frame) window, title);
		} else {
			dialog = new AssertionDialog((Dialog) window, title);
		}
		dialog.setComponentOrientation(this.getComponentOrientation());

		Container contentPane = dialog.getContentPane();
		JLabel label = new JLabel("What do you think about below image?");
		
		contentPane.add(label, BorderLayout.NORTH);
		
		contentPane.add(message, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		JButton buttonValidate = new JButton("Validate");
		buttonValidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAssertionResult(true);
				dialog.setVisible(false);
				dialog.dispose();		
				
			}});
		buttonsPanel.add(buttonValidate);
		JButton buttonInvalidate = new JButton("Invalidate");
		buttonInvalidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setAssertionResult(false);
				dialog.setVisible(false);
				dialog.dispose();		
				
			}});
		buttonsPanel.add(buttonInvalidate);	
		contentPane.add(buttonsPanel, BorderLayout.SOUTH);
		dialog.setResizable(false);
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = UIManager.getLookAndFeel()
					.getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				dialog.setUndecorated(true);
				getRootPane().setWindowDecorationStyle(
						JRootPane.QUESTION_DIALOG);
			}
		}
		dialog.pack();
		dialog.setLocationRelativeTo(parentComponent);
		WindowAdapter adapter = new WindowAdapter() {
			private boolean gotFocus = false;

			public void windowClosing(WindowEvent we) {
				setAssertionResult(false);
			}

			public void windowGainedFocus(WindowEvent we) {
				// Once window gets focus, set initial focus
				if (!gotFocus) {
					setAssertionResult(false);
					gotFocus = true;
				}
			}
		};
		dialog.addWindowListener(adapter);
		dialog.addWindowFocusListener(adapter);
		dialog.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				// reset value to ensure closing works properly
				setAssertionResult(false);
			}
		});
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				// Let the defaultCloseOperation handle the closing
				// if the user closed the window without selecting a button
				// (newValue = null in that case). Otherwise, close the dialog.
				if (dialog.isVisible()
						&& event.getSource() == AssertionPane.this
						&& event.getNewValue() != null
						&& event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE) {
					dialog.setVisible(false);
				}
			}
		});
		return dialog;
	}

	private boolean getAssertionResult() {
		return this.assertionResult;
	}

	public void setAssertionResult(boolean assertionResult) {
		this.assertionResult = assertionResult;
	}

	/**
	 * http://kickjava.com/src/javax/swing/JOptionPane.java.htm
	 * JOptionPane.getRootFrame() de çağırılabilir.
	 */
	public static Frame getRootFrame() throws HeadlessException {
		Frame sharedFrame = (Frame) appContextGet(sharedFrameKey);
		if (sharedFrame == null) {
			sharedFrame = getSharedOwnerFrame();
			appContextPut(sharedFrameKey, sharedFrame);
		}
		return sharedFrame;
	}

	private static Frame getSharedOwnerFrame() throws HeadlessException {
		Frame sharedOwnerFrame = (Frame) appContextGet(sharedOwnerFrameKey);
		if (sharedOwnerFrame == null) {
			sharedOwnerFrame = new Frame();
			appContextPut(sharedOwnerFrameKey, sharedOwnerFrame);
			System.out.println("sharedOwnerFrame == null");
		}
		return sharedOwnerFrame;
	}

	/**
	 * http://kickjava.com/src/javax/swing/SwingUtilities.java.htm
	 * 
	 */
	private static Object appContextGet(Object key) {
		return AppContext.getAppContext().get(key);
	}

	/**
	 * http://kickjava.com/src/javax/swing/SwingUtilities.java.htm
	 * 
	 */
	private static void appContextPut(Object key, Object value) {
		AppContext.getAppContext().put(key, value);
	}

	private static Window getWindowForComponent(Component parentComponent)
			throws HeadlessException {
		if (parentComponent == null)
			return getRootFrame();
		if (parentComponent instanceof Frame
				|| parentComponent instanceof Dialog)
			return (Window) parentComponent;
		return getWindowForComponent(parentComponent.getParent());
	}
}
