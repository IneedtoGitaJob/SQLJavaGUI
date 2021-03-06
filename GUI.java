
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.JTextField;

import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;


public class GUI {
	//The connection to the database
	Connection connection = null;

    public GUI() {

        
        JFrame frame = new JFrame("GUI");
        JPanel Pan = new JPanel();

        //Drop down menu
        String[] optionsToChoose = {
            "root.properties",
            "client.properties",
            "db3.properties",
            "db4.properties"
        };
        JComboBox < String > jComboBox = new JComboBox < > (optionsToChoose);
        jComboBox.setBounds(100, 25, 140, 20);

        
        //text boxes
        JTextField InputUsername = new JTextField();
        JTextField InputPassword = new JTextField();
        JTextField InputCommand = new JTextField();
        JTextField Connection = new JTextField();
        Connection.setBounds(200, 275, 500, 25);
        Connection.setEditable(false);
        Connection.setText("No Connection Now");
        InputUsername.setBounds(75, 120, 150, 25);
        InputPassword.setBounds(75, 145, 150, 25);
        InputCommand.setBounds(400, 25, 300, 200);

        
        //JTable
        DefaultTableModel model = new DefaultTableModel();
        JTable Results = new JTable(model);
        JScrollPane pan = new JScrollPane(Results);
        //Results.setEnabled(false);
        pan.setBounds(50, 400, 800, 300);
        
        
        //static text
        JLabel ConnectionDetails = new JLabel("Connection Details                                                                                                                            Enter An SQL Command");
        JLabel PropertiesFile = new JLabel("Properties File");
        JLabel Username = new JLabel("Username");
        JLabel Password = new JLabel("Password");
        JLabel SQLExecutionResultWindow = new JLabel("SQL Execution Result Window");
        Dimension size2 = ConnectionDetails.getPreferredSize();
        ConnectionDetails.setBounds(0, 0, size2.width, size2.height);
        PropertiesFile.setBounds(0, 25, size2.width, size2.height);
        Username.setBounds(0, 125, size2.width, size2.height);
        Password.setBounds(0, 150, size2.width, size2.height);
        SQLExecutionResultWindow.setBounds(0, 350, size2.width, size2.height);

        
        //Buttons
        JButton ClearCommandButton = new JButton("Clear SQL Command");
        JButton ExecuteButton = new JButton("Execute SQL Command");
        JButton ConnectButton = new JButton("Connect to Database");
        JButton ClearResultButton = new JButton("Clear Results");
        ExecuteButton.setEnabled(false);
        Dimension size = ClearCommandButton.getPreferredSize();
        ClearCommandButton.setBounds(400, 225, size.width, size.height);
        ExecuteButton.setBounds(575, 225, size.width, size.height);
        ConnectButton.setBounds(0, 275, size.width, size.height);
        ClearResultButton.setBounds(0, 700, size.width, size.height);

        //Add to Pan
        //Text
        Pan.add(ConnectionDetails);
        Pan.add(PropertiesFile);
        Pan.add(Username);
        Pan.add(Password);

        //textfields
        Pan.add(InputUsername);
        Pan.add(InputPassword);
        Pan.add(InputCommand);
        Pan.add(Connection);
        
        //JTable (we add the wrapper)
        Pan.add(pan);
        
        //Buttons
        Pan.add(ClearCommandButton);
        Pan.add(ExecuteButton);
        Pan.add(ConnectButton);
        Pan.add(ClearResultButton);

        //Dropdown menu
        Pan.add(jComboBox);


        //Set layout to null so we can manually place Objects
        Pan.setLayout(null);
        Pan.setBackground(Color.GRAY);

        //Set Frame Settings
        //Exit Frame when closed
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Add Pan to Frame
        frame.add(Pan);
        //Set to fullscreen at first
        frame.setSize(1000, 850);
        //Make visible
        frame.setVisible(true);

        //Action Listeners
        ClearCommandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                InputCommand.setText("");
            }

        });

        //Clear results window
        ClearResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0);
                model.setColumnCount(0);
            }

        });

        //connect
        ConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    InputStream input = new FileInputStream("src\\" + (String) jComboBox.getSelectedItem());
                    //get properties
                    Properties p = new Properties();
                    p.load(input);

                    //get driver
                    try {
                        //"com.mysql.cj.jdbc.Driver"
                        Class.forName(p.getProperty("MYSQL_DB_DRIVER_CLASS"));
                    } catch (ClassNotFoundException a) {
                        JOptionPane.showMessageDialog(null, "Incorrect Driver");
                        return;
                    }


                    //Try and Connect
                    try {

                        if (p.getProperty("MYSQL_DB_USERNAME").equals(InputUsername.getText()) && p.getProperty("MYSQL_DB_PASSWORD").equals(InputPassword.getText())) {
                            connection = DriverManager.getConnection(p.getProperty("MYSQL_DB_URL"), p.getProperty("MYSQL_DB_USERNAME"), p.getProperty("MYSQL_DB_PASSWORD"));
                            Connection.setText(p.getProperty("MYSQL_DB_URL"));
                            ExecuteButton.setEnabled(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect Username or password");
                        }


                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(null, "Incorrect SQL url");
                    }


                } catch (IOException x) {
                    JOptionPane.showMessageDialog(null, "Properties file not found");
                }




            }

        });

        //Execute SQL script
        ExecuteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //We already must have a connection so we can easily connect here
                    Statement statement = connection.createStatement();
                    ResultSet myResult;

                    //Check if its a query or an update
                    if (InputCommand.getText().startsWith("select")) {
                    	

                        myResult = statement.executeQuery(InputCommand.getText());

                        ResultSetMetaData meta = myResult.getMetaData();

                    	
                        
                    	//CREATE THE RESULTS TABLE
                    	
                    	
                        int cols = meta.getColumnCount();
                        String[] tableColumnsName = new String[cols];

                        //Get Column name metadata
                        for (int x = 0; x < cols; x++) {
                            tableColumnsName[x] = meta.getColumnName(x + 1);
                        }
                        
                        model.setColumnIdentifiers(tableColumnsName);
                        model.addRow(tableColumnsName);
                        
                        //Fill model
                        while (myResult.next()) {
                            Object[] objects = new Object[cols];

                            for (int i = 0; i < cols; i++) {
                                objects[i] = myResult.getObject(i + 1);
                            }
                            model.addRow(objects);
                        }
                        
                        Results.setModel(model);
                        
                        
                        //Increase operationslog
                        
                        //Make a new connection unseen by the client to operationslog we can login as a root as the client doesnt actually have access 
                    	Connection OperationConnect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/operationslog","root","cgs2545");
                    	Statement OperationStatement = OperationConnect.createStatement();
                    	//Add to operationslog query
                    	OperationStatement.executeUpdate("UPDATE operationscount SET num_queries = num_queries + 1");
                     
                        
                        
                    } else {
                    	//Execute command
                        statement.executeUpdate(InputCommand.getText());
                        
                        //Increase operationslog
                        
                      //Make a new connection unseen by the client to operationslog we can login as a root as the client doesnt actually have access
                    	Connection OperationConnect = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/operationslog","root","cgs2545");
                    	Statement OperationStatement = OperationConnect.createStatement();
                    	//Add to operationslog query
                    	OperationStatement.executeUpdate("UPDATE operationscount SET num_updates = num_updates + 1");
                    }

                  //If a SQL command is not valid
                } catch (SQLException e1) {

                	JOptionPane.showMessageDialog(null, "Incorrect SQL command");
                }
            }



        });

    }

}