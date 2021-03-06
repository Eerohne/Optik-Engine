/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.Entity.AbstractEntity;

import Engine.Core.Exceptions.EntityCreationException;
import Engine.Entity.AbstractEntity.Entity;
import Engine.RaycastRenderer.Renderer;
import java.util.HashMap;
import javafx.geometry.Point2D;

//Merouane Issad
public abstract class Entity_Player extends Entity{

    protected float rotation; //view rotation in the world
    
    public Entity_Player(String name, Point2D position, float rotation) {
        super(name, position);
        this.rotation = rotation;
    }
    
    public Entity_Player(HashMap<String, Object> propertyMap) throws EntityCreationException
    {
        super(propertyMap);
        try{
            this.rotation = Float.parseFloat((String) propertyMap.get("rotation"));
            if(propertyMap.containsKey("height"))
                this.height = Float.parseFloat((String) propertyMap.get("height"));
            else
                this.height = 0.5f;
        }
        catch(Exception e)
        {
            System.out.println(e);
            throw new EntityCreationException("could not initialize entity '"+this.name+"' with the given properties");
        }
    }
    
    @Override
    public void start()
    {
        Renderer.setPlayer(this);
    }

    public float getRotation() {
        return rotation; 
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
}
