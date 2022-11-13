package de.alex.mod_updater;


import javax.swing.text.StyledEditorKit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//E:\Users\Alex\Desktop\Desktop\mod manager\mods\1.0

public class Main {
    public static HashMap<String,String> path_hash =new HashMap<>();
    public static HashMap<String,String> online_list=new HashMap<>();
    public static ArrayList<String> to_delete= new ArrayList();
    public static ArrayList<String> to_add= new ArrayList();
    public static Lib lib = new Lib();
    public static File folder = null;
    public static Boolean debug;
    public static Boolean adelina_mode= false;
    public static Boolean gen_mode = false;
    public static Boolean unix_like=false;
    public static void main(String[] args) {
	// write your code here
        try {
            if(args[0].equals("-b")){
                System.exit(1);
            }
        }catch (Exception e){

        }
        System.out.println("v2");
        if(System.getProperty("os.name").contains("Linux")){
            System.out.println("using unix syntax");
            unix_like=true;
        }

//        File jar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
//        if(jar.getAbsolutePath().endsWith(".jar")){
//            Main.debug = false;
//            String sum = null;
//            try {
//                sum = lib.getMD5Checksum(jar);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                File xd = File.createTempFile("test",".txt");
//                xd.delete();
//                lib.download("https://ts3byalex.ddns.net/pw/modsupdaterhash.txt",xd.getAbsolutePath());
//                BufferedReader br = new BufferedReader(new FileReader(xd));
//                String out ="";
//                String online_sum="";
//                while (out!=null){
//                    out = br.readLine();
//                    try {
//                        if(out.length()==32){
//                            online_sum = out;
//                        }
//                    }catch (Exception e){
//
//                    }
//
//                }
//                if(!online_sum.equals("")){
//                    if(!online_sum.equals(sum)){
//                        //System.out.println(sum+" "+online_sum);
//                        File xd2 = File.createTempFile("test",".jar");
//                        xd2.delete();
//                        lib.download("https://ts3byalex.ddns.net/pw/updater.jar",xd2.getAbsolutePath());
//                        Runtime.getRuntime().exec("java -jar "+xd2+" -url https://ts3byalex.ddns.net/pw/factorio_mod_updater.jar -file "+jar.getAbsolutePath()+" -args -b");
//                        System.out.println("updating");
//                        System.exit(1);
//                    }
//                }else{
//                    System.out.println("found no online sum");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else{
//            debug=true;
//        }
        for(int i = 0;i< args.length  ;i++){
            try {
                if(args[i].equals("--mods-folder")){
                    String file = args[i+1].replace("\"","");
                    if((int)file.charAt(file.length()-1)==32){
                        file=file.substring(0,file.length()-1);
                    }
                    folder=new File(file);
                }
                if(args[i].equalsIgnoreCase("-adelina")){
                    adelina_mode=true;
                }
                if(args[i].equalsIgnoreCase("-gen")){
                    gen_mode=true;
                }
            }catch (Exception e){
                System.out.println("no folder provided");
                return;
            }

        }

        if(folder==null){
            System.out.println("no folder provided");
            return;
        }
        if(!folder.exists()){
            System.out.println("folder does not exist");
            return;
        }
        if(gen_mode){
            createlist();
            System.exit(1);
            return;
        }
        downloadlist();
        createlist();
        checklists();
        sum_changes();


        if(to_delete.isEmpty()&&to_add.isEmpty()){
            System.out.println("nothing to change");
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            Scanner scanner = new Scanner(System.in);
            System.out.println(folder.getAbsolutePath());
            System.out.print("enter y to continue: ");
            if(scanner.nextLine().equals("y")){
                make_changes();
            }
            scanner.close();
        }
        System.out.println("done");
    }
    public static void make_changes(){
        if(adelina_mode){
            System.out.println("making changes");
        }
        to_delete.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                File f = new File(folder+s);
                if(unix_like){
                    String abs =f.getAbsolutePath().replaceAll("\\\\","/");
                    f=new File(abs);
                }
                f.delete();
                //System.out.println("deleted "+s);
                if(adelina_mode){
                    System.out.println("deleted "+s);
                }
            }
        });
        int[] i ={0};
        to_add.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                File f = new File(folder+s);
                if(unix_like){
                    String abs =f.getAbsolutePath().replaceAll("\\\\","/");
                    f=new File(abs);
                }
                Boolean stop = true;
                File tempfile=null;
                while (stop){
                    if(!f.getParentFile().getAbsolutePath().equals(folder.getAbsolutePath())){
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
        if(adelina_mode){
            System.out.println("printing changes");
        }
        if(!to_delete.isEmpty()){
            System.out.println("Deleting:");
            to_delete.forEach(new Consumer<String>() {
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
            to_add.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    //File f = new File(folder+s);
                    System.out.println(s);
                }
            });
        }
        if(adelina_mode){
            System.out.println("done printing changes");
        }
        //System.out.println("listed");
    }
    public static void checklists(){
        if(adelina_mode){
            System.out.println("checking lists");
        }
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
        if(adelina_mode){
            System.out.println("checked list for things to delete");
        }
        online_list.forEach((s, s2) -> {
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
        });
        if(adelina_mode){
            System.out.println("checked list for things to add");
        }
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
                    if(adelina_mode){
                        System.out.println("added "+cur);
                    }
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
        //Main.folder = folder.getAbsolutePath();
        if(folder.listFiles()==null){
            System.out.println("wrong input");
            System.exit(10);
        }
        Long time = System.currentTimeMillis();
        unpack_folder(folder);
//        while (!threads.isEmpty()){
//            System.out.println(threads.size());
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            ArrayList<Thread> fuck = new ArrayList();
//            try {
//                threads.forEach(new Consumer<Thread>() {
//                    @Override
//                    public void accept(Thread thread) {
////                        System.out.println(thread.isAlive());
//                        if(thread==null){
//                            fuck.add(thread);
//                            return;
//                        }
//                        if(!thread.isAlive()){
//                            fuck.add(thread);
//                        }
//                    }
//                });
//            }catch (ConcurrentModificationException e){
//                System.out.println("fuck");
//            }
//            threads.removeAll(fuck);
//        }
        System.out.println("Took: "+(System.currentTimeMillis()-time)/1000+" s");

        if(gen_mode){
            Writer.in(".\\modshash.txt");
            path_hash.forEach((s, s2) -> Writer.Write(s.replace(folder.getAbsolutePath(),"")+"<>"+ s2));
        }
    }
    public static ArrayList<Thread> threads = new ArrayList<>();
    public static void unpack_folder(File file){
        for (File listFile : file.listFiles()) {
//            System.out.println(file.getAbsolutePath().replaceAll(" ","-"));
//            System.out.println(listFile.getName().replaceAll(" ","-"));
//            System.out.println(listFile.getAbsolutePath().replaceAll(" ","-"));
//            listFile = new File(listFile.getAbsolutePath());
            if(adelina_mode){
                System.out.println("unpacking "+listFile.getAbsolutePath());
            }
            if (listFile.isDirectory()) {
                unpack_folder(listFile);
                if(adelina_mode){
                    System.out.println("made hash of folder " + listFile.getAbsolutePath().replace(folder.getAbsolutePath(),""));
                }
            } else {
                try {
                    path_hash.put(listFile.getAbsolutePath().replace(folder.getAbsolutePath(),""), lib.getMD5Checksum(listFile));
                    if(adelina_mode){
                        System.out.println("made hash of file " + listFile.getAbsolutePath().replace(folder.getAbsolutePath(),""));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        Arrays.stream(file.listFiles()).forEach(file1 -> {
////                Thread[] t = {null};
////                t[0] = new Thread(new Runnable() {
////                    @Override
////                    public void run() {
//                    if (file1.isDirectory()) {
//                        unpack_folder(file1);
//                        if(adelina_mode){
//                            System.out.println("made hash of folder " + file1.getAbsolutePath().replace(folder.getAbsolutePath(),""));
//                        }
//                    } else {
//                        try {
//                            System.out.println(file1.getAbsolutePath());
//                            System.out.println(folder.getAbsolutePath());
//                            path_hash.put(file1.getAbsolutePath().replace(folder.getAbsolutePath(),""), lib.getMD5Checksum(file1));
//                            if(adelina_mode){
//                                System.out.println("made hash of file " + file1.getAbsolutePath().replace(folder.getAbsolutePath(),""));
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
////                        threads.remove(t[0]);
////                    }
////                });
////                t[0].start();
////                threads.add(t[0]);
//
//        });
    }
}
