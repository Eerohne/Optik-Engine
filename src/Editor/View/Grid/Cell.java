/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.View.Grid;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 *
 * @author A
 */
public class Cell extends Rectangle{
    private int size;
    private int scaleSize;
    private Color cellColor;
    private Color selectColor;
    private Color floorColor = Color.WHITE;
    private Translate tVector = new Translate(0, 0);
    private Scale sMatrix = new Scale(1, 1);
    
    public Cell(int defaultSize) {
        super(defaultSize, defaultSize);
        super.setFill(floorColor);
        super.setStroke(Color.BLACK);
        
        this.size = defaultSize;
        this.scaleSize = size;
        this.cellColor = this.floorColor;
        this.selectColor = this.cellColor.darker();
        this.getTransforms().addAll(tVector, sMatrix);
    }

    public void clear(){
        this.setColor(cellColor);
    }
    
    public int getDefaultSize() {
        return size;
    }

    public void setDefaultSize(int size) {
        this.size = size;
        super.setWidth(this.size);
        super.setHeight(this.size);
    }

    public Color getCellColor() {
        return cellColor;
    }

    public void setColor(Color color){
        super.setFill(color);
        this.cellColor = color;
        if (this.cellColor.equals(Color.BLACK)) {
            this.selectColor = color.brighter();
        } else this.selectColor = color.darker();
    }
    
    public void isSelected(boolean isSelected) {
        if (isSelected) {
            this.setFill(selectColor);
        } else{
             super.setFill(cellColor);
        }
       
    }
    
    public void setXPos(double value){
        super.setX(value * size);
    }
    
    public void setYPos(double value){
        super.setY(value * size);
    }
    
    public Translate getTranslationVector(){
        return tVector;
    }
    
    public Scale getScaleObject(){
        return sMatrix;
    }
    
    public void addTranslationVector(Translate vector){
        double x = vector.getX() + tVector.getX();
        double y = vector.getY() + tVector.getY();
        
        this.tVector.setX(x);
        this.tVector.setY(y);
    }
    
    public void addScaleMatrix(Scale matrix){
        double x = matrix.getX();
        double y = matrix.getY();

        
        this.setHeight(scaleSize);
        this.setWidth(scaleSize);
        
        this.sMatrix.setX(x);
        this.sMatrix.setY(y);
    }
}
