package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ListToString<ListObject> {
    StringBuilder finalString;
    ListToString(){
        finalString = new StringBuilder();
    }
    public static<LO> ListToString<LO> of (){
        return new ListToString<>();
    }
    public void addString(ListObject s){
        finalString.append(s);
        finalString.append(".");
    }
    public String getString(){
        return finalString.toString();
    }
}
