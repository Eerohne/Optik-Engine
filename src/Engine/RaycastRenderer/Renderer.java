/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Engine.RaycastRenderer;

import Engine.Core.Game;
import Engine.Entity.AbstractEntity.SpriteEntity;
import Engine.Entity.GameEntity.Entity_Environment;
import Engine.Entity.GameEntity.Entity_Player;
import Engine.Level.Level;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 *
 * @author Jeffrey
 */

/*  TODO FEATURES:
*   Render Level        +
*   Render Entities     +
*   Render Textures     +
*   Render Floor/Ceil   +-
*   Game Minimap        -
*/
public class Renderer {
    
    private static HashMap<String, SpriteEntity> spriteEntities = new HashMap<>();
    
    private static Canvas frame = new Canvas();
    private static GraphicsContext gc = frame.getGraphicsContext2D();
    private static double screenWidth = frame.getWidth(), screenHeight = frame.getHeight();
    
    private static Entity_Player player;
    private static Entity_Environment env;
    private static float fov=70f; //default field of view (degrees)
    private static double viewD=5.0; //default view distance
    private static int res = 1;
    
    private Renderer(){}
    
    public static void setPlayer(Entity_Player player){Renderer.player = player;}
    public static void setEnvironment(Entity_Environment env){Renderer.env=env;};
    
    public static void setCanvas(Canvas canvas){
        frame = canvas;
        gc = canvas.getGraphicsContext2D();
        screenWidth = canvas.getWidth();
        screenHeight = canvas.getHeight();
    }
    
    public static void setEntityList(HashMap<String, SpriteEntity> list){spriteEntities = list;}
    public static void addEntity(SpriteEntity spriteEntity){spriteEntities.put(spriteEntity.getName(), spriteEntity);}
    public static void removeEntity(String name){spriteEntities.remove(name);}
    
    public static void setFov(float angdeg){fov = angdeg;} // set the field of view
    public static void setViewDistance(double dist){viewD = dist;} // set the view distance
    public static void setResolution(int resolution){res = resolution;}
    
    public static void resize(){
        screenWidth = frame.getWidth();
        screenHeight = frame.getHeight();
    }
    
    //renders one frame
    public static void render(){
        //change colors here
        //gc.clearRect(0, 0, screenWidth, screenHeight);
        //ceiling
        //gc.setFill(Color.LIGHTBLUE);
        //gc.fillRect(0, 0, screenWidth, screenHeight);
        //floor
        //gc.setFill(Color.GREEN);
        //gc.fillRect(0, screenHeight/2.0, screenWidth, screenHeight/2.0);
        
        ArrayList<HitPoint> hPoints = getHitPoints();
        renderFloorCeiling(hPoints);
        renderWalls(hPoints);
        //renderEntities(hPoints);
    }
    
    //calculates all the of the points of intersection with a wall
    private static ArrayList<HitPoint> getHitPoints(){
        ArrayList<HitPoint> hPoints = new ArrayList((int) screenWidth);
        Level level = Game.getCurrentLevel();
        Point2D cam = player.getPosition();
        float camA = player.getRotation();
        
        final double tileX, tileY; //player grid position
        tileX = Math.floor(cam.getX());
        tileY = Math.floor(cam.getY());
        
        //creates all the hit points
        for(int r=0; r<screenWidth;r+=res){
            //angle
            double rayA = Math.toDegrees(Math.atan
            ((  2.0
               *Math.tan( Math.toRadians(fov/2.0) )
               *( (r/screenWidth)-0.5 ) 
            )));
            rayA += camA;
            
            while (rayA<0 || rayA>=360) {                
                if(rayA < 0)  rayA+=360;
                if(rayA >=360)rayA-=360;
            }
            
            boolean upward, rightward;
            upward = rayA<180; rightward = (rayA<90 || rayA>270);
            
            final double tan = Math.tan(Math.toRadians(rayA));
            final double cotan = 1.0/tan;
            
            double stepX=0, stepY=0;
            
            Point2D rH = Point2D.ZERO, rV = Point2D.ZERO;
            //intersects with vertical
            if(rightward){//looking right (+x)
                rV = rV.add(tileX+1, cam.getY());
                rV = rV.subtract(0, (cam.getX()-rV.getX())*tan);
                stepX = 1; stepY = tan;
            }
            if(!rightward){//looking left (-x)
                rV = new Point2D(tileX  , cam.getY());
                if(cam.getX() == tileX)rV = rV.add(-1,0);
                rV = rV.subtract(0, (cam.getX()-rV.getX())*tan);
                stepX = -1; stepY = -tan;
            }
            if(tan>80.0 || tan<-80.0){//looking directly up or down
                rV = new Point2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
            
            HitPoint v = HitPoint.ZERO;
            for(int i=0;i<viewD;i++){
                int x = (int)Math.floor(rV.getX());
                int y = (int)Math.floor(rV.getY());
                if(x>=level.width || y>= level.height || x<0 || y<0)break;
                if(level.isWall(x-1, y) && !rightward){v = new HitPoint(rV,'v', x-1, y);break;}
                if(level.isWall(x, y)){v = new HitPoint(rV,'v', x, y);break;}
                rV = rV.add(stepX, stepY);
                if(i+1.0==viewD){
                    rV = cam.add(viewD*Math.cos(Math.toRadians(rayA)), viewD*Math.sin(Math.toRadians(rayA)));
                    v = new HitPoint(rV, 'f', -1, -1);
                }
            }
            
            //intersects with horizontal
            stepX =0; stepY=0;
            if(upward){//looking up (+y)
                rH = rH.add(cam.getX(), tileY+1);
                rH = rH.subtract((cam.getY()-rH.getY())*cotan, 0);
                stepX = cotan; stepY = 1;
            }
            if(!upward){//looking down (-y)
                rH = rH.add(cam.getX(), tileY);
                if(cam.getY() == tileY)rH = rH.add(0, -1);
                rH = rH.subtract((cam.getY()-rH.getY())*cotan, 0);
                stepX = -cotan; stepY = -1;
            }
            if(cotan>80.0 || cotan <-80.0){//looking directly left or right
                rH = new Point2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
            
            HitPoint h = HitPoint.ZERO;
            for(int i=0;i<viewD;i++){
                int x = (int)Math.floor(rH.getX());
                int y = (int)Math.floor(rH.getY());
                if(x>=level.width || y>= level.height || x<0 || y<0)break;
                if(level.isWall(x, y-1) && !upward){h = new HitPoint(rH,'h', x, y-1);break;}
                if(level.isWall(x, y)){h = new HitPoint(rH,'h', x, y);break;}
                rH = rH.add(stepX, stepY);
                if(i+1.0==viewD){
                    rH = cam.add(viewD*Math.cos(Math.toRadians(rayA)), viewD*Math.sin(Math.toRadians(rayA)));
                    h = new HitPoint(rH, 'f', -1, -1);
                }
            }
            
            double hLength = cam.distance(rH);
            double vLength = cam.distance(rV);
            if(rH.equals(rV)){
                hPoints.add(h);
            }
            else if(hLength<vLength){
                hPoints.add(h);
            }
            else{
                hPoints.add(v);
            }
            
        }
        return hPoints;
    }
    
    //renders floor/ceiling
    private static void renderFloorCeiling(ArrayList<HitPoint> hPoints){
        Level level = Game.getCurrentLevel();
        double camA = Math.toRadians(player.getRotation());
        double sin = Math.sin(camA);
        double cos = Math.cos(camA);
        
        //direction vector of the player
        Point2D dir = new Point2D( Math.cos(camA), Math.sin(camA) );
        
        //vector perpendicular (90 deg rotates) to the dir vertor
        Point2D perpDir = new Point2D(-sin, cos);
        
        //the hit point furthest away from the player (smallest height)
        Point2D toWall = Point2D.ZERO;
        for(HitPoint hPoint: hPoints){
            if (toWall.magnitude()<hPoint.subtract(player.getPosition()).magnitude()) {
                toWall = hPoint.subtract(player.getPosition());
            }
        }
        
        double distance = toWall.magnitude();
        distance = distance*Math.cos(Math.toRadians(dir.angle(toWall)));
        double height = screenHeight/distance;
        double lineBottom = ((screenHeight-height)/2.0)+height;
        
        
        
        double y = 0.0; //y position on the scrren
        
        if(env.hasSky()){ //color the sky and jump to the floor
            y=lineBottom;
            gc.setFill(env.getSkyColor());
            gc.fillRect(0, 0, screenWidth, screenHeight/2.0);
        }
        
        while (y<(screenHeight)) //for every horizontal line of pixels on the screen
        { 
            double fdist = Math.abs( 0.5/(( (y/screenHeight) -0.5)) );
            
            if(fdist<level.height && fdist<level.width  && fdist<env.getFogFarDistance() )
            {
                Point2D gridPos = player.getPosition().add(fdist*cos, fdist*sin);
                Point2D gridLeft = gridPos.subtract(perpDir.multiply(fdist*Math.tan(fov/2.0)));
                Point2D perpStep = perpDir.multiply(res*2*gridLeft.distance(gridPos)/screenWidth);

                gridPos = gridLeft;

                for(int x=0; x<screenWidth; x+=res){
                    int cx = (int)gridPos.getX(), cy = (int)gridPos.getY();
                    if(cx<0 ||cy<0 || cx>level.width || cy>level.height)
                    {
                    }
                    else
                    {
                        if(!level.isWall(cx, cy))
                        {
                        Image texture = level.getCellTexture(cx, cy);
                        int tx = (int)(texture.getWidth() *(Math.abs(gridPos.getX()%1.0)));
                        int ty = (int)(texture.getHeight()*(Math.abs(gridPos.getY()%1.0)));
                        gc.drawImage(texture, tx, ty, 1, 1, x, y, res, res);
                        }
                    }
                    gridPos = gridPos.add(perpStep);
                }
            }
            
            y+=res;
        }
        
        
    }
    
    //renders level
    private static void renderWalls(ArrayList<HitPoint> hPoints){
        for(int i=0;i<screenWidth/res;i++){
            drawWallLine(i, hPoints.get(i));
        }
    }
    
    //renders entities
    private static void renderEntities(ArrayList<HitPoint> hPoints){
        Point2D cam = player.getPosition();
        float camA = player.getRotation();
        final Point2D dir = new Point2D(Math.cos(Math.toRadians(camA)), Math.sin(Math.toRadians(camA)));
        PriorityQueue<SpriteEntity> sprites = new PriorityQueue(new EntityDistanceComparator());
        
        //sort entities by distance to player
        for(String name: spriteEntities.keySet()){
            SpriteEntity entity = spriteEntities.get(name);
            sprites.offer(entity);
        }
        
        //draw entities
        while(!sprites.isEmpty())
        {
            SpriteEntity e = sprites.poll();
            Point2D ePos = e.getPosition().subtract(cam); //vector from player to entity
            if(dir.angle(ePos)<(0.1+fov/2.0) && ePos.magnitude()<viewD){ //if entity is within the player's fov and within view range
                
                double fovR = -Math.toRadians(fov/2f);
                Point2D fovLeft = new Point2D( //vector representing the left edge of the fov triangle
                        dir.getX()*Math.cos(fovR)-dir.getY()*Math.sin(fovR),
                        dir.getX()*Math.sin(fovR)+dir.getY()*Math.cos(fovR)
                );
                
                Image sprite = e.getTexture();
                double dist = ePos.magnitude();
                double screenPos = screenWidth*fovLeft.angle(ePos)/fov;
                double scale = (e.getSize()*screenHeight/sprite.getHeight())/dist;
                double height = scale*sprite.getHeight();
                double width = scale*sprite.getWidth();
                
                
                boolean hidden = true; //if the entity is completely obscured by a wall
                for(int i = (int)(screenPos-(width/2.0)); i<(screenPos+(width/2.0)) && i<screenWidth; i++){
                    if(i<0){i=0;}
                    double pDist = cam.distance(hPoints.get(i));
                    double eDist = cam.distance(e.getPosition());
                    if(eDist<pDist){hidden = false;break;}
                }
                if(!hidden){
                    double relX =screenPos-(width/2.0), relY = (screenHeight-height)/2.0; //coordinates of the top left corner of the image
                    relY -= (e.getHeight()-0.5)*screenHeight/dist; //height offsett
                    relY -= (player.getHeight()-0.5);
                    gc.drawImage(sprite, relX, relY, width, height );

                    //draw walls that are in front of the entity
                    for(int i = (int)(screenPos-(width/2.0)); i<(screenPos+(width/2.0)) && i<screenWidth; i++)
                    {
                        if(i<0){i=0;}
                        double pDist = cam.distance(hPoints.get(i));
                        double eDist = cam.distance(e.getPosition());
                        if(pDist<eDist){
                            drawWallLine(i, hPoints.get(i));
                        }
                    }
                    
                }
            }
        }
        
    }

    //draws the line representing a slice of a wall
    private static void drawWallLine(int r, HitPoint hPoint){
        Point2D toWall = hPoint.subtract(player.getPosition());
        double camA = Math.toRadians(player.getRotation());
        Point2D dir = new Point2D( Math.cos(camA), Math.sin(camA) );
        
        double distance = toWall.magnitude();
        distance *= Math.cos(Math.toRadians(dir.angle(toWall)));
        
        double height = screenHeight/distance;
        
        double lineBottom = ((screenHeight-height)/2.0)+height;
        int x = r*res;
        
        if(hPoint.getType()=='f'   || distance> env.getFogFarDistance() )
        {
            gc.setFill(env.getFogColor());
            gc.fillRect(x, lineBottom, res, -height*env.getWallHeight());
        }else{
            Image texture = Game.getCurrentLevel().getCellTexture(hPoint.getXIndex(), hPoint.getYIndex());
            int tx = 0;
            switch(hPoint.getType()){
                case('h'): tx = (int)(texture.getWidth()*(1.0-hPoint.getX()%1)); break;
                case('v'): tx = (int)(texture.getWidth()*(1.0-hPoint.getY()%1)); break;
            }
            
            gc.drawImage(texture, tx, texture.getHeight(), 1, -texture.getHeight(), x, lineBottom, res, -height  *env.getWallHeight() );
        }
        
    }

    static class EntityDistanceComparator implements Comparator<SpriteEntity>{
        @Override
        public int compare(SpriteEntity o1, SpriteEntity o2) {
            Point2D cam = player.getPosition();
            double d1 = cam.distance(o1.getPosition());
            double d2 = cam.distance(o2.getPosition());
            
            if(d1<d2) return 1;
            if(d1>d2) return -1;
            return 0;
        }
    }
    
}
class HitPoint extends Point2D{
    final private int xIn, yIn;
    final private char type;
    protected HitPoint(Point2D point, char type, int xIndex, int yIndex){
        super(point.getX(), point.getY());
        this.type = type;
        this.xIn = xIndex; this.yIn = yIndex;
    }
    final static public HitPoint ZERO = new HitPoint(Point2D.ZERO, '0', -1, -1);
    
    public int getXIndex() {return xIn;}
    public int getYIndex() {return yIn;}
    public char getType() {return type;}
}
