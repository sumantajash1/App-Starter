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
import java.io.InputStreamReader;
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
        nextStart+=10; 
        appPathField.setBounds(150, nextStart, 300, 30); 
        nextStart+=40;
        TextField appNameField = new TextField();
        appNameField.setBounds(150, nextStart, 300, 30);
        nextStart+=40;
        JButton addButton = new JButton("Add New App");
        addButton.setBounds(150, nextStart, 150, 40);
        nextStart+=50; 
        
        startButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateSelection(checkBoxes, execPaths);
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if (checkBoxes.get(i).isSelected() && !isAppRunning(execPaths.get(i))) {
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

    public static String getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".devAppStart");
        if(!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "config.txt").getAbsolutePath();
    }

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

    public static void updateSelection(List<JCheckBox> checkBoxes, List<String> execPaths) {
        File file = new File(CONFIG_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(int i=0; i<checkBoxes.size(); i++) {
                String appName = checkBoxes.get(i).getText();
                String execPath = execPaths.get(i);
                String isSelected = checkBoxes.get(i).isSelected()?"true":"false";
                writer.write(appName + "=" + execPath + "," + isSelected);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadDetails(List<String> execPaths, List<JCheckBox> checkBoxes) {
        File file = new File(CONFIG_FILE);
        if(file.exists()) {
            try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while((line=reader.readLine())!=null) {
                // Line example :: App Name=ExecutablePath.exe, Selection(Boolean)
                    String parts1[] = line.split("=");
                    String appName = parts1[0];
                    String parts2[] = parts1[1].split(","); 
                    String execPath = parts2[0]; 
                    String selection = parts2[1]; 
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

    public static Boolean isAppRunning(String execPath) {
        String exeName = new File(execPath).getName();
        //System.out.println(exeName);
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                //System.out.println(line.toLowerCase());
                if(line.toLowerCase().contains(exeName.toLowerCase())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
