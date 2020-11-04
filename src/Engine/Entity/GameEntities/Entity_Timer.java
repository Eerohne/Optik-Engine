
package Engine.Entity.GameEntities;

import Engine.Entity.AbstractEntity.Entity;
import javafx.geometry.Point2D;

//Merouane Issad
public class Entity_Timer extends Entity{
    private float time;
    private float maxTime;
    
    public Entity_Timer(String name, Point2D position, float maxTime) {
        super(name, position);
        this.maxTime = maxTime;
        this.time = maxTime;
    }
    
    @Override
    public void start() {
    }
    
    @Override
    public void update() {
        System.out.println("logic!");
    }
    
}