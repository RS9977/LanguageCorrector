package TypoCorrector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FilePrefixComparator {
    private String filename;

    private FilePrefixComparator(String filename) {
        this.filename = filename;
    }

    public static FilePrefixComparator of(String filename) {
        return new FilePrefixComparator(filename);
    }

    public String findBestMatchingPrefix(String prefixToCompare) {
        String bestMatch = null;
        int maxMatchLength = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Compare prefixes
              //  System.out.println(line);
                int commonPrefixLength = longestCommonPrefix(line, prefixToCompare);
                if (commonPrefixLength > maxMatchLength) {
                    maxMatchLength = commonPrefixLength;
                    bestMatch = line;//.substring(0, commonPrefixLength);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestMatch;
    }

    private int longestCommonPrefix(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());
        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return i;
            }
        }
        return minLength;
    }
    public boolean deleteLine(String lineToDelete) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename));
             BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".tmp"))) {

            String line;
            while ((line = br.readLine()) != null) {
                // If lineToDelete is found, skip it
                if (line.equals(lineToDelete)) {
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Rename the temporary file to the original filename
        java.io.File oldFile = new java.io.File(filename);
        java.io.File newFile = new java.io.File(filename + ".tmp");
        if (newFile.renameTo(oldFile)) {
            return true;
        } else {
            return false;
        }
    }
    
}
