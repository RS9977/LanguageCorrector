package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class StringFileWriter {
    private StringBuilder stringBuilder;
    private String filePath;
    private String space;
    private boolean appendFlag;

    private StringFileWriter(String filePath) {
        this.stringBuilder = new StringBuilder();
        this.filePath = filePath;
        this.space = " ";
        this.appendFlag = false;
    }
    private StringFileWriter(String filePath, String S) {
        this.stringBuilder = new StringBuilder();
        this.filePath = filePath;
        this.space = S;
        this.appendFlag = false;
    }

    private StringFileWriter(String filePath, String S, boolean appendFlag) {
        this.stringBuilder = new StringBuilder();
        this.filePath = filePath;
        this.space = S;
        this.appendFlag = appendFlag;
    }

    public static StringFileWriter of(String filePath, String S, boolean appendFlag) {
        return new StringFileWriter(filePath, S, appendFlag);
    }

    public static StringFileWriter of(String filePath, String S) {
        return new StringFileWriter(filePath, S);
    }

    public static StringFileWriter of(String filePath) {
        return new StringFileWriter(filePath);
    }

    public void appendString(String str) {
        stringBuilder.append(str);
        stringBuilder.append(space);
    }

    public void writeToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, appendFlag))) {
            writer.write(stringBuilder.toString());
        }
    }
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            boolean deleted = file.delete();
            /* 
            if (deleted) {
                System.out.println("File '" + fileName + "' has been deleted successfully.");
            } else {
                System.out.println("Failed to delete the file '" + fileName + "'.");
            }*/
        } /*else {
            System.out.println("File '" + fileName + "' does not exist.");
        }*/
    }
}
