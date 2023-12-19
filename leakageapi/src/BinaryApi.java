import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BinaryApi {
    private String file_name;               // example.py
    private String file_path_inclusive;     // /path/to/file/example.py
    private String file_path;               // /path/to/file

    private String api_code_path;

    public BinaryApi(String fp) {
        Path path = Paths.get(fp);
        file_name = path.getFileName().toString();
        file_path_inclusive = fp;
        file_path = path.getParent().toString();

        api_code_path = BinaryApi.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("src/BinaryApi.java", "");
    }

    public int analysis() {
        try {
            // Clean the directory, in case the user has run an analysis without cleaning
            this.clean();

            // Run the leakage analysis code
            String[] command;
            if (System.getProperty("os.name").contains("Windows")) {
                command = {"wsl", api_code_path + "bin/leakage-analyisis", file_path_inclusive};
            } else {
                command = {api_code_path + "bin/leakage-analyisis", file_path_inclusive};
            }

            // Create ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

            return exitCode;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void delete(String stringToExclude) {
        File directory = new File(file_path);

        if (!directory.exists() || !directory.isDirectory()) {
            System.err.println("Invalid directory path");
            return;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().contains(stringToExclude) && !file.getName().equals(file_name)) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    public void clean() {
        this.delete(file_name.replaceAll("\\.py$", ""));
    }

    public static void main(String[] args) {
        // BinaryApi example = new BinaryApi("../tests/test_overlap.py");
        // example.clean();
    }
}