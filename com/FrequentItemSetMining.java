package com;

import com.fp.FPGrowth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;  
import java.text.NumberFormat;
import java.util.*;

class GUI
{
	GUI()
	{
		JFrame jfrm = new JFrame("Frequent Item Set Mining");
		jfrm.setLayout(new GridBagLayout());
		jfrm.setSize(500,300);
		GridBagConstraints c = new GridBagConstraints();
		
		JLabel hostLabel = new JLabel("host:port");
		c.gridx = 0;
		c.gridy = 0;
		jfrm.add(hostLabel ,c);	

		JTextField hostTextField = new JTextField(14);
		c.gridx = 1;
		c.gridy = 0;
		jfrm.add(hostTextField , c);

		JLabel usernameLabel = new JLabel("username");
		c.gridx = 0;
		c.gridy = 1;
		jfrm.add(usernameLabel ,c);	

		JTextField usernameTextField = new JTextField(14);
		c.gridx = 1;
		c.gridy = 1;
		jfrm.add(usernameTextField , c);

		JLabel passwordLabel = new JLabel("password");
		c.gridx = 0;
		c.gridy = 2;
		jfrm.add(passwordLabel ,c);	

		JPasswordField passwordTextField = new JPasswordField(14);
		c.gridx = 1;
		c.gridy = 2;
		jfrm.add(passwordTextField , c);
		
		JLabel supportLabel = new JLabel("Support(%)");
		c.gridx = 0;
		c.gridy = 3;
		jfrm.add(supportLabel ,c);	

		JFormattedTextField supportTextField = 
			new JFormattedTextField(NumberFormat.getNumberInstance());
		supportTextField.setColumns(14);
		c.gridx = 1;
		c.gridy = 3;
		jfrm.add(supportTextField , c);
		
		JLabel confidenceLabel = new JLabel("Confidence(%)");
		c.gridx = 0;
		c.gridy = 4;
		jfrm.add(confidenceLabel ,c);	

		JFormattedTextField confidenceTextField = 
			new JFormattedTextField(NumberFormat.getNumberInstance());
		confidenceTextField.setColumns(14);
		c.gridx = 1;
		c.gridy = 4;
		jfrm.add(confidenceTextField , c);
		
		JLabel dataSetPath = new JLabel("DataSet(*.csv) ");
		c.gridx = 0;
		c.gridy = 5;
		jfrm.add(dataSetPath, c);	

		JTextField dataSetPathTextField = new JTextField(14);
		c.gridx = 1;
		c.gridy = 5;
		jfrm.add(dataSetPathTextField, c);
		
		JButton executeButton = new JButton("         Execute         ");
		c.gridx = 1;
		c.gridy = 6;
		jfrm.add(executeButton, c);
		
		JButton recommendationButton = new JButton("Recommendation");
		c.gridx = 1;
		c.gridy = 7;
		jfrm.add(recommendationButton, c);
		
		jfrm.setVisible(true);
		
		executeButton.addActionListener( new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) 
			{
				executeButton.setEnabled(false);
				try
				{ 
					// "jdbc:mysql://localhost:3306/?","username","password"
					Connection con = DriverManager.getConnection
						("jdbc:mysql://" + hostTextField.getText() + "/?" ,
						usernameTextField.getText(),
						String.valueOf(passwordTextField .getPassword()));  
					
					if(supportTextField.getValue() != null) 
					{
						double support = Double.valueOf(supportTextField.getText());
						if(support >= 0.0 && support <= 100.0) 
						{
							if(confidenceTextField.getValue() != null) 
							{
								double confidence = Double.valueOf(confidenceTextField.getText());
								if(confidence >= 0.0 && confidence <= 100.0) 
								{
									String csvPath = dataSetPathTextField.getText();
									if(csvPath.lastIndexOf(".csv") != -1)
									{
										FPGrowth fp = new FPGrowth(csvPath, support, confidence);
										if(fp.success)
										{
											Statement stmt = con.createStatement();
											stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS FP;");
											stmt.executeUpdate("use FP");
											stmt.executeUpdate("DROP TABLE IF EXISTS support;");
											stmt.executeUpdate("DROP TABLE IF EXISTS confidence;");
											stmt.executeUpdate("CREATE TABLE `support` (`Frequent ItemSet` text, `Support` BIGINT);");
											stmt.executeUpdate("CREATE TABLE `confidence` (`Association Rules` text, `Confidence` DECIMAL(5,2));");
											
											HashMap<ArrayList<String>, Long> map1 =	fp.getFrequentItemSets();
											Iterator<Map.Entry<ArrayList<String>, Long>> iterator = map1.entrySet().iterator();
											Map.Entry<ArrayList<String>, Long> entry;
											while(iterator.hasNext())	
											{
												entry = iterator.next();
												stmt.executeUpdate("insert into `support` values(\"" + entry.getKey() + "\",\"" + entry.getValue() + "\");");
											}
											
											HashMap<String, Double> map2 =	fp.getAssociationRules();
											Iterator<Map.Entry<String, Double>> iterator0 = map2.entrySet().iterator();
											Map.Entry<String, Double> entry0;
											while(iterator0.hasNext())	
											{
												entry0 = iterator0.next();
												stmt.executeUpdate("insert into `confidence` values(\"" + entry0.getKey() + "\",\"" + entry0.getValue() + "\");");
											}
										}
										else
										{
											
											JOptionPane.showMessageDialog(jfrm, fp.errorMessage, 
													"Error", JOptionPane.ERROR_MESSAGE);
										}
									}
									else
									{
										JOptionPane.showMessageDialog(jfrm, "(only *.csv file is supported)", 
													"Error", JOptionPane.ERROR_MESSAGE);
									}
								}
								else 
								{
									JOptionPane.showMessageDialog(jfrm, "Valid confidence range[0-100]", 
										"Error", JOptionPane.ERROR_MESSAGE);
								}
							} 
							else 
							{
								JOptionPane.showMessageDialog(jfrm, "Confidence can't be empty!", 
									"Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						else 
						{
							JOptionPane.showMessageDialog(jfrm, "Valid support range[0-100]", 
								"Error", JOptionPane.ERROR_MESSAGE);
						}
					} 
					else 
					{
						JOptionPane.showMessageDialog(jfrm, "Support can't be empty!", 
							"Error", JOptionPane.ERROR_MESSAGE);
					}
					con.close();
				}
				catch(SQLException e)
				{
					JOptionPane.showMessageDialog(jfrm, "Database connection error (" + e + ")", 
						"Error", JOptionPane.ERROR_MESSAGE);
				}
				catch(Exception e)
				{
					JOptionPane.showMessageDialog(jfrm, "File error (" + e + ")", 
						"Error", JOptionPane.ERROR_MESSAGE);
				}
				executeButton.setEnabled(true);
			}
		});
		
		recommendationButton.addActionListener( new ActionListener() 
		{
			public void actionPerformed(ActionEvent ae) 
			{
				jfrm.setEnabled(false);
				JFrame jfrm1 = new JFrame("Recommended Products");
				jfrm1.setLayout(new GridBagLayout());
				jfrm1.setSize(500,400);
				GridBagConstraints c1 = new GridBagConstraints();
				
				try
				{
					// "jdbc:mysql://localhost:3306/?","username","password"
					Connection con = DriverManager.getConnection
							("jdbc:mysql://" + hostTextField.getText() + "/?" ,
							usernameTextField.getText(),
							String.valueOf(passwordTextField .getPassword()));
					Statement stmt = con.createStatement();
					stmt.executeUpdate("use FP");
					ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'confidence'");  
					if(rs.next() == false)
					{
						JOptionPane.showMessageDialog(jfrm1, 
											"No Data Available",
											"Error",
											JOptionPane.ERROR_MESSAGE);
						jfrm1.dispose();
						con.close();
						jfrm.setEnabled(true);
						return;
					}
					rs = stmt.executeQuery("SELECT * FROM `confidence`;");
					HashMap<String, HashMap<String, ArrayList<Double>>> recommended_products = 
							new HashMap<String, HashMap<String, ArrayList<Double>>>();
					HashMap<String, ArrayList<Double>> temp = null;
					ArrayList<Double> temp1 = null;
					while(rs.next())
					{
						String[] s = rs.getString(1).split(" -> ");
						double confidenceTemp = rs.getDouble(2);
						String[] tempString1 = s[0].substring(1, s[0].length()-1).split(",");
						String[] tempString2 = s[1].substring(1, s[1].length()-1).split(",");
						
						for(String x : tempString1)
						{
							x = x.trim();
							if(recommended_products.containsKey(x))
							{
								temp = recommended_products.get(x);
								for(String x1 : tempString2)
								{
									x1 = x1.trim();
									if(temp.containsKey(x1))
									{
										temp1 = temp.get(x1);
										double d1 = temp1.get(0);
										double d2 = temp1.get(1);
										d1 += confidenceTemp;
										d2 += 1.0;
										temp1.clear();
										temp1.add(d1);
										temp1.add(d2);
									}
									else
									{
										temp1 = new ArrayList<Double>();
										temp1.add(confidenceTemp);
										temp1.add(1.0);
										temp.put(x1, temp1);
									}
								}
								recommended_products.put(x, temp);
							}
							else
							{
								temp = new HashMap<String, ArrayList<Double>>();
								for(String x1 : tempString2)
								{
									x1 = x1.trim();
									temp1 = new ArrayList<Double>();
									temp1.add(confidenceTemp);
									temp1.add(1.0);
									temp.put(x1, temp1);
								}
								recommended_products.put(x, temp);
							}
						}
					}
					rs.close();
					con.close();
					
					JPanel panelTop = new JPanel();    
					c1.gridx = 0;
					c1.gridy = 0;
					jfrm1.add(panelTop, c1);
					
					JLabel itemLabel = new JLabel("item(s)"); 
					panelTop.add(itemLabel);

					Object[] list_of_items = recommended_products.keySet().toArray();
					Arrays.sort(list_of_items);
					JComboBox<Object> cb = new JComboBox<>(list_of_items);  
					panelTop.add(cb);
					
					JButton recommendButton = new JButton("Recommend");
					panelTop.add(recommendButton);
					
					JPanel panelBottom = new JPanel();
					c1.gridx = 0;
					c1.gridy = 1;
					jfrm1.add(panelBottom, c1);
					
					recommendButton.addActionListener( new ActionListener() 
					{
						@SuppressWarnings("unchecked")
						public void actionPerformed(ActionEvent ae) 
						{
							String str = (String)cb.getSelectedItem();
							if(str == null)
							{
								return;
							}
							HashMap<String, ArrayList<Double>> temp = recommended_products.get(str);
							Set s = temp.entrySet();
							Iterator iterator = s.iterator();
							RecommendedSet[] set = new RecommendedSet[temp.size()];
							int i = 0;
							while(iterator.hasNext())	
							{
								Map.Entry entry = (Map.Entry)iterator.next();
								ArrayList<Double> d = (ArrayList<Double>)entry.getValue();
								double value = (double)d.get(0) / (double)d.get(1);
								set[i] = new RecommendedSet((String)entry.getKey(), value);
								i++;
							}
							Arrays.sort(set);
							String[][] row_col = new String[temp.size()][2];
							for(i = 0; i < temp.size(); i++)
							{
								row_col[i][0] = set[i].key;
								row_col[i][1] = 
									String.valueOf(Math.round(set[i].value * 100.0) / 100.0);
							}
							JTable jt = new JTable(row_col, new String[]{"Product", "%"});
							JScrollPane scrollPane = new JScrollPane(jt);
							panelBottom.removeAll();
							panelBottom.add(scrollPane);
							panelBottom.validate();
							panelBottom.repaint();
						}
					});
					jfrm1.setVisible(true);	
				}
				catch(SQLException e)
				{
					JOptionPane.showMessageDialog(jfrm1, "Database connection error (" + e + ")", 
						"Error", JOptionPane.ERROR_MESSAGE);
					jfrm1.dispose();
					jfrm.setEnabled(true);
					return;
				}
				jfrm1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jfrm1.addWindowListener(new WindowAdapter() 
				{
					@Override
					public void windowClosing(WindowEvent event) 
					{
						jfrm.setEnabled(true);
					}
				});
			}
		});
		
		jfrm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}


public class FrequentItemSetMining
{
	public static void main(String[] args)
	{		
		SwingUtilities.invokeLater(new Runnable() { public void run() { new GUI(); } } );
	}
}