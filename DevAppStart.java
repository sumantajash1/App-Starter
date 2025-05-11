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
        JPanel panel = new JPanel();
        int panelHeight = checkBoxes.size() * 15;
        panel.setBounds(150, 250, 300,panelHeight);
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

        List<String> execPaths = new ArrayList<>();
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //saveSelections(checkBoxes, execPaths);
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected()) {
                            //System.out.println(checkBoxes[i].getText());
                            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", execPaths.get(i));
                            builder.start();
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // add checkbox(take the name from the nameField), add execPath(take path from pathField)
                    
                } catch(IOException excep) {
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

    public static String getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".devAppStart");
        if(!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "config.txt").getAbsolutePath();
    }

    public void loadDetails(List<String> execPaths, List<JCheckBox> checkBoxes) {
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
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
