package TypoCorrector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TypoCorrector {
    TypoCorrector(String filePath){
        dic = new ArrayList<>();
        readWordsFromFile(filePath);
    }

    public static TypoCorrector of (String filename){
        return new TypoCorrector(filename);
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
                    int diag = scoreMat[i-1][j-1] + (word.charAt(j-1)==dicS.charAt(i-1)?matchScore:mismatchScore);


                    curScore = diag;
                    dirMat[i][j]= 1;

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
            }
            indCur ++;
        }
        
        
        return (curMax<4 && wordMax.length()>0)?wordMax:word;
        
    }
    ArrayList<String> dic;
    final int mismatchScore  = -2;
    final int matchScore = 0;
    final int gapOpen   = -2;
    final int gapExtend  = -1;
    final int insert  = -1;
    final int insertGapOpen  = -2;
    final int insertGapExtend  = -1;
    final int minusInf = -(1<<4);
}
