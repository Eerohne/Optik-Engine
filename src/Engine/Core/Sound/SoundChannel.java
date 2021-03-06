/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Core.Sound;

import Commons.SettingsManager.Settings;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 *
 * @author child
 */
public class SoundChannel {
    public ReadOnlyObjectWrapper<Double> volume;
    private ArrayList<MediaPlayer> players;
    private boolean temporaryChannel; //should players in this channel be cleared on map load?
    
    //only when a channel is a slave of a master channel
    private String masterChannelName = "";
    private Double realVolume = 1.0;
    
    public SoundChannel(String configValueName)
    {
        this(configValueName, "", true);
    }
    
    public SoundChannel(String configValueName, String masterChannelName, boolean isTemporary)
    {
        if(!configValueName.equals(null))
            volume = new ReadOnlyObjectWrapper<>(Settings.getDouble(configValueName)); 
        else
            volume = new ReadOnlyObjectWrapper<>(1.0);//in case the config file doesnt hold the volume value

        
        if(!masterChannelName.isEmpty()){
            this.masterChannelName = masterChannelName;
            SoundManager.getChannel(masterChannelName).volume.addListener((ObservableValue<? extends Double> observable, Double oldValue, Double newValue) -> {
                this.setVolume(this.realVolume);
            });
            
            this.volume.set(SoundManager.getChannel(masterChannelName).volume.get() * this.volume.get());
        }
        else
            this.masterChannelName = "";
        
        this.temporaryChannel = isTemporary;
        players = new ArrayList<>();
    }
    
    public void setVolume(Double volume)
    {
        this.realVolume = volume;
        if(this.masterChannelName.isEmpty())//is not a slave channel
        {
            this.volume.set(volume);
        }
        else
        {
            Double masterVolume = SoundManager.getChannel(masterChannelName).volume.get();
            this.volume.set(realVolume * masterVolume);
        }
    }
    
    public void addPlayer(MediaPlayer player)
    {
        players.add(player);
        player.volumeProperty().bind(this.volume);
    }
    
    public void removePlayer(MediaPlayer player)
    {
        for(MediaPlayer p : players) {
            if(p.equals(player)){
                player.dispose();
                players.remove(p);
            }
        }
    }
    
    public boolean isTemporary()
    {
        return temporaryChannel;
    }
    
    public void clear()
    {
        for(MediaPlayer p : players) {
            p.dispose();
        }
        players.clear();
    }
}
