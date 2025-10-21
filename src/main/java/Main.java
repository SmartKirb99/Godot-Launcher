import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Main {
    static String user = System.getProperty("user.name");
    static String fileLocation = "";
    static List<GodotVersionInfo> versions = new ArrayList<>();
    static String latestVersion = "4.5.1";
    static String latest3Version = "3.6.1";
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
        String userHome =  System.getProperty("user.home");
        String directoryPath = userHome + "\\GodotPrograms";
        Path programsPath = Paths.get(directoryPath);
        try{
            if(Files.exists(programsPath)){

            } else {
                Files.createDirectories(programsPath);
                JOptionPane.showMessageDialog(frame, "A Directory called GodotPrograms should be at: " + userHome + ". Go ahead and put your Godot Installations there, elsewise the program will give you an error and then quit.");
            }
        } catch (IOException e){
            System.err.println("Failed to make directory: " + e.getMessage());
        }
        JOptionPane.showMessageDialog(frame, "I can't tell if you have .NET versions installed, but if you do, then I recommend making sure\nyou also have the standard edtion to be able to run it in the launcher. Elsewise they won't appear in the Launcher","Warning", JOptionPane.WARNING_MESSAGE);
        System.out.println("Programs Path: " + programsPath.toString());
        populateComboBox(comboBox);
        checkBox.setSelected(false);
        try{
            if (hasMono.getFirst() == true){
                checkBox.setEnabled(true);
            } else {
                checkBox.setEnabled(false);
            }
        } catch (NoSuchElementException e){
            JOptionPane.showMessageDialog(frame, "You likely only have .NET installations, which kinda also requires the Standard edition to run.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
        if (versions.isEmpty()) {
            JOptionPane.showMessageDialog(frame,"Ah, you don't have any installations of Godot.\nThis ain't the Minecraft launcher.\nSo go and install Godot, then put it in " + programsPath +".\nIf you have only .NET versions, that's because the launcher uses a Check Box to run the mono version, so it's best to add the Standard version as well", "Error", JOptionPane.ERROR_MESSAGE);
//            System.out.println("Uhh, you don't have any installations of Godot, please install one before using this.\n" +
//                    "This isn't the Minecraft Launcher after all");
            System.exit(0);
        }
        JButton button = new JButton("Open this instance of Godot");
        fileLocation = directoryPath + "\\" + versions.getFirst().getOriginalFilename();
//        System.out.println(fileLocation);
//        System.out.println(versions.getFirst().getOriginalFilename());

        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedVersion =  (String) comboBox.getSelectedItem();
                checkBox.setEnabled(selectedVersion != null && hasMono.get(comboBox.getSelectedIndex()));
                for (int i = 0; i < versions.size(); i++) {
                    if (i == comboBox.getSelectedIndex()) {
                        fileLocation = directoryPath + "\\" + versions.get(i).getOriginalFilename();
                        if (isMonoInstalled(comboBox.getSelectedIndex())) {
                            checkBox.setEnabled(true);
                        } else {
                            checkBox.setEnabled(false);
                            checkBox.setSelected(false);
                        }
                    }
                }

//                System.out.println(fileLocation);
            }
        });

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedVersion =  (String) comboBox.getSelectedItem() + " .NET Version";
                String originalVersion = directoryPath + versions.get(comboBox.getSelectedIndex()).getOriginalFilename();
                String toInsert = "_mono";
                int lastUnderscoreIndex = originalVersion.lastIndexOf("_");
                if (lastUnderscoreIndex != -1) {
                    String prefix = originalVersion.substring(0, lastUnderscoreIndex);
                    int extensionIndex = originalVersion.lastIndexOf(".");
                    String suffix = originalVersion.substring(lastUnderscoreIndex, extensionIndex);
                    String newVersion = prefix + toInsert + suffix;
//                    System.out.println(originalVersion);
//                    System.out.println(newVersion);
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
//                        System.out.println(newVersion);
                        fullPath = fileLocation + "\\" + newVersion;
                    } else {
                        fullPath = fileLocation + "\\" + versions.get(comboBox.getSelectedIndex()).getOriginalFilename();
                    }
                    ProcessBuilder processBuilder = new ProcessBuilder(fullPath);

                    processBuilder.directory(new File(fileLocation));
                    processBuilder.inheritIO();

                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();
                    System.out.println("Godot Engine closed with code " + exitCode);
                } catch (IOException | InterruptedException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Whoops, there was an error opening Godot ;-;... \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        File rootDirectory = new  File(System.getProperty("user.home") + "\\GodotPrograms");

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

    /**
     * Checks to see if the .NET version is installed for a version at a specific index
     * @param index Because isMonoInstalled runs {@link #getMono() getMono().get(index)}, it uses a specified index to check if that specific version of Godot has the .NET version installed.
     * @return
     */
    public static boolean isMonoInstalled(int index){
        return getMono().get(index);
    }

    /**
     * Checks all versions to see if the .NET version of Godot is also installed
     * @return
     */
    public static List<Boolean> getMono(){
        List<String> filenames = new ArrayList<>();
        List<GodotVersionInfo> versions = new ArrayList<>();
        List<Boolean> hasMono = new ArrayList<>();

        File rootDirectory = new  File(System.getProperty("user.home") + "\\GodotPrograms");

        if (rootDirectory.exists() && rootDirectory.isDirectory()) {
            File[] files = rootDirectory.listFiles();
            if  (files != null) {
                int i = 0;
                for (File file : files) {
                    if (file.isDirectory() && !file.getName().contains("_mono")){
                        filenames.add(file.getName());
                        versions.add (new GodotVersionInfo(file.getName()));
                        if (i == 0){

                        }
                        else if (files[i - 1].getName().contains("_mono")){
                            hasMono.add(true);
                        }
                        else {
                            hasMono.add(false);
                        }
                        i++;
                    } else if (file.isDirectory() && file.getName().contains("_mono")){
                        i++;
                    }
                }
            }
        } else {
            System.err.println("Uh, so, this probably ran because the code didn't see GodotPrograms within " + System.getProperty("user.home") +". So just rerun it, it shouldn't give this error again");
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