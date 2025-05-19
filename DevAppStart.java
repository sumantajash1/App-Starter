import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
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
        frame.setSize(750, 850);
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
        JLabel pathLabel = new JLabel("App Executable Path:");
        pathLabel.setBounds(150, nextStart, 150, 30);
        frame.add(pathLabel);
        nextStart += 30;
        TextField appPathField = new TextField();
        appPathField.setBounds(150, nextStart, 300, 30);
        frame.add(appPathField);
        nextStart += 40;
        JLabel nameLabel = new JLabel("App Name:");
        nameLabel.setBounds(150, nextStart, 150, 30);
        frame.add(nameLabel);
        nextStart += 30;
        TextField appNameField = new TextField();
        appNameField.setBounds(150, nextStart, 300, 30);
        frame.add(appNameField);
        nextStart += 40;
        JButton addButton = new JButton("Add New App");
        addButton.setBounds(150, nextStart, 150, 40);
        nextStart += 50;
        JLabel statusLabel = new JLabel("");
        statusLabel.setBounds(150, nextStart, 300, 30);
        frame.add(statusLabel);
        nextStart += 40;
        JLabel deleteLabel = new JLabel("Name of the app you want to delete (As per the checkboxes)");
        deleteLabel.setBounds(150, nextStart, 350, 30);
        frame.add(deleteLabel);
        nextStart += 30;
        TextField deleteAppField = new TextField();
        deleteAppField.setBounds(150, nextStart, 300, 30);
        frame.add(deleteAppField);
        nextStart += 40;
        JButton deleteButton = new JButton("Delete an App");
        deleteButton.setBounds(150, nextStart, 150, 40);
        frame.add(deleteButton);
        nextStart += 50;
        JButton closeAllButton = new JButton("Close all Running Applications");
        closeAllButton.setBounds(150, nextStart, 300, 40);
        frame.add(closeAllButton);

        startButton.addActionListener(new ActionListener() { 
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    updateSelection(checkBoxes, execPaths);
                    for (int i = 0; i < checkBoxes.size(); i++) {
                        if(checkBoxes.get(i).isSelected()) {
                            System.out.println(checkBoxes.get(i).getText() + "is selected"); //works
                        }
                        if(isAppRunning(execPaths.get(i))) {
                            System.out.println(checkBoxes.get(i).getText() + "app is already running"); 
                        }
                        if ((checkBoxes.get(i).isSelected() && !isAppRunning(execPaths.get(i)))) {
                            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", execPaths.get(i));
                            System.out.println(checkBoxes.get(i).getText() + "running ig"); 
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
                    String appName = appNameField.getText();
                    String appExecPath = appPathField.getText();
                    if(appName.isEmpty() || appExecPath.isEmpty()) {
                        statusLabel.setText("app name &/ executable path cannot be empty");
                        return;
                    }
                    if(doesExist(appExecPath)) {
                        statusLabel.setText("App already Exists");
                    } else {
                        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", appExecPath);
                        builder.start();
                        if(isAppRunning(appExecPath)) {
                            addNewApp(appName, appExecPath);
                            appNameField.setText("");
                            appPathField.setText("");
                            statusLabel.setText("App added successfully!");
                            restartApp();
                        } else {
                            statusLabel.setText("App doesn't exist / execution path is wrong.");
                        }
                    }
                } catch(Exception excep) {
                    statusLabel.setText("App doesn't exist / execution path is wrong.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(deleteApp(deleteAppField.getText(), checkBoxes, execPaths)) {
                    deleteLabel.setText("App deleted Successfully");
                    deleteAppField.setText("");
                    restartApp();
                } else {
                    deleteLabel.setText("Please enter a valid app name");
                }  
            }
        });

        closeAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeAlltheApps(execPaths);
            }
        });

        for (int i = 0; i < checkBoxes.size(); i++) {
            panel.add(checkBoxes.get(i));
        }
        frame.add(addButton);
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
                writer.write(appName+"="+appExecPath+",false");   
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

    public static void restartApp() {
        try {
            String thisAppPath = "C:\\Users\\jashs\\OneDrive\\Desktop\\Java Projects\\All Dev App Starter\\AppStarter.jar";
            ProcessBuilder builder = new ProcessBuilder("java", "-jar", thisAppPath);
            builder.start();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean doesExist(String appExecPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(CONFIG_FILE)))) {
            String line;
            while((line = reader.readLine()) != null) {
                String[] split1 = line.split("=");
                String[] split2 = split1[1].split(",");
                if(appExecPath.toLowerCase().equals(split2[0].toLowerCase())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean deleteApp(String appName, List<JCheckBox> checkboxes, List<String> execPaths) {
        Boolean isFound = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(CONFIG_FILE)))) {
            for(int i=0; i<checkboxes.size(); i++) {
                if(checkboxes.get(i).getText().toLowerCase().equals(appName.toLowerCase())) {
                    isFound = true;
                    continue;
                }
                String isSelected = checkboxes.get(i).isSelected()?"true":"false";
                writer.write(checkboxes.get(i).getText()+"="+execPaths.get(i)+","+isSelected);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isFound;
    }

    public static void closeAlltheApps(List<String> appExecPaths) {
        try {
            ProcessBuilder builder = new ProcessBuilder("tasklist");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> taskListLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                taskListLines.add(line.toLowerCase());
            }
            for (String execPath : appExecPaths) {
                String name = new File(execPath).getName().toLowerCase();
                for (String taskLine : taskListLines) {
                    if (taskLine.contains(name)) {
                        System.out.println("Killing: " + name);
                        ProcessBuilder killBuilder = new ProcessBuilder("taskkill", "/f", "/im", name);
                        killBuilder.start();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
