package Javapro;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectTest {
    public static void main(String[] args) {
        String userDataPath = "";
        String dir = "C:\\Users\\"+System.getProperty("user.name")+"\\Documents";
        Path p = Paths.get(dir+"\\Excel");
        if(!Files.exists(p)){
            try{
                Files.createDirectories(p);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        userDataPath = dir + "\\userData.xlsx";
        new entryGUI(userDataPath);
    }
}
