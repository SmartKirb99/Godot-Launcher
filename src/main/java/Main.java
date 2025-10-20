import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static String user = System.getProperty("user.name");
    static String fileLocation = "";
    static List<GodotVersionInfo> versions = new ArrayList<>();
    static String latestVersion = "4.5.1";
    /**
     * Average JFrame stuff, uses {@link #populateComboBox(JComboBox)} to make the comboBox work
     */
    public static void main(String[] args) {
        List<Boolean> hasMono = getMono();
        //Frame and Panel
        JPanel panel = new JPanel();
        JFrame frame = new JFrame("Godot Launcher");
        //Versions and If the user wants to use the .NET version
        JComboBox<String> comboBox = new JComboBox();
        JCheckBox checkBox = new JCheckBox(".NET Version");
        //Add all the versions. And also disables the CheckBox
        populateComboBox(comboBox);
        checkBox.setEnabled(false);
        JButton button = new JButton("Open this instance of Godot");
        fileLocation = "C:\\Users\\" + user + "\\GodotPrograms\\" + versions.getFirst().getOriginalFilename();
        System.out.println(fileLocation);
//        System.out.println(versions.getFirst().getOriginalFilename());

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedVersion =  (String) comboBox.getSelectedItem();
                checkBox.setEnabled(selectedVersion != null && hasMono.get(comboBox.getSelectedIndex()));
                for (int i = 0; i < versions.size(); i++) {
                    if (i == comboBox.getSelectedIndex()) {
                        fileLocation = "C:\\Users\\" + user + "\\GodotPrograms\\" + versions.get(i).getOriginalFilename();
                        if (isMonoInstalled(comboBox.getSelectedIndex())) {
                            checkBox.setEnabled(true);
                        } else {
                            checkBox.setEnabled(false);
                            checkBox.setSelected(false);
                        }
                    }
                }

                System.out.println(fileLocation);
            }
        });

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedVersion =  (String) comboBox.getSelectedItem() + " .NET Version";
                String originalVersion = "C:\\Users\\" + user + "\\GodotPrograms\\" + versions.get(comboBox.getSelectedIndex()).getOriginalFilename();
                String toInsert = "_mono";
                int lastUnderscoreIndex = originalVersion.lastIndexOf("_");
                if (lastUnderscoreIndex != -1) {
                    String prefix = originalVersion.substring(0, lastUnderscoreIndex);
                    int extensionIndex = originalVersion.lastIndexOf(".");
                    String suffix = originalVersion.substring(lastUnderscoreIndex, extensionIndex);
                    String newVersion = prefix + toInsert + suffix;
                    System.out.println(originalVersion);
                    System.out.println(newVersion);
                    fileLocation = newVersion;
                }
                System.out.println(checkBox.isSelected());
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String fullPath;
                    if (isMonoInstalled(comboBox.getSelectedIndex()) && checkBox.isSelected()) {
                        String originalVersion = versions.get(comboBox.getSelectedIndex()).getOriginalFilename();
                        String toInsert = "_mono";
                        int lastUnderscoreIndex = originalVersion.lastIndexOf("_");
                        String prefix = originalVersion.substring(0, lastUnderscoreIndex);
                        int extensionIndex = originalVersion.lastIndexOf(".");
                        String suffix = originalVersion.substring(lastUnderscoreIndex, extensionIndex);
                        String newVersion = prefix + toInsert + suffix + ".exe";
                        System.out.println(newVersion);
                        fullPath = fileLocation + "\\" + newVersion;
                    } else {
                        fullPath = fileLocation + "\\" + versions.get(comboBox.getSelectedIndex()).getOriginalFilename();
                    }
                    ProcessBuilder processBuilder = new ProcessBuilder(fullPath);

                    processBuilder.directory(new File(fileLocation));
                    processBuilder.inheritIO();

                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    System.out.println("Godot Engine Exited with code " + exitCode);
                } catch (IOException | InterruptedException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Whoops, there was an error opening Godot ;-;... " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //Alignment Stuff
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(Box.createVerticalStrut(5));
        panel.add(comboBox);
        comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(checkBox);
        panel.add(button);
        panel.setBackground(new Color(71,140,191));
//        panel.setBackground(new Color(0, 48, 156));
        comboBox.setBackground(new Color(16, 184, 204));
//        comboBox.setBackground(new Color(90, 126, 220));
//        comboBox.setForeground(Color.WHITE);
//        checkBox.setForeground(Color.WHITE);
//        checkBox.setBackground(new Color(0, 48, 156));
//        button.setForeground(Color.WHITE);
//        button.setBackground(new Color(0, 48, 156));


        //Frame stuff
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);

    }

    /**
     * Basically Fills the Combo Box with versions.
     * @param comboBox Pretty much just, a standard {@link JComboBox}
     */
    public static void populateComboBox(JComboBox<String> comboBox) {
        List<String> filenames = new ArrayList<>();
        comboBox.removeAllItems();
        File rootDirectory = new  File("C:\\Users\\"+ user +"\\GodotPrograms");

        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            File[] files = rootDirectory.listFiles();
            if  (files != null) {
                int i = 0;
                for (File file : files) {
                    if (file.isDirectory() && !file.getName().contains("_mono")){
                        filenames.add(file.getName());
                        versions.add (new GodotVersionInfo(file.getName()));
                        if (!versions.get(i).getVersionNumber().equals(latestVersion)){
                            comboBox.addItem("Godot " + versions.get(i).getVersionNumber());
                        } else {
                            comboBox.addItem("Godot " + versions.get(i).getVersionNumber() + " (Latest Version)");
                        }
                        i++;
                    }
                }
            }
        } else {
            System.err.println("Whoops, apparently GodotPrograms isn't found within a specified directory. Perhaps create that folder and try again.");
        }
    }
    public static boolean isMonoInstalled(int index){
        return getMono().get(index);
    }

    public static List<Boolean> getMono(){
        List<String> filenames = new ArrayList<>();
        List<GodotVersionInfo> versions = new ArrayList<>();
        List<Boolean> hasMono = new ArrayList<>();

        File rootDirectory = new  File("C:\\Users\\"+ user +"\\GodotPrograms");

        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            File[] files = rootDirectory.listFiles();
            if  (files != null) {
                int i = 0;
                for (File file : files) {
                    if (file.isDirectory() && !file.getName().contains("_mono")){
                        filenames.add(file.getName());
                        versions.add (new GodotVersionInfo(file.getName()));
                        hasMono.add(false);
                        i++;
                    } else if (file.isDirectory() && file.getName().contains("_mono")){
                        hasMono.add(true);
                        i++;
                    }
                }
            }
        } else {
            System.err.println("Whoops, apparently GodotPrograms isn't found within C:\\Users\\"+ user +". Perhaps create that file and try again.");
        }
        return hasMono;
    }

}
/* String standardFilename = "Godot_v4.5.1-stable_win64";
        String dotNetFilename = "Godot_v4.5.1-stable_mono_win64";

        GodotVersionInfo standardVersionInfo = new GodotVersionInfo(standardFilename);
        System.out.println(standardFilename + ":");
        System.out.println(" Version Number: " + standardVersionInfo.getVersionNumber());
        System.out.println(" Is .NET Version: " + standardVersionInfo.isDotNet());
        System.out.println(standardVersionInfo);

        System.out.println();

        GodotVersionInfo dotNetVersion = new  GodotVersionInfo(dotNetFilename);
        System.out.println(dotNetFilename + ":");
        System.out.println(" Version Number: " + dotNetVersion.getVersionNumber());
        System.out.println(" Is .NET Version: " + dotNetVersion.isDotNet());
        System.out.println(dotNetVersion);

 */