/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Core.Sound;

import static Engine.Core.Game.mediaPlayer;
import Engine.Util.RessourceManager.ResourceLoader;
import java.util.HashMap;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author child
 */
public class SoundManager {
    private static HashMap<String, SoundChannel> channels = new HashMap<>();
    
    public static void init()
    {
        addChannel("master", "snd_master");
        addChannel("game", "snd_game", "master");
        addChannel("music", "snd_music", "master");
    }
    
    public static void addChannel(String channelName, String configValueName)
    {
        channels.put(channelName, new SoundChannel(configValueName));
    }
    
    public static void addChannel(String channelName, String configValueName, String masterNameChannel)
    {
        channels.put(channelName, new SoundChannel(configValueName, masterNameChannel, true));
    }
    
    public static SoundChannel getChannel(String channelName)
    {
        return channels.get(channelName);
    }
    
    public static void changeChannelVolume(String channelName, Double volume)
    {
        try{
            if(volume != null)
                channels.get(channelName).setVolume(volume);
            else
                System.out.println("NULL VOLUME WAS SENT TO CHANNEL '"+ channelName+ "'");
        }
        catch(NullPointerException e)
        {
            System.out.println(e);
        }
    }
    
    public static MediaPlayer createPlayer(String soundPath, String channelName, boolean loop)
    {
        MediaPlayer mediaPlayer = new MediaPlayer(ResourceLoader.loadAudio(soundPath));
         if(loop)
             mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
         
        addPlayer(channelName, mediaPlayer);
        return mediaPlayer;
    }
    
    public static void addPlayer(String channelName, MediaPlayer player)
    {
        channels.get(channelName).addPlayer(player);
    }
    
    public static void clear()
    {
        channels.forEach((k, v) -> {
            if(v.isTemporary())
                v.clear();
        });
    }
    
}
