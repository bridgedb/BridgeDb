package org.bridgedb.cytoscape;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyMenus;

public class CyBridgeDbPlugin extends CytoscapePlugin
{	
	public CyBridgeDbPlugin()
	{
		CyMenus menus = Cytoscape.getDesktop().getCyMenus();
		menus.addCytoscapeAction (new SelectGdb());
	}

	public class SelectGdb extends CytoscapeAction
	{
		@Override
		protected void initialize()
		{
			super.initialize();
			putValue (NAME, "Select Derby synonym database");		   
		}
		
		@Override
		public String getPreferredMenu()
		{
			return "Plugins";
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Action not implemented");			
		}

		@Override
		public boolean isInMenuBar()
		{
			return true;
		}
	}
}