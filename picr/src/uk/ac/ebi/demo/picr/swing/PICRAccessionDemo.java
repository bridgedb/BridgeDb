package uk.ac.ebi.demo.picr.swing;

import uk.ac.ebi.demo.picr.business.PICRClient;
import uk.ac.ebi.demo.picr.soap.UPEntry;
import uk.ac.ebi.demo.picr.soap.CrossReference;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.ws.soap.SOAPFaultException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 *    Copyright 2007 - European Bioinformatics Institute
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * User: rcote
 * Date: 17-May-2007
 * Time: 10:37:54
 * $Id: $
 */
public class PICRAccessionDemo extends JPanel {

    //class that will do the communication with the webservice
    private PICRClient client = new PICRClient();

    public PICRAccessionDemo() {

        //set general layout
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        add(Box.createVerticalStrut(5));

        //create components
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.add(Box.createHorizontalStrut(5));
        row1.setBorder(BorderFactory.createTitledBorder(""));
        row1.add(new JLabel("Accession:"));
        row1.add(Box.createHorizontalStrut(10));
        final JTextField accessionField = new JTextField(10);
        accessionField.setMaximumSize(accessionField.getPreferredSize());
        row1.add(Box.createHorizontalStrut(10));

        row1.add(accessionField);
        row1.add(Box.createHorizontalGlue());

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.setBorder(BorderFactory.createTitledBorder("Target Databases"));
        final JList databaseList = new JList();
        JScrollPane listScroller = new JScrollPane(databaseList);
        listScroller.setMaximumSize(new Dimension(100, 10));
        JButton loadDBButton = new JButton("Load Databases");
        row2.add(listScroller);
        row2.add(loadDBButton);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox onlyActiveCheckBox = new JCheckBox("Only Active");
        onlyActiveCheckBox.setSelected(true);
        row3.add(new JLabel("Options:  "));
        row3.add(onlyActiveCheckBox);

        add(row1);
        add(row2);
        add(row3);

        final String[] columns = new String[]{"Database", "Accession", "Version", "Taxon ID"};
        final JTable dataTable = new JTable(new Object[0][0], columns);
        dataTable.setShowGrid(true);
        add(new JScrollPane(dataTable));

        JPanel buttonPanel = new JPanel();
        JButton mapAccessionButton = new JButton("Generate Mapping!");
        buttonPanel.add(mapAccessionButton);
        add(buttonPanel);

        //create listeners!

        //clear previous accession on focus
        accessionField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                accessionField.setText("");
            }

            public void focusLost(FocusEvent e) {
            }
        });

        //update boolean flag in communication class
        onlyActiveCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                client.setOnlyActive(((JCheckBox) e.getSource()).isSelected());
            }
        });

        //performs mapping call and updates interface with results
        mapAccessionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    if (!"".equals(accessionField.getText())) {

                        java.util.List<UPEntry> entries = client.performAccessionMapping(accessionField.getText(), databaseList.getSelectedValues());

                        //compute size of arary
                        if (entries != null) {
                            int size = 0;
                            for (UPEntry entry : entries) {
                                for (CrossReference xref : entry.getIdenticalCrossReferences()) {
                                    size++;
                                }
                                for (CrossReference xref : entry.getLogicalCrossReferences()) {
                                    size++;
                                }
                            }

                            if (size > 0) {

                                final Object[][] data = new Object[size][4];
                                int i = 0;
                                for (UPEntry entry : entries) {
                                    for (CrossReference xref : entry.getIdenticalCrossReferences()) {
                                        data[i][0] = xref.getDatabaseName();
                                        data[i][1] = xref.getAccession();
                                        data[i][2] = xref.getAccessionVersion();
                                        data[i][3] = xref.getTaxonId();
                                        i++;
                                    }
                                    for (CrossReference xref : entry.getLogicalCrossReferences()) {
                                        data[i][0] = xref.getDatabaseName();
                                        data[i][1] = xref.getAccession();
                                        data[i][2] = xref.getAccessionVersion();
                                        data[i][3] = xref.getTaxonId();
                                        i++;
                                    }
                                }

                                //refresh
                                DefaultTableModel dataModel = new DefaultTableModel();
                                dataModel.setDataVector(data, columns);
                                dataTable.setModel(dataModel);

                                System.out.println("update done");

                            } else {
                                JOptionPane.showMessageDialog(null, "No Mappind data found.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "No Mappind data found.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "You must enter an accession to map.");
                    }
                } catch (SOAPFaultException soapEx) {
                    JOptionPane.showMessageDialog(null, "A SOAP Error occurred.");
                    soapEx.printStackTrace();
                }
            }
        });

        //loads list of mapping databases from communication class
        loadDBButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {

                    java.util.List<String> databases = client.loadDatabases();
                    if (databases != null && databases.size() > 0) {

                        databaseList.setListData(databases.toArray());
                        System.out.println("database refresh done");

                    } else {
                        JOptionPane.showMessageDialog(null, "No Databases Loaded!.");
                    }

                } catch (SOAPFaultException soapEx) {
                    JOptionPane.showMessageDialog(null, "A SOAP Error occurred.");
                    soapEx.printStackTrace();
                }
            }
        });


    }


    public static void main(String[] args) {

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Map By Accession Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new PICRAccessionDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }


}
