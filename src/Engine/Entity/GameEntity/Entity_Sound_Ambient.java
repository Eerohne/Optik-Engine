/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Entity.GameEntity;

import Engine.Core.Sound.SoundManager;
import Engine.Entity.AbstractEntity.Entity;
import java.util.HashMap;
import javafx.geometry.Point2D;

/**
 *
 * @author child
 */
public class Entity_Sound_Ambient extends Entity_Sound{
    
    //variables here
    private int myVariable;
    
    public Entity_Sound_Ambient(HashMap<String, Object> propertyMap)
    {
        super(propertyMap);
    }
    
    @Override
    public void start() {
        //start logic here
    }

    @Override
    public void update() {
        mediaplayer.volumeProperty().set(playerVolume.get() * channelVolume.get());
        System.out.println(playerVolume.get() * channelVolume.get());
    }
    
    @Override
    public void handleSignal(String signalName, Object[] arguments){
        switch(signalName) //new signals here
        {
            default:
                super.handleSignal(signalName, arguments);
                
        }
    }
}
