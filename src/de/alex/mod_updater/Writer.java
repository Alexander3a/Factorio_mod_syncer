package de.alex.mod_updater;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Writer {
    static File file;
    static BufferedWriter writer;
    //static BufferedReader reader;
    //static String old;
    public static void in(String path){
//        if(HWID.bytesToHex(HWID.generateHWID()).equals(hwids.HOME.getUrl())){
//            file = new File("D:\\logs\\log"+System.currentTimeMillis()/1000+".txt");
//        }else{
//            file = new File("./log"+System.currentTimeMillis()/1000+".txt");
//        }
        file = new File(path);

        try {
            if(file.exists()){
                file.delete();
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            //reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void close(){
        try {
            writer.close();
            file =null;
            writer=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void Write(String input){
        try {
            writer.write(""+input);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static String get_Date(){
//        LocalDateTime myDateObj = LocalDateTime.now();
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("[dd.MM|HH:mm:ss]");
//        return myDateObj.format(myFormatObj);
//    }
//    public static String file_Date(){
//        LocalDateTime myDateObj = LocalDateTime.now();
//        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd.MM=HH.mm.ss");
//        return myDateObj.format(myFormatObj);
//    }

}
