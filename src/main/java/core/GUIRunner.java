package core;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.JSONObject;
 
@SuppressWarnings("unused")
public class GUIRunner extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final String[] TYPES = {"ignored", "watched", "short_term", "long_term"};
	
    JButton applyButton = new JButton("Save Changes");
    JButton updateButton = new JButton("Upload Changes");
    GridLayout experimentLayout = new GridLayout(0,3);
    
    private static JSONObject userPref;
    
    private static String[] choices;
    private static String currentCurrency;
    private static String currentType;
    /*
    private static double currentInit;
    private static double currentThreshold;
    private static double currentSafety;
    */
     
    public GUIRunner(String name) {
        super(name);
        setResizable(false);
    }
     
    public void initGaps() {
    	String data = S3Pull.readObject("user_pref/user_preferences.json");
	    userPref = new JSONObject(data);
	    int size = userPref.length();

	    choices = new String[size];
	    
	    for(int i = 0; i < size; i++) {
	    	choices[i] = userPref.names().getString(i);
	    }
	    
	    currentCurrency = choices[0];
	    currentType = userPref.getJSONObject(choices[0]).getString("type");
	    /*
	    currentInit = userPref.getJSONObject(choices[0]).getDouble("initial");
	    currentThreshold = userPref.getJSONObject(choices[0]).getDouble("threshold");
	    currentSafety = userPref.getJSONObject(choices[0]).getDouble("ditch");
	    */
    }
    
    public void updateWithCurrency(String code) {
    	currentCurrency = code;
	    currentType = userPref.getJSONObject(code).getString("type");
	    /*
	    currentInit = userPref.getJSONObject(code).getDouble("initial");
	    currentThreshold = userPref.getJSONObject(code).getDouble("threshold");
	    currentSafety = userPref.getJSONObject(code).getDouble("ditch");
	    */
    }
     
    public void addComponentsToPane(final Container pane) {
        initGaps();
        final JPanel compsToExperiment = new JPanel();
        experimentLayout.setHgap(20);
        compsToExperiment.setLayout(experimentLayout);
        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(0,2));
        compsToExperiment.setPreferredSize(new Dimension(600, 100));
        
        final JComboBox<String> currencySelect = new JComboBox<String>(choices);
        final JComboBox<String> typeSelect = new JComboBox<String>(TYPES);
        /*
        final JTextField initialSelect = new JTextField(12);
        final JTextField threshSelect = new JTextField(12);
        final JTextField safetySelect = new JTextField(12);
        */
        
        DecimalFormat df = new DecimalFormat("###0.00");
        JLabel[] currentData = new JLabel[2];
        currentData[0] = new JLabel(currentCurrency);
        currentData[1] = new JLabel(currentType);
        /*
        currentData[2] = new JLabel("$" + df.format(currentInit));
        currentData[3] = new JLabel("$" + df.format(currentThreshold));
        currentData[4] = new JLabel("$" + df.format(currentSafety));
        */
        
        
        JLabel[] staticText = {new JLabel("Select Currency"), new JLabel("Select Investment Type") }; 
        /*,
        		new JLabel("Set Desired Investment Value"), new JLabel("Set Amount Above Initial In Which to Scalp"),
        		new JLabel("Set Safety Net To Instantly Sell")
        };
        */
        
        for(int i = 0; i < currentData.length; i++) {
        	currentData[i].setFont(new Font("Calibri", Font.PLAIN, 14));
        	staticText[i].setFont(new Font("Calibri", Font.PLAIN, 14));
        }
        
        currencySelect.setFont(new Font("Calibri", Font.PLAIN, 14));
        typeSelect.setFont(new Font("Calibri", Font.PLAIN, 14));
        /*
        initialSelect.setFont(new Font("Calibri", Font.PLAIN, 14));
        threshSelect.setFont(new Font("Calibri", Font.PLAIN, 14));
        safetySelect.setFont(new Font("Calibri", Font.PLAIN, 14));
        */
        
         
        //Add buttons to experiment with Grid Layout
        compsToExperiment.add(staticText[0]);
        compsToExperiment.add(currencySelect);
        compsToExperiment.add(currentData[0]);
        
        compsToExperiment.add(staticText[1]);
        compsToExperiment.add(typeSelect);
        compsToExperiment.add(currentData[1]);
        
        /*
        compsToExperiment.add(staticText[2]);
        compsToExperiment.add(initialSelect);
        compsToExperiment.add(currentData[2]);
        
        compsToExperiment.add(staticText[3]);
        compsToExperiment.add(threshSelect);
        compsToExperiment.add(currentData[3]);
        
        compsToExperiment.add(staticText[4]);
        compsToExperiment.add(safetySelect);
        compsToExperiment.add(currentData[4]);
        */
        //Add controls to set up horizontal and vertical gaps
        controls.add(applyButton);
        controls.add(updateButton);
         
        //Process the Apply gaps button press
        applyButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JSONObject n = userPref.getJSONObject(currentCurrency);
                n.put("type", currentType);
                /*
                n.put("initial", currentInit);
                n.put("threshold", currentThreshold);
                n.put("ditch", currentSafety);
                */
                userPref.put(currentCurrency, n);
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		S3Upload.uploadString(userPref.toString(1), "user_pref/user_preferences.json");
        	}
        });
        
        currencySelect.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		currentCurrency = (String)currencySelect.getSelectedItem();
        		updateWithCurrency(currentCurrency);
        		currentData[0].setText(currentCurrency);
        		currentData[1].setText(currentType);
        		/*
        		currentData[2].setText("$" + df.format(currentInit));
        		currentData[3].setText("$" + df.format(currentThreshold));
        		currentData[4].setText("$" + df.format(currentSafety));
        		*/
        	}
        });
        
        typeSelect.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		currentType = (String)typeSelect.getSelectedItem();
        		currentData[1].setText(currentType);
        	}
        });
        
        /*
        initialSelect.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				currentInit = Double.parseDouble(initialSelect.getText());
        		currentData[2].setText("$" + df.format(currentInit));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
				currentInit = Double.parseDouble(initialSelect.getText());
				} catch(NumberFormatException p) {
					currentInit = 0;
				}
        		currentData[2].setText("$" + df.format(currentInit));
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if(initialSelect.getText().isEmpty()) currentInit = 0;
				try {
					currentInit = Double.parseDouble(initialSelect.getText());
				} catch(NumberFormatException p) {
						currentInit = 0;
				}
        		currentData[2].setText("$" + df.format(currentInit));
			}
        	
        });
        
        threshSelect.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				currentThreshold = Double.parseDouble(threshSelect.getText());
        		currentData[3].setText("$" + df.format(currentThreshold));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					currentThreshold = Double.parseDouble(threshSelect.getText());
				} catch(NumberFormatException p) {
						currentThreshold = 0;
				}
        		currentData[3].setText("$" + df.format(currentThreshold));
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if(threshSelect.getText().isEmpty()) currentThreshold = 0;
				try {
					currentThreshold = Double.parseDouble(threshSelect.getText());
				} catch(NumberFormatException p) {
						currentThreshold = 0;
				}
        		currentData[3].setText("$" + df.format(currentThreshold));
			}
        	
        });
        
        safetySelect.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				currentSafety = Double.parseDouble(safetySelect.getText());
        		currentData[4].setText("$" + df.format(currentSafety));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				try {
					currentSafety = Double.parseDouble(safetySelect.getText());
				} catch(NumberFormatException p) {
						currentSafety = 0;
				}
        		currentData[4].setText("$" + df.format(currentSafety));
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if(safetySelect.getText().isEmpty()) currentSafety = 0;
				try {
					currentSafety = Double.parseDouble(safetySelect.getText());
				} catch(NumberFormatException p) {
						currentSafety = 0;
				}
        		currentData[4].setText("$" + df.format(currentSafety));
			}
        	
        });
        */
        pane.add(compsToExperiment, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(controls, BorderLayout.SOUTH);
        pane.setBackground(Color.WHITE);
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method is invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        GUIRunner frame = new GUIRunner("CryptoTrader User Preferences");
        frame.setLocation(430, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        frame.addComponentsToPane(frame.getContentPane());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
     
    public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
