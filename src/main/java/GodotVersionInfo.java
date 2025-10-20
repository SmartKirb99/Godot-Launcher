import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is mostly meant to give Main a structure for Godot versions
 */
public class GodotVersionInfo {
    private String versionNumber;
    private boolean isDotNet;
    private String originalFilename;

    public static final Pattern VERSION_PATTERN = Pattern.compile("Godot_v?(\\d+\\.\\d+(?:\\.\\d+)?)-(stable|stable_mono)_win64");

    /**
     * Constructor to get a specific file
     * @param filename The name of the file
     */
    public GodotVersionInfo(String filename){
        this.originalFilename = filename;
        if (filename == null){
            throw new IllegalArgumentException("Filename cannot be null");
        }

        Matcher matcher = VERSION_PATTERN.matcher(filename);
        if (matcher.find()) {
            this.versionNumber = matcher.group(1);
        } else {
            this.versionNumber = "Unknown";
        }

        this.isDotNet = filename.contains("_mono");
    }

    /**
     * Gets the version number
     * @return Returns the version number of Godot, such as 4.5.1
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Gets if the specific version of Godot is the .NET version, indicated by "mono" being in the filename
     * @return Returns if the specific version is the .NET version
     */
    public boolean isDotNet() {
        return isDotNet;
    }

    /**
     * Gets the original filename
     * @return Returns the original filename
     */
    public String getOriginalFilename() {
        return originalFilename;
    }

    /**
     * Sets the original filename to a new filename
     * @param originalFilename The new filename
     */
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    /**
     * Converts the Version to a readable string, I guess.
     */
    @Override
    public String toString() {
        String type = isDotNet ? ".NET version" : "Standard Version"; //This feels weird
        return "Version: " + versionNumber + ", Type: " + type;
    }
}
