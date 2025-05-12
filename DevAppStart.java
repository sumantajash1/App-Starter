import java.awt.Checkbox;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class DevAppStart {
    static final String CONFIG_FILE = getConfigFilePath();
    public static void main(String[] args) {
        JFrame frame = new JFrame("All Dev App Starter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 750);
        frame.setLayout(null);
        JButton startButton = new JButton("Start");
        startButton.setBounds(225, 200, 200, 50);
        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> execPaths = new ArrayList<>();
        loadDetails(execPaths, checkBoxes);
        JPanel panel = new JPanel();
        int panelHeight = checkBoxes.size() * 10;
        panel.setBounds(150, 250, 500, panelHeight);
        int nextStart = 250+panelHeight;
        TextField appPathField = new TextField();
        nextStart+=10; //increase nextStart
        appPathField.setBounds(150, nextStart, 300, 30); 
        nextStart+=40;
        TextField appNameField = new TextField();
        appNameField.setBounds(150, nextStart, 300, 30);
        nextStart+=40;
        JButton addButton = new JButton("Add New App");
        addButton.setBounds(150, nextStart, 150, 40);
        nextStart+=50; 
        
        startButton.addActionListener(new ActionListener() { // Start Button
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateSelection(checkBoxes);
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", execPaths.get(i));
                            builder.start();
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
        addButton.addActionListener(new ActionListener() { // Add new App button
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addNewApp(appNameField.getText(), appPathField.getText());
                } catch(Exception excep) {
                    excep.printStackTrace();
                }
            }
        });

        for (int i = 0; i < checkBoxes.size(); i++) {
            panel.add(checkBoxes.get(i));
        }
        frame.add(addButton);
        frame.add(appPathField);
        frame.add(appNameField);
        frame.add(panel);
        frame.add(startButton);
        frame.setVisible(true);
    }

    // To get the configuration file path, if doesn't exist, it will automatically make one
    public static String getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".devAppStart");
        if(!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "config.txt").getAbsolutePath();
    }

    // To add a new App
    public static void addNewApp(String appName, String appExecPath) {
        File file = new File(CONFIG_FILE);
        if(file.exists()) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(appName+"="+appExecPath+","+"false");
                writer.newLine();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    // For updating which apps were selected to be opened last time
    public static void updateSelection(List<JCheckBox> checkBoxes) { 
        System.out.println("1. updateSelection method being called");
        for(int i=0; i<checkBoxes.size(); i++) {
            File file = new File(CONFIG_FILE);
            if(file.exists()) System.out.println("2. file exists");
            String appName = checkBoxes.get(i).getText();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line;
                while((line= reader.readLine()) != null) {
                    String[] parts1 = line.split("=");
                    if(appName.equals(parts1[0]))  {
                        System.out.println("3. app found" + parts1[0]);
                        String[] parts2 = parts1[1].split(",");
                        System.out.println(parts2[1]);
                        if(checkBoxes.get(i).isSelected()) {
                            parts2[1] = "true"; //use buffred writer
                
                        } else {
                            parts2[1] = "false";
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    // To load all the details that are stored into the program
    public static void loadDetails(List<String> execPaths, List<JCheckBox> checkBoxes) {
        File file = new File(CONFIG_FILE);
        if(file.exists()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while((line=reader.readLine())!=null) {
                // Line example :: App Name=ExecutablePath.exe, Selection(Boolean)
                    String parts1[] = line.split("=");
                    String appName = parts1[0]; //app name 
                    String parts2[] = parts1[1].split(","); // execPath, selection
                    String execPath = parts2[0]; //execPath
                    String selection = parts2[1]; // selection
                    execPaths.add(execPath);
                    JCheckBox checkBox = new JCheckBox(appName);
                    checkBox.setSelected(Boolean.parseBoolean(selection));
                    checkBoxes.add(checkBox);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
