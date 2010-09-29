package org.bridgedb.util.taverna.ui.view;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

import org.bridgedb.util.taverna.BridgeDbActivity;

public class BridgeDbActivityContextViewFactory implements
		ContextualViewFactory<BridgeDbActivity> {

	public boolean canHandle(Object selection) {
		return selection instanceof BridgeDbActivity;
	}

	public List<ContextualView> getViews(BridgeDbActivity selection) {
		return Arrays.<ContextualView>asList(new BridgeDbContextualView(selection));
	}
	
}
