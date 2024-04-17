package com.example.languagechecker;
import com.example.languagechecker.CheckerCorrector.Checker;

public class LanguageChecker {
    private Checker internal_checker;

    public LanguageChecker() {
        internal_checker = new Checker();
    }

    public String analyzeSentence(String sentence){
        return internal_checker.checkerAndroidInterface(sentence);
        //return "5";
    }
}
