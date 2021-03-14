package de.alex.mod_updater;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Objects;

public class Lib {
    Clip clip;
    final public Boolean useBose = false;
    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public String init_sound(String location) {
        try {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(location).getAbsoluteFile());
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            Mixer.Info info = mixerInfo[11];
//            System.out.println(String.format("Name [%s] \n Description [%s]\n\n", info.getName(), info.getDescription()));
//            System.out.println(info.getDescription());
            System.out.println(location);
            Clip clip = AudioSystem.getClip(info);
            clip.open(audioInputStream);
            this.clip = clip;
            return "playing";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ("Error with playing sound.");
        }
    }
    public String play_sound(){
        clip.start();
        return null;
    }
    public Clip new_sound(String filepath){
//        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
//        Mixer.Info info = mixerInfo[10]; //Edit this number to select output // 0 = Default
//
//        System.out.println(String.format("Name [%s]\n", info.getName()));
//        System.out.println(info.getDescription());

        try {
            Clip clip =null;
            if(useBose){
                clip = AudioSystem.getClip(findBos());
            }else{
                clip = AudioSystem.getClip(find3());
            }
            clip.open(AudioSystem.getAudioInputStream(new File(filepath)));
            clip.start();
            return clip;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
    public void setVolume(Clip clip, int level) {
        Objects.requireNonNull(clip);
        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
        if (volume != null) {
            volume.setValue((float) (level / 100.0));
        }
    }
    public Mixer.Info find3(){
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for(int i = 0;i< mixerInfo.length  ;i++){
            if(mixerInfo[i].getName().equals("Line 3 (Virtual Audio Cable)")){
                if(!mixerInfo[i].getDescription().equals("Direct Audio Device: DirectSound Playback")){
                    System.out.println("NOT RIGHT MODE");
                }
                //System.out.println("Mode: "+mixerInfo[i].getDescription());
                //Thread.dumpStack();
                return mixerInfo[i];
            }else{
                continue;
            }
        }
        mixerInfo = null;
        throw new NullPointerException();
    }
    public Mixer.Info findBos(){
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for(int i = 0;i< mixerInfo.length  ;i++){
            if(mixerInfo[i].getName().equals("Lautsprecher (Bose Mini SoundLink Stereo)")){
                if(!mixerInfo[i].getDescription().equals("Direct Audio Device: DirectSound Playback")){
                    System.out.println("NOT RIGHT MODE");
                }
                //System.out.println("Mode: "+mixerInfo[i].getDescription());
                //Thread.dumpStack();
                return mixerInfo[i];
            }else{
                continue;
            }
        }
        throw new NullPointerException();
    }
    public void download(String url, String fileName) throws Exception {
        try (InputStream in = URI.create(url).toURL().openStream()) {

            if(!Files.exists(Paths.get(fileName))){
                Files.copy(in, Paths.get(fileName));
                return;
            }else{
                return;
            }
        }
    }
    public byte[] createChecksum(File file) throws Exception {
        InputStream fis =  new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public String getMD5Checksum(File file) throws Exception {
        byte[] b = createChecksum(file);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }
}
