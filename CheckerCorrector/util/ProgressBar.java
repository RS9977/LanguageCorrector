package util;

public class ProgressBar {
    public static void printProgress(double x) {
        // Calculate the number of characters to represent the progress
        int progressChars = (int) (x * 100);

        // Print the progress
        System.out.print("[");
        for (int i = 0; i < 100; i++) {
            if (i < progressChars) {
                System.out.print("#"); // Print '#' for completed portion
            } else {
                System.out.print("-"); // Print space for remaining portion
            }
        }
        System.out.print("] " + (int) (x * 100) + "%\r");
        System.out.flush(); // Flush the output buffer
    }

}