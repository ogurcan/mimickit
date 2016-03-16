package rast.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowEvent;

import javax.accessibility.Accessible;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

/**
 * @author Önder Gürcan
 * @version $Revision$ $Date$
 *
 */
public class AssertionDialog extends Dialog implements WindowConstants,
		Accessible, RootPaneContainer {

	private static final long serialVersionUID = 1L;
	private JRootPane rootPane;
	private boolean rootPaneCheckingEnabled;
	private int defaultCloseOperation = DO_NOTHING_ON_CLOSE;

	public AssertionDialog(Frame owner, String title) {
		super(owner, title, true);
		setLocale(JComponent.getDefaultLocale());
		setRootPane(createRootPane());
		setRootPaneCheckingEnabled(true);
		//
		if (JDialog.isDefaultLookAndFeelDecorated()) {
			boolean supportsWindowDecorations = UIManager.getLookAndFeel()
					.getSupportsWindowDecorations();
			if (supportsWindowDecorations) {
				setUndecorated(true);
				getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
			}
		}
		//sun.awt.SunToolkit.checkAndSetPolicy(this); //, true);		
	}

	public AssertionDialog(Dialog owner, String title) {
		super(owner, title, true);
		setRootPane(createRootPane());
		setRootPaneCheckingEnabled(true);
	}

	public Container getContentPane() {
		return getRootPane().getContentPane();
	}

	public JRootPane getRootPane() {
		return rootPane;
	}

	protected void setRootPane(JRootPane root) {
		if (rootPane != null) {
			remove(rootPane);
		}
		rootPane = root;
		if (rootPane != null) {
			boolean checkingEnabled = isRootPaneCheckingEnabled();
			try {
				setRootPaneCheckingEnabled(false);
				add(rootPane, BorderLayout.CENTER);
			} finally {
				setRootPaneCheckingEnabled(checkingEnabled);
			}
		}
	}

	protected JRootPane createRootPane() {
		JRootPane rp = new JRootPane();
		rp.setOpaque(true);
		return rp;
	}

	protected boolean isRootPaneCheckingEnabled() {
		return rootPaneCheckingEnabled;
	}

	protected void setRootPaneCheckingEnabled(boolean enabled) {
		rootPaneCheckingEnabled = enabled;
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public Component getGlassPane() {
		return getRootPane().getGlassPane();
	}

	@Override
	public JLayeredPane getLayeredPane() {
		return getRootPane().getLayeredPane();
	}

	@Override
	public void setContentPane(Container arg0) {
		getRootPane().setContentPane(arg0);

	}

	@Override
	public void setGlassPane(Component arg0) {
		getRootPane().setGlassPane(arg0);
	}

	@Override
	public void setLayeredPane(JLayeredPane arg0) {
		getRootPane().setLayeredPane(arg0);
	}

	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);

		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			switch (defaultCloseOperation) {
			case HIDE_ON_CLOSE:
				setVisible(false);
				break;
			case DISPOSE_ON_CLOSE:
				setVisible(false);
				dispose();
				break;
			case DO_NOTHING_ON_CLOSE:
			default:
				break;
			}
		}
	}

}
