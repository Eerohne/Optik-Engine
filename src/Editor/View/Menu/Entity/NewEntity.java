/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.View.Menu.Entity;

import Editor.Model.EntityModel;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 *
 * @author linuo
 */

public class NewEntity extends GridPane{
    
    public TableView table = new TableView();
    
    public TableColumn<EntityModel, String> propertyCol = new TableColumn<>("property");
    public TableColumn<EntityModel, String> valueCol = new TableColumn<>("value");
    
    public Label label = new Label("please add other properties here");
    public TextField propertyText = new TextField();
    public TextField valueText = new TextField();
    public Button addBtn = new Button("add row");
    public Button deleteBtn = new Button("delete row");
    public Button exportBtn = new Button("create entity");
    public Button switchBtn = new Button("entity editor");
    public Button signalBtn = new Button("create signal");
    public Button viewSignal = new Button("view signals");
    public Button close = new Button("close window");
    public ComboBox<String> cb = new ComboBox();
    public VBox vbox = new VBox();
    
    public VBox labelBox = new VBox();
    public Label classNameLabel = new Label("class name:");
    public Label name = new Label("name:"); 
    
    
    public VBox tfBox = new VBox();
    public TextField classNameTf = new TextField();
    public TextField nameTf = new TextField();
    
    public VBox tableBox = new VBox();
    
    public NewEntity(){
        
        table.setEditable(true);
        this.setHgap(10);
        this.setVgap(10);
        tableBox.getChildren().add(table);
        
        propertyCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        valueCol.prefWidthProperty().bind(table.widthProperty().multiply(0.5));
        
        this.add(tableBox, 1, 1);
        this.add(labelBox, 1, 0);
        this.add(tfBox, 2, 0);
        
        labelBox.getChildren().addAll(classNameLabel, name);
        labelBox.setPadding(new Insets(16));
        labelBox.setSpacing(16);
        
        tfBox.getChildren().addAll(classNameTf, nameTf);
        tfBox.setPadding(new Insets(10));
        tfBox.setSpacing(10);
        
        addBtn.setMaxWidth(Double.MAX_VALUE);
        deleteBtn.setMaxWidth(Double.MAX_VALUE);
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        switchBtn.setMaxWidth(Double.MAX_VALUE);
        signalBtn.setMaxWidth(Double.MAX_VALUE);
        viewSignal.setMaxWidth(Double.MAX_VALUE);
        
        
        vbox.setPadding(new Insets(5));
        vbox.setSpacing(5);
        
        vbox.getChildren().add(label);
        vbox.getChildren().add(propertyText);
        vbox.getChildren().add(valueText);
        vbox.getChildren().add(addBtn);
        vbox.getChildren().add(deleteBtn);
        vbox.getChildren().add(signalBtn);
        vbox.getChildren().add(exportBtn);
        vbox.getChildren().add(switchBtn);
        vbox.getChildren().add(viewSignal);
        
        this.add(vbox, 2, 1);
        propertyCol.setCellValueFactory(new PropertyValueFactory<EntityModel, String>("property"));
        valueCol.setCellValueFactory(new PropertyValueFactory<EntityModel, String>("value"));
        
        table.getColumns().addAll(propertyCol, valueCol);
        
        propertyText.setPromptText("property here");
        valueText.setPromptText("value here");
        
    }
   
}
