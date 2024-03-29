package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class StringFileWriter {
    private StringBuilder stringBuilder;
    private String filePath;

    private StringFileWriter(String filePath) {
        this.stringBuilder = new StringBuilder();
        this.filePath = filePath;
    }

    public static StringFileWriter of(String filePath) {
        return new StringFileWriter(filePath);
    }

    public void appendString(String str) {
        stringBuilder.append(str);
        stringBuilder.append(" ");
    }

    public void writeToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(stringBuilder.toString());
        }
    }
}
