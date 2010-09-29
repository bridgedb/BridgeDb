package org.bridgedb.util.taverna.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

import org.bridgedb.util.taverna.BridgeDbActivity;
import org.bridgedb.util.taverna.BridgeDbActivityConfigurationBean;

@SuppressWarnings("serial")
public class BridgeDbConfigureAction
		extends
		ActivityConfigurationAction<BridgeDbActivity, BridgeDbActivityConfigurationBean> {

	public BridgeDbConfigureAction(BridgeDbActivity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog<BridgeDbActivity, BridgeDbActivityConfigurationBean> currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		BridgeDbConfigurationPanel panel = new BridgeDbConfigurationPanel(
				getActivity());
		ActivityConfigurationDialog<BridgeDbActivity, BridgeDbActivityConfigurationBean> dialog = new ActivityConfigurationDialog<BridgeDbActivity, BridgeDbActivityConfigurationBean>(
				getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);

	}

}
