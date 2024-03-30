package TypoCorrector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.DeepCopyTwoD;

public class TypoCorrector {
    private TypoCorrector(String filePath){
        dic = new ArrayList<>();
        readWordsFromFile(filePath);
        traceBackFlag = false;
        copyOfDirMar = DeepCopyTwoD.createEmpty();
    }

    public static TypoCorrector of (String filename){
        return new TypoCorrector(filename);
    }

    private TypoCorrector(String filePath, Boolean tBFalg){
        dic = new ArrayList<>();
        readWordsFromFile(filePath);
        traceBackFlag = tBFalg;
        copyOfDirMar = DeepCopyTwoD.createEmpty();
    }

    public static TypoCorrector of (String filename, Boolean tBFalg){
        return new TypoCorrector(filename, tBFalg);
    }

    public void readWordsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z ]", "").toLowerCase(); // Remove non-alphabetic characters and convert to lowercase
                String[] words = line.split("\\s+"); // Split the line by spaces
                for (String word : words) {
                    if (!word.isEmpty()) {
                        dic.add(word); // Add the word to the dictionary
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String closestWord(String word) {
        if(word.equals(".") || word.equals(","))
            return word;
        int curMax = -(1<<30);
        String wordMax = new String();
        int indCur = 0;

        DeepCopyTwoD copyOfDirMar = DeepCopyTwoD.createEmpty();
        for(String dicS: dic){
            int m = dicS.length();
            int n = word.length();
            int[][] scoreMat = new int[m+1][n+1];
            int[][] dirMat   = new int[m+1][n+1];

            for(int i=1; i<=n; i++){
                scoreMat[0][i] = (i-1)*gapExtend+gapOpen;
            }
            for(int i=1; i<=m; i++){
                scoreMat[i][0] = (i-1)*insertGapExtend+insertGapOpen;
            }
            for(int i=1; i<=m; i++){
                for(int j=1; j<=n; j++){
                    int curScore = 0;
                    int up   = scoreMat[i][j-1] + ((dirMat[i][j-1]==2 || dirMat[i][j-1]==0)?gapExtend:gapOpen);
                    int left = scoreMat[i-1][j] + ((dirMat[i-1][j]==3 || dirMat[i-1][j]==0)?insertGapExtend:insertGapOpen);
                    int s = (word.charAt(j-1)==dicS.charAt(i-1)?matchScore:mismatchScore);
                    int diag = scoreMat[i-1][j-1] + s;


                    curScore = diag;
                    dirMat[i][j]= s-1;

                    if(up>curScore){
                    curScore = up;
                    dirMat[i][j]= 2;
                    }
                    if(left>curScore){
                        dirMat[i][j]= 3;
                        curScore = left;
                    }
                    scoreMat[i][j] = curScore;
                }
            }

            int disCur = scoreMat[m][n];
            if(disCur>curMax){
                wordMax = dicS;
                curMax = disCur;
                if(traceBackFlag)
                    copyOfDirMar.setArray(dirMat);
            }
            indCur ++;
        }  
        return (curMax<4 && wordMax.length()>0)?wordMax:word;
        
    }
    
    public List<Integer> traceBack(){
        int [][] dirMat     = copyOfDirMar.getArray();
        copyOfDirMar.displayArray();
        List<Integer> trace = new ArrayList<>();
        if(traceBackFlag){
            int m = dirMat.length;
            if(m>0){
                int n = dirMat[0].length;
                return traceBackRecursion(dirMat, trace, m-1, n-1);
            }else{
                return trace;
            }
        }else{
            return trace;
        }
    }
    private List<Integer> traceBackRecursion(int[][] dirMat, List<Integer> trace, int i, int j){
        if(i==0 && j==0){
            return trace;
        }
        System.out.println(i + " " + j);
        int curDir = dirMat[i][j];
        if(curDir==2){
            trace.add(2);
            return traceBackRecursion(dirMat, trace, i, j-1);
        }else if(curDir==3){
            trace.add(3);
            return traceBackRecursion(dirMat, trace, i-1, j);
        }else{
            if(i>0 && j>0){
                trace.add(curDir==(matchScore-1)?0:1);
                return traceBackRecursion(dirMat, trace, i-1, j-1);
            }else if(i==0){
                trace.add(2);
                return traceBackRecursion(dirMat, trace, i, j-1);
            }else{
                trace.add(3);
                return traceBackRecursion(dirMat, trace, i-1, j);
            }
        }   
    }

    ArrayList<String> dic;
    boolean traceBackFlag;
    DeepCopyTwoD copyOfDirMar;
    final int mismatchScore  = -2;
    final int matchScore = 0;
    final int gapOpen   = -2;
    final int gapExtend  = -1;
    final int insert  = -1;
    final int insertGapOpen  = -2;
    final int insertGapExtend  = -1;
    final int minusInf = -(1<<4);
    
}
