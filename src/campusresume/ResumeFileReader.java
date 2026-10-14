package campusresume;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResumeFileReader {
    public String readTextFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }
}
