// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.batchmapper;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Lookup implements ActionListener
{
	private JFrame frame;
	private JTextField txtSearch;
	private JButton btnGo;
	private JButton btnCancel;
	
	public void createAndShowGUI()
	{
		frame = new JFrame();
		frame.setTitle ("Identifier Lookup tool");
		
		btnGo = new JButton ("Search");
		btnGo.addActionListener(this);
		btnCancel = new JButton ("Cancel");
		btnCancel.addActionListener(this);
		
		JPanel pnlButtons = new JPanel();
		pnlButtons.add (btnGo);
		pnlButtons.add (btnCancel);
		
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add (pnlButtons, BorderLayout.NORTH);
		
		frame.add (txtSearch);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Look up biologiacal identifiers by symbol or related identifier,
	 * using the bridgeDb API.
	 * 
	 * Gives out linkouts, cross references and background information
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Lookup lookup = new Lookup();
		lookup.createAndShowGUI();
	}

	public void actionPerformed(ActionEvent ae) 
	{
		if (ae.getSource() == btnCancel)
		{
			frame.dispose();
		}
		else if (ae.getSource() == btnGo)
		{
			JOptionPane.showMessageDialog(frame, "Not implemented");
		}
	}

}
