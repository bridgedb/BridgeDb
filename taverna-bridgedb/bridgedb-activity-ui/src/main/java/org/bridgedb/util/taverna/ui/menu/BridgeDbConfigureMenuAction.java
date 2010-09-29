package org.bridgedb.util.taverna.ui.menu;

import javax.swing.Action;

import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.bridgedb.util.taverna.BridgeDbActivity;
import org.bridgedb.util.taverna.ui.config.BridgeDbConfigureAction;

public class BridgeDbConfigureMenuAction extends
		AbstractConfigureActivityMenuAction<BridgeDbActivity> {

	public BridgeDbConfigureMenuAction() {
		super(BridgeDbActivity.class);
	}

	protected Action createAction() {
		BridgeDbActivity a = findActivity();
		Action result = null;
		result = new BridgeDbConfigureAction(findActivity(),
				getParentFrame());
		result.putValue(Action.NAME, "Configure example service");
		addMenuDots(result);
		return result;
	}

}
