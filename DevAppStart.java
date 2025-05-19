import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class DevAppStart {
    static final String CONFIG_FILE = getConfigFilePath();

    public static void main(String[] args) {
        JFrame frame = new JFrame("All Dev App Starter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        frame.setContentPane(mainPanel);

        // Title
        JLabel titleLabel = new JLabel("All Dev App Starter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Section: App List
        JLabel listLabel = new JLabel("Select apps to launch:");
        listLabel.setFont(new Font("Arial", Font.BOLD, 16));
        listLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(listLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        List<JCheckBox> checkBoxes = new ArrayList<>();
        List<String> execPaths = new ArrayList<>();
        loadDetails(execPaths, checkBoxes);

        JPanel appListPanel = new JPanel();
        appListPanel.setLayout(new BoxLayout(appListPanel, BoxLayout.Y_AXIS));
        appListPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        for (JCheckBox cb : checkBoxes) {
            cb.setFont(new Font("Arial", Font.PLAIN, 14));
            appListPanel.add(cb);
        }
        JScrollPane scrollPane = new JScrollPane(appListPanel);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));

        // Section: Add New App
        JLabel addLabel = new JLabel("Add a New App");
        addLabel.setFont(new Font("Arial", Font.BOLD, 16));
        addLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(addLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel pathLabel = new JLabel("App Executable Path:");
        JTextField appPathField = new JTextField(25);
        JLabel nameLabel = new JLabel("App Name:");
        JTextField appNameField = new JTextField(25);

        gbc.gridx = 0; gbc.gridy = 0;
        addPanel.add(pathLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(appPathField, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        addPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        addPanel.add(appNameField, gbc);

        JButton addButton = new JButton("Add New App");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        addPanel.add(addButton, gbc);

        mainPanel.add(addPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        statusLabel.setForeground(new Color(0, 102, 0));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Section: Delete App
        JLabel deleteLabel = new JLabel("Delete an App");
        deleteLabel.setFont(new Font("Arial", Font.BOLD, 16));
        deleteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(deleteLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel deletePanel = new JPanel(new GridBagLayout());
        deletePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel deleteAppLabel = new JLabel("Name of the app you want to delete:");
        JTextField deleteAppField = new JTextField(20);
        JButton deleteButton = new JButton("Delete an App");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        deletePanel.add(deleteAppLabel, gbc);
        gbc.gridx = 1;
        deletePanel.add(deleteAppField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        deletePanel.add(deleteButton, gbc);

        mainPanel.add(deletePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Start Button
        JButton startButton = new JButton("Start Selected Apps");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(startButton);

        // --- Action Listeners ---
        startButton.addActionListener(e -> {
            try {
                updateSelection(checkBoxes, execPaths);
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected() && !isAppRunning(execPaths.get(i))) {
                        ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", execPaths.get(i));
                        builder.start();
                    }
                }
                statusLabel.setText("Selected apps started (if not already running).");
            } catch (IOException exception) {
                statusLabel.setText("Error starting apps.");
            }
        });

        addButton.addActionListener(e -> {
            String appName = appNameField.getText().trim();
            String appExecPath = appPathField.getText().trim();
            if (appName.isEmpty() || appExecPath.isEmpty()) {
                statusLabel.setText("App name & executable path cannot be empty.");
                return;
            }
            for (JCheckBox cb : checkBoxes) {
                if (cb.getText().equalsIgnoreCase(appName)) {
                    statusLabel.setText("App name already exists.");
                    return;
                }
            }
            if (doesExist(appExecPath)) {
                statusLabel.setText("App path already exists.");
                return;
            }
            try {
                ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", appExecPath);
                builder.start();
                if (isAppRunning(appExecPath)) {
                    addNewApp(appName, appExecPath);
                    appNameField.setText("");
                    appPathField.setText("");
                    statusLabel.setText("App added successfully! Restarting...");
                    restartApp();
                } else {
                    statusLabel.setText("App doesn't exist / execution path is wrong.");
                }
            } catch (Exception excep) {
                statusLabel.setText("App doesn't exist / execution path is wrong.");
            }
        });

        deleteButton.addActionListener(e -> {
            String appNameToDelete = deleteAppField.getText().trim();
            if (appNameToDelete.isEmpty()) {
                statusLabel.setText("Please enter an app name to delete.");
                return;
            }
            if (deleteApp(appNameToDelete, checkBoxes, execPaths)) {
                deleteAppField.setText("");
                statusLabel.setText("App deleted. Restarting...");
                restartApp();
            } else {
                statusLabel.setText("App not found.");
            }
        });

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
}
