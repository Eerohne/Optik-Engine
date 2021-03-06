package Editor.Controller;

import Editor.Main.MapEditor;
import Editor.Model.Profile.EntityProfile;
import Editor.Model.Profile.MapProfile;
import Editor.Model.Profile.Profile;
import Editor.Model.Profile.WallProfile;
import Editor.View.Grid.Cell;
import Editor.View.Grid.EntityDot;
import Editor.View.Grid.Grid;
import Editor.View.Info;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author A
 */
public class GridController{
    //Objects to be controlled
    Grid grid;
    
    //EntityDot ed = new EntityDot(Color.CYAN);

    
    private Profile selectedProfile;

    //Grid Panning Vector Variables
    double preMouseX;
    double preMouseY; 
    double mouseX;
    double mouseY;
    
    private int editingMode = 1; // 0: Editing Disabled | 1: Wall Placement | 2: Entity Placement
    
    double zoom = 1.0d;

    String hoverEntity;
    
    Cell hoverCell = new Cell(1);
    EntityDot dot = new EntityDot(Color.CYAN, 0, 0, 1);
    Info info = new Info();
    
    double dotX = 0;
    double dotY = 0;
    
    public GridController(Grid grid) {
        //this.scene = scene;
        this.grid = grid;
        
        
        //Grid Events
        
        //Zoom Event
        grid.setOnScroll(event -> {
            double scaleShift = 0;
            if(zoom > 1.99) {
                if (event.getDeltaY() < 0) {
                    scaleShift -= 0.05;
                    scale(scaleShift);
                }
            }
            else if(zoom < 0.05) {
                if (event.getDeltaY() > 0) {
                    scaleShift += 0.05;
                    scale(scaleShift);
                }
            }
            else{
                if (event.getDeltaY() < 0) {
                    scaleShift -= 0.05;
                } else{
                    scaleShift += 0.05;
                }
                
                scale(scaleShift);
            }
        });
        
        //Hover Event
        for (Cell[] cells : grid.getCells()) {
            for (Cell cell : cells) {
                cell.setOnMouseEntered(event -> {
                    onHover(cell);
                });
            }
        }
        
        //Update Mouse Position on Mouse Moved
        grid.setOnMouseMoved(event -> {
            updateMousePos(event);
        });
        
        //Place Wall on Mouse Pressed
        grid.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                switch(editingMode){
                    case 1:
                        placeWall();
                        break;
                    case 2:
                        placeEntity();
                        break;
                    default:
                        break;
                }
            }
        });
        
        //Wall Placement when Mouse Dragged
        grid.setOnMouseDragged(new GridEvent());
    }
    
    class GridEvent implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            updateMousePos(event);
            
            //Grid Translation Implementation
            if(event.getButton().equals(MouseButton.MIDDLE)){
                //Translation vecxtor
                Translate vector = new Translate((mouseX - preMouseX), (mouseY - preMouseY));
                
                //Every cell is translating with the vector above
                for (Cell[] cells : grid.getCells()) {
                    for (Cell cell : cells) {
                        cell.addTranslationVector(vector);
                    }
                }
                
                
                dot.addTranslationVector(vector);
                dotX += (mouseX - preMouseX);
                dotY += (mouseY - preMouseY);
                
                grid.getSelectionCell().addTranslationVector(vector);
            }
            
            //
            if(event.getButton().equals(MouseButton.PRIMARY)){
                if(editingMode == 1)
                    placeWall();
            }  
        }
    }
    
    private void updateMousePos(MouseEvent event){
        preMouseX = mouseX;
        preMouseY = mouseY;
        this.mouseX = event.getX();
        this.mouseY = event.getY();
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }

    public double getZoom() {
        return zoom;
    }
    
    public int getEditingMode(){
        return editingMode;
    }

    public String getHoverEntity() {
        return hoverEntity;
    }
    
    public void setEditingMode(int mode){
        this.editingMode = mode;
    }
    
    public WallProfile getSelectedWallProfile() {
        return (WallProfile)selectedProfile;
    }

    public void setSelectedWallProfile(WallProfile selectedWallProfile) {
        this.selectedProfile = selectedWallProfile;
    }
    
    public EntityProfile getSelectedEntityProfile() {
        return (EntityProfile)selectedProfile;
    }

    public void setSelectedEntityProfile(EntityProfile selectedEntityProfile) {
        this.selectedProfile = selectedEntityProfile;
    }
    
    private double getGlobalX(){
        return mouseX + getPaneBounds().getMinX();
    } 
    
    private double getGlobalY(){
        return mouseY + getPaneBounds().getMinY();
    }
    
    private Bounds getPaneBounds(){
        return grid.getCells()[0][0].getParent().localToScene(grid.getCells()[0][0].getParent().getBoundsInLocal());
    } 
    
    private Bounds getGridBounds(){
        return grid.getCells()[0][0].localToScene(grid.getCells()[0][0].getBoundsInLocal());
    } 
    
    public double getGridX(){
        return (this.getGlobalX() - getGridBounds().getMinX())/grid.getCellSize();
    }
    
    public double getGridY(){
        return (this.getGlobalY() - getGridBounds().getMinY())/grid.getCellSize();
    }
    
    private void placeWall(){
        try {
            if(!(mouseX < 0 || mouseY < 0 || mouseX > getPaneBounds().getMaxX() || mouseY > getPaneBounds().getMaxY())){
                Cell c = this.grid.getCells()[(int)getGridX()][(int)getGridY()];
                this.setImg(c, getSelectedWallProfile().getImage());
                onHover(c);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void placeEntity(){
        try {
            if(!(mouseX < 0 || mouseY < 0 || mouseX > getPaneBounds().getMaxX() || mouseY > getPaneBounds().getMaxY())){
                EntityProfile ep = getSelectedEntityProfile();
                EntityDot ed = ep.getDot();
                ed.initialize((mouseX - dotX)/dot.getScaleObject().getX(), (mouseY - dotY)/dot.getScaleObject().getY(), 10);
                ed.setOnMouseEntered(e -> {
                    this.hoverEntity = ep.getName();
                });
                ed.setOnMouseExited(e -> {
                    this.hoverEntity = "null";
                });
                
                savePosition(getSelectedEntityProfile().getName(), getGridX(), getGridY());
            }
        } catch(Exception e){
            System.out.println("Entity : " + e);
        }
    }
    
    private void onHover(Cell hoverCell){
        this.hoverCell = hoverCell;
        this.highlight();
    }
    
    private void highlight(){
        grid.getSelectionCell().setX(hoverCell.getX());
        grid.getSelectionCell().setY(hoverCell.getY());
    }
    
    private void scale(double scaleFactor){
        Scale scale = new Scale(scaleFactor, scaleFactor);
        
        grid.setCellSize(scaleFactor);

        for (Cell[] cells : grid.getCells()) {
            for (Cell cell : cells) {
                cell.addScaleMatrix(scale);
            }
        }
        
        this.dot.addScaleMatrix(scale);
       
        grid.setEntityDotSize(10 * zoom);
        grid.getSelectionCell().addScaleMatrix(scale);
        
        grid.setCellSize(grid.getCells()[0][0].getDefaultSize() * grid.getCells()[0][0].getScaleObject().getX());
        zoom = this.getScaleRatio();
    }
    
    private double getScaleRatio(){
        return grid.getCells()[0][0].getScaleObject().getX();
    }
    
    public void setImg(Cell cell, Image img){
        WallProfile wp = (WallProfile)selectedProfile;
        int wallId = wp.getID();
        this.setImg(cell, wallId);
    }
    
    public void setImg(Cell cell, int paletteID){
        cell.setID(paletteID);
        cell.setTexture(new ImagePattern(getSelectedWallProfile().getImage()));
    }
    
    public void setImg(Cell cell, int paletteID, MapProfile map){
        cell.setID(paletteID);
        cell.setTexture(new ImagePattern(map.getWallMap().get(paletteID).getImage()));
    }
    
     public void loadPalette(int[][] palette, MapProfile map){
        try{
            for (int j = 0; j < grid.getyLength(); j++) {
                for(int i = 0; i < grid.getxLength(); i++){
                    this.setImg(grid.getCells()[i][j], palette[i][j], map);
                }
            }
        }catch (ArrayIndexOutOfBoundsException ae){
            System.out.println(map.getName() + ": " + ae);
        }
    }
    
    public void setupDot(EntityProfile ep){
        EntityDot ed = ep.getDot();
        ed.initialize(0, 0, 1);
        ed.setScaleObject(dot.getScaleObject());
        ed.setTranslationObject(dot.getTranslationObject());

        ed.setOnMouseEntered(e -> {
            this.hoverEntity = ep.getName();
        });
        ed.setOnMouseExited(e -> {
            this.hoverEntity = "null";
        });
        
        grid.getEntities().add(ed);
        grid.getChildren().add(ed);
    }
    
    private void savePosition(String name, double x, double y){
        
        FileReader reader = null;
        try {
            JSONParser parser = new JSONParser();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            reader = new FileReader(MapEditor.getProject().getSelectedMapPath());
            JSONObject savefile = (JSONObject) parser.parse(reader);
            JSONArray entities = (JSONArray) savefile.get("entities");
            JSONObject currentEntity = new JSONObject();
            JSONArray position = new JSONArray();
            position.add(x);
            position.add(y);
            
            for(int i = 0; i < entities.size(); i++){
                currentEntity = (JSONObject) entities.get(i);
                if(currentEntity.get("name").equals(name)){
                    currentEntity.put("position", position);
                    entities.set(i, currentEntity);
                }
            }
            
            savefile.put("entities", entities);
            
            FileWriter writer = new FileWriter(MapEditor.getProject().getSelectedMapPath());
            gson.toJson(savefile, writer);
            writer.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}


