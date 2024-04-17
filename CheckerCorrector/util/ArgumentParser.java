package util;

import java.util.List;
import java.util.Arrays;

public class ArgumentParser {

    private String sentence;
    private String fileName;
    private boolean updateToken;
    private boolean checkSentence;
    private boolean checkFile;
    private boolean checkGUI;
    private boolean correctionGUI;
    private boolean updateHashTable;
    private boolean validateUpdates;
    private boolean translateDutch;

    ArgumentParser(String[] args) {
        checkSentence = false;
        updateToken = false;
        checkFile  = false;
        updateHashTable = false;
        validateUpdates = false;
        checkGUI = false;
        correctionGUI = false;
        translateDutch = false;
        parseArguments(Arrays.asList(args));
    }

    public static ArgumentParser of(String[] args){
        return new ArgumentParser(args);
    }
    private void parseArguments(List<String> args) {
        
        if(args.size()>0){
            for (int i = 0; i < args.size(); i++) {
                String arg = args.get(i);
                switch (arg) {
                    case "--help":
                        printHelp();
                        break;
                    case "--file":
                        if (i + 1 < args.size()) {
                            fileName = args.get(i + 1);
                            checkFile = true;
                            i++; // Increment to skip the next argument
                        } else {
                            System.err.println("Error: Missing argument after --file");
                        }
                        break;
                    case "--sentence":
                        if (i + 1 < args.size()) {
                            sentence = args.get(i + 1);
                            checkSentence = true;
                            i++; // Increment to skip the next argument
                        } else {
                            System.err.println("Error: Missing argument after --sentence");
                        }
                        break;
                    case "--updateToken":
                        updateToken = true;
                        break;
                    case "--updateHashTable":
                        updateHashTable = true;
                        break;
                    case "--validateUpdates":
                        validateUpdates = true;
                        break;
                    case "--checkerGUI":
                        checkGUI = true;
                        break;
                    case "--correctorGUI":
                        correctionGUI = true;
                        break;
                    case "--translateDutch":
                        translateDutch = true;
                        break;
                    // Add cases for other arguments here
                    default:
                        System.out.println("Invalis options. Please use --help to see how to use the tool.");
                        break;
                }
            }
        }else{
            checkSentence = true;
            sentence = "it a very good book, but it is small book.";
            System.out.println("Please enter a sentence. Program used the default sentence:\n" + sentence + "\n--------------------------------------------");
        }
    }


    private void printHelp() {
        System.out.println("Help information:");
        System.out.println("    Corrector Options:");
        System.out.println("        --file <filename>: this option should be used if you want to pass your input as file.");
        System.out.println("        --sentence <sentence>: this option should be used if you want to pass your input as a small sentence.");
        System.out.println("        --correctorGUI: this option can be used if you want a GUI for the corrector to select between possible suggestions.");
        System.out.println("        --translateDutch <sentence>: this option should be used if you want to translate from English to Dutch.");
        System.out.println("    Checker Options:");
        System.out.println("        --file <filename>: this option should be used if you want to pass your input as file.");
        System.out.println("        --sentence <sentence>: this option should be used if you want to pass your input as a small sentence.");
        System.out.println("        --checkerGUI: this option can be used if you want a GUI for the checker to see the highlighted sentences.");
        System.out.println("        --updateToken: this option should be used alongside a file as input to update new tokens for the database. This option may take hours based on the size of crawled file.");
        System.out.println("        --updateHashTable: this option should be used alongside a file as input to update n-grams weights for the database. This option may take a few minutes.");
        System.out.println("        --validateUpdates: this option can be used to check the correctness of the database for tokens. This will pops up a window.");
        // Add help information here
    }

    // Getters for parsed values
    public String getSentence() {
        return sentence;
    }

    public String getFileName(){
        return fileName;
    }

    public boolean isUpdateToken() {
        return updateToken;
    }
    public boolean isCheckSentence(){
        return checkSentence;
    }
    public boolean isCheckFile(){
        return checkFile;
    }
    public boolean isUpdateHashTable(){
        return updateHashTable;
    }
    public boolean isValidateUpdates(){
        return validateUpdates;
    }
    public boolean isCheckGUI(){
        return checkGUI;
    }
    public boolean isCorrectionGUI(){
        return correctionGUI;
    }
    public boolean isTranslateDutch(){
        return translateDutch;
    }
}
