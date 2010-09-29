package org.bridgedb.util.taverna.ui.view;

import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

import org.bridgedb.util.taverna.BridgeDbActivity;
import org.bridgedb.util.taverna.BridgeDbActivityConfigurationBean;
import org.bridgedb.util.taverna.ui.config.BridgeDbConfigureAction;

@SuppressWarnings("serial")
public class BridgeDbContextualView extends ContextualView {
	private final BridgeDbActivity activity;
	private JLabel description = new JLabel("ads");

	public BridgeDbContextualView(BridgeDbActivity activity) {
		this.activity = activity;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		JPanel jPanel = new JPanel();
		jPanel.add(description);
		refreshView();
		return jPanel;
	}

	@Override
	public String getViewTitle() {
		BridgeDbActivityConfigurationBean configuration = activity
				.getConfiguration();
//		return "Example service " + configuration.getExampleString();
		return "";
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		BridgeDbActivityConfigurationBean configuration = activity
				.getConfiguration();
//		description.setText("Example service " + configuration.getExampleUri()
//				+ " - " + configuration.getExampleString());
		// TODO: Might also show extra service information looked
		// up dynamically from endpoint/registry
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// We want to be on top
		return 100;
	}
	
	@Override
	public Action getConfigureAction(final Frame owner) {
		return new BridgeDbConfigureAction(activity, owner);
	}

}
