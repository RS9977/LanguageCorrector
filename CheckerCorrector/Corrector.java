import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DBinterface.DBinterface;
import DirectedGraph.BasicGraph;
import DirectedGraph.DirectedGraph;
import GUI.SelectCorrectionGUI;
import GUI.GUIListener;
import util.ArgumentParser;
import util.JsonMaker;
import util.PhraseExtractor;
import util.SentenceExtractor;
import util.StringFileWriter;
import Translator.DirectTranslator;
import StateMachine.*;


public class Corrector implements GUIListener {
    SentenceExtractor extractorGUI;
    private String curSentenceGUI;
    private DBinterface dbInterfaceGUI;
    private StringFileWriter stringWriterGUI;
    private List<String> extractedSentencesGUI;
    private DirectedGraph<State> graphGUI;
    private int senteceIndGUI;
    public void start() {
        
        this.dbInterfaceGUI = new DBinterface();
        this.graphGUI = new BasicGraph().getGraph();
        this.stringWriterGUI = StringFileWriter.of("corrected.txt");
        this.senteceIndGUI = 0;
        this.curSentenceGUI = extractedSentencesGUI.get(0);
        String tempString = dbInterfaceGUI.correctTokenInDatabase(this.curSentenceGUI.toLowerCase(), graphGUI, 1, false);
        SelectCorrectionGUI gui = new SelectCorrectionGUI(this, this.curSentenceGUI);
    }

    @Override
    public String updateFlagsAndLabel(List<Boolean> flags) {
        String tempString = dbInterfaceGUI.correctTokenInDatabaseGUI(this.curSentenceGUI.toLowerCase(), graphGUI, flags);
        if(tempString.contains("|")){
            String[] parts = tempString.split("|");
            //this.curSentenceGUI = parts[0];
            int ind = Integer.parseInt(parts[1]);
            List<Boolean> flagsTemp = new ArrayList<>();
            for(int k=0; k<flags.size(); k++){
                if(k<=ind)
                    flagsTemp.add(flags.get(k));
                else
                    flagsTemp.add(true);
            }
            String tempSentenceGUI = dbInterfaceGUI.correctTokenInDatabase(this.curSentenceGUI.toLowerCase(), graphGUI, 1, false, flags);
        }else{
            this.curSentenceGUI = tempString;
            String tempSentenceGUI = dbInterfaceGUI.correctTokenInDatabase(this.curSentenceGUI.toLowerCase(), graphGUI, 1, false);
        }
        return this.curSentenceGUI;
    }

    @Override
    public String loadNextSentece() {
        
        stringWriterGUI.appendString(this.curSentenceGUI);
        try {
            stringWriterGUI.writeToFile();
           // System.out.println("Corrected version has been written to the file.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }

        this.senteceIndGUI++;
        if(this.senteceIndGUI>=extractedSentencesGUI.size()){
            System.out.println("end!");
            return "";
        }
        
        this.curSentenceGUI = extractedSentencesGUI.get(senteceIndGUI);
        String tempSentenceGUI = dbInterfaceGUI.correctTokenInDatabase(this.curSentenceGUI.toLowerCase(), graphGUI, 1, false);
        return this.curSentenceGUI;

    }
    Corrector(SentenceExtractor extractor){
        this.extractedSentencesGUI = extractor.getSentences();
    }
    public static void main(String[] args) {
        //DirectedGraph<State> graph = new DirectedGraph<>();
        
        ArgumentParser argPars = ArgumentParser.of(args);
        BasicGraph basicGraphClass = new BasicGraph();
        DBinterface dbInterface = new DBinterface();
        DirectedGraph graph = basicGraphClass.getGraph();
        StringFileWriter stringWriter = StringFileWriter.of("corrected.txt");
        
            
        StringFileWriter.deleteFile("correction_details.txt");
        if(argPars.isCheckFile()){
            if(argPars.isCorrectionGUI()){    
                    Corrector corrector = new Corrector(SentenceExtractor.of(argPars.getFileName()));
                    corrector.start();
            }else{
                SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
                List<String> extractedSentences = extractor.getSentences();
                for (String sentence : extractedSentences) {
                    System.out.println("Sentence: " + sentence);
                    String tempString = dbInterface.correctTokenInDatabase(sentence.toLowerCase(), graph, 2, true);
                    stringWriter.appendString(tempString);

                    
                    System.out.println("##########################################################");
                }
            }
            try {
                stringWriter.writeToFile();
                System.out.println("Corrected version has been written to the file.");
            } catch (IOException e) {
                System.err.println("An error occurred while writing to the file: " + e.getMessage());
            }
        }else if(argPars.isCheckSentence()){
            if(argPars.isCorrectionGUI()){  
                Corrector corrector = new Corrector(SentenceExtractor.of(argPars.getSentence()));  
                corrector.start();
            }else{
                System.out.println("Sentence: " + argPars.getSentence());
                stringWriter.appendString(dbInterface.correctTokenInDatabase(argPars.getSentence().toLowerCase(), graph, 2, true));
                try {
                    stringWriter.writeToFile();
                    System.out.println("Corrected version has been written to the file.");
                } catch (IOException e) {
                    System.err.println("An error occurred while writing to the file: " + e.getMessage());
                }
                System.out.println("##########################################################");
            }
        }else if(argPars.isTranslateDutch()){
            DirectTranslator directTranslator = DirectTranslator.make();
            directTranslator.loadWordMapFromFile("SQLite/word_map.txt");
            if(argPars.isCheckFile()){
                SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
                List<String> extractedSentences = extractor.getSentences();
                for (String sentence : extractedSentences) {
                    System.out.println("Sentence: " + sentence);
                    String tempString = directTranslator.replaceWordsInSentence(sentence.toLowerCase());
                    stringWriter.appendString(tempString);
                    System.out.println("##########################################################");
                }
            }else{
                
                System.out.println("Sentence: " + argPars.getSentence());
                stringWriter.appendString(directTranslator.replaceWordsInSentence(argPars.getSentence().toLowerCase()));
                System.out.println("##########################################################");
            }
            try {
                stringWriter.writeToFile();
                System.out.println("Corrected version has been written to the file.");
            } catch (IOException e) {
                System.err.println("An error occurred while writing to the file: " + e.getMessage());
            }
        }
          
    }
}
