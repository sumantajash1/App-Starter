import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class DevAppStart {
    static final String CONFIG_FILE = getConfigFilePath();
    public static void main(String[] args) {
        JFrame frame = new JFrame("All Dev App Start");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(null);
        JButton button = new JButton("Start");
        button.setBounds(150, 200, 200, 50);
        JCheckBox checkBoxes[] = {
									new JCheckBox("Postman"),
									new JCheckBox("Git-Bash"),
									new JCheckBox("Powershell"),
									new JCheckBox("MongoDBCompass"),
									new JCheckBox("Notion"),
									new JCheckBox("Intellij-IDEA"),
									new JCheckBox("Cursor"),
									new JCheckBox("VS-Code"),
									new JCheckBox("PG-Admin-4"),
									new JCheckBox("Spring-Tool-Suite-4")
        						};
        JPanel panel = new JPanel();
        panel.setBounds(150, 250, 300, 300);
        for (int i = 0; i < checkBoxes.length; i++) {
            panel.add(checkBoxes[i]);
        }
        frame.add(panel);
        frame.add(button);
        frame.setVisible(true);
        final String execPaths[] = {
										"C:\\Users\\jashs\\AppData\\Local\\Postman\\Postman.exe",
										"C:\\Program Files\\Git\\git-bash.exe",
										"C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",
										"C:\\Users\\jashs\\AppData\\Local\\MongoDBCompass\\MongoDBCompass.exe",
										"C:\\Users\\jashs\\AppData\\Local\\Programs\\Notion\\Notion.exe",
										"C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2024.3\\bin\\idea64.exe",
										"C:\\Users\\jashs\\AppData\\Local\\Programs\\cursor\\Cursor.exe",
										"C:\\Users\\jashs\\AppData\\Local\\Programs\\Microsoft VS Code\\Code.exe",
										"C:\\Program Files\\PostgreSQL\\17\\pgAdmin 4\\runtime\\pgAdmin4.exe", 
										"D:\\Downloads\\spring-tool-suite-4\\sts-4.28.0.RELEASE\\SpringToolSuite4.exe",
        							};

        loadSelections(checkBoxes);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveSelections(checkBoxes);
                    for (int i = 0; i < checkBoxes.length; i++) {
                        if (checkBoxes[i].isSelected()) {
                            //System.out.println(checkBoxes[i].getText());
                            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", "start", "", execPaths[i]);
                            builder.start();
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private static String getConfigFilePath() {
        String userHome = System.getProperty("user.home");
        File configDir = new File(userHome, ".devAppStart");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return new File(configDir, "config.txt").getAbsolutePath();
    }

    private static void saveSelections(JCheckBox[] checkBoxes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE))) {
            for (int i = 0; i < checkBoxes.length; i++) {
                writer.write(checkBoxes[i].isSelected() ? "true" : "false");
                if (i != checkBoxes.length - 1) {
                    writer.write(",");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSelections(JCheckBox[] checkBoxes) {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                if (line != null) {
                    String[] selections = line.split(",");
                    for (int i = 0; i < selections.length && i < checkBoxes.length; i++) {
                        checkBoxes[i].setSelected(Boolean.parseBoolean(selections[i]));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
