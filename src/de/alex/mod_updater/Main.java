package de.alex.mod_updater;

import jdk.internal.dynalink.support.BottomGuardingDynamicLinker;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

import javax.swing.text.StyledEditorKit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Main {
    public static HashMap<String,String> path_hash =new HashMap<>();
    public static HashMap<String,String> online_list=new HashMap<>();
    public static ArrayList<String> to_delete= new ArrayList();
    public static ArrayList<String> to_add= new ArrayList();
    public static Lib lib = new Lib();
    public static String folder = "";
    public static Boolean debug;
    public static void main(String[] args) {
	// write your code here
        try {
            if(args[0].equals("-b")){
                System.exit(1);
            }
        }catch (Exception e){

        }
        File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
        if(jar.getAbsolutePath().endsWith(".jar")){
            Main.debug = false;
            String sum = null;
            try {
                sum = lib.getMD5Checksum(jar);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                File xd = File.createTempFile("test",".txt");
                xd.delete();
                lib.download("https://ts3byalex.ddns.net/pw/modsupdaterhash.txt",xd.getAbsolutePath());
                BufferedReader br = new BufferedReader(new FileReader(xd));
                String out ="";
                String online_sum="";
                while (out!=null){
                    out = br.readLine();
                    try {
                        if(out.length()==32){
                            online_sum = out;
                        }
                    }catch (Exception e){
                        
                    }

                }
                if(!online_sum.equals("")){
                    if(!online_sum.equals(sum)){
                        //System.out.println(sum+" "+online_sum);
                        File xd2 = File.createTempFile("test",".jar");
                        xd2.delete();
                        lib.download("https://ts3byalex.ddns.net/pw/updater.jar",xd2.getAbsolutePath());
                        Runtime.getRuntime().exec("java -jar "+xd2+" -url https://ts3byalex.ddns.net/pw/factorio_mod_updater.jar -file "+jar.getAbsolutePath()+" -args -b");
                        System.out.println("updating");
                        System.exit(1);
                    }
                }else{
                    System.out.println("found no online sum");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            debug=true;
        }
        try {
            if(args[0].equals("--mods-folder")){
                folder=args[1].replaceAll("\"","");
            }
        }catch (Exception e){
            System.out.println("no folder provided");
            return;
        }

        if(folder.equals("")){
            System.out.println("no folder provided");
            return;
        }
        downloadlist();
        createlist();
        checklists();
        sum_changes();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        make_changes();
        System.out.println("done");
    }
    public static void make_changes(){
        to_delete.stream().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                File f = new File(folder+s);
                f.delete();
                //System.out.println("deleted "+s);
            }
        });
        int[] i ={0};
        to_add.stream().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                File f = new File(folder+s);
                File main_folder = new File(folder);
                Boolean stop = true;
                File tempfile=null;
                while (stop){
                    if(!f.getParentFile().getAbsolutePath().equals(main_folder.getAbsolutePath())){
                        tempfile = f.getParentFile();
                        if(f.getParentFile().exists()){
                            stop=false;
                        }else{
                            tempfile.mkdirs();
                        }
                    }else{
                        stop=false;
                    }
                }
                try {
                    lib.download("https://ts3byalex.ddns.net/pw/fmods"+s.replaceAll("\\\\","/").replaceAll(" ","%20"),f.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i[0]++;
                System.out.println("downloaded: "+i[0]+"/"+to_add.size()+"\t"+s);
            }
        });
    }
    public static void sum_changes(){
        if(!to_delete.isEmpty()){
            System.out.println("Deleting:");
            to_delete.stream().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
//                File f = new File(folder+s);
//                System.out.println(f.getAbsolutePath());
                    System.out.println(s);
                }
            });
        }
        if(!to_add.isEmpty()){
            System.out.println("Adding:");
            to_add.stream().forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    //File f = new File(folder+s);
                    System.out.println(s);
                }
            });
        }
        //System.out.println("listed");
    }
    public static void checklists(){
        path_hash.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                try {
                    if(!online_list.get(s).equals(s2)){
//                        System.out.println(s.replace(folder,"")+" online:"+online_list.get(s.replace(folder,""))+" offline:"+path_hash.get(s));
                        //System.out.println("diff "+s);
                        to_delete.add(s);
                        to_add.add(s);
                    }
                }catch (Exception e){
                    //System.out.println(s+" only local but not online");
                    to_delete.add(s);
                    //System.out.println(online_list.containsKey(s.replace(folder,""))+" "+s.replace(folder,""));
                }
            }
        });
        online_list.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                try {
//                    if(s.equals("\\aai-vehicles-flame-tumbler_0.6.1\\thumbnail.png")){
//                        if(path_hash.containsKey(s)){
//                            System.out.println("true");
//                        }else{
//                            System.out.println("false");
//                        }
//                    }
                    if(!path_hash.containsKey(s)){
                        //System.out.println(s+" only online but not local");
                        to_add.add(s);
                    }
                    //if(!path_hash.get(s.replace(folder,"")).equals(s2)){
                        //System.out.println(s.replace(folder,"")+" online:"+path_hash.get(s.replace(folder,""))+" offline:"+online_list.get(s));
                    //}
                }catch (Exception e){
                    e.printStackTrace();
                    //System.out.println(s.replace(folder,"")+" only online but not local");
                    //System.out.println(online_list.containsKey(s.replace(folder,""))+" "+s.replace(folder,""));
                }
            }
        });
    }
    public static void downloadlist(){
        try {
            File temp = new File(Files.createTempFile("temp",".txt").toUri());
            temp.delete();
            lib.download("https://ts3byalex.ddns.net/pw/modshash.txt",temp.getAbsolutePath());
            BufferedReader br = new BufferedReader(new FileReader(temp));
            String cur = "";
            while (cur!=null){
                try {
                    online_list.put(cur.split("<>")[0],cur.split("<>")[1]);
                    //System.out.println("put "+cur);
                }catch (Exception e){

                }
                cur=br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void createlist(){
        File folder = new File(Main.folder);
        //Main.folder = folder.getAbsolutePath();
        if(folder.listFiles()==null){
            System.out.println("wrong input");
            System.exit(10);
        }
        Arrays.stream(folder.listFiles()).forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                if(file.isFile()){
                    try {
                        path_hash.put(file.getAbsolutePath().replace(Main.folder,""),lib.getMD5Checksum(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    try {
//                        path_hash.put(file.getAbsolutePath(),lib.getMD5Checksum(file));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }else{
                    unpack_folder(file);
                }
            }
        });
        if(debug){
            Writer.in(".\\modshash.txt");
            path_hash.forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String s, String s2) {
                    Writer.Write(s.replace(folder.getAbsolutePath(),"")+"<>"+ s2);
                }
            });
        }
    }
    public static void unpack_folder(File file){
        Arrays.stream(file.listFiles()).forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                if(file.isDirectory()){
                    unpack_folder(file);
                }else{
                    try {
                        path_hash.put(file.getAbsolutePath().replace(folder,""),lib.getMD5Checksum(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
