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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


/**
 *
 * @author linuo
 */

public class NewEntity extends GridPane{
    
    public TableView table = new TableView();
    public TableColumn<EntityModel, String> propertyCol = new TableColumn<>("property");
    public TableColumn<EntityModel, String> valueCol = new TableColumn<>("value");
    public TextField nameText = new TextField();
    public TextField propertyText = new TextField();
    public TextField valueText = new TextField();
    public Button addBtn = new Button("add row");
    public Button deleteBtn = new Button("delete selected row");
    public Button exportBtn = new Button("export");
    public Button newEntityBtn = new Button("create new entity");
    public Button switchBtn = new Button("open entity editting window");
    public ComboBox<String> cb = new ComboBox();
    public VBox vbox = new VBox();
    
    
    public NewEntity(){
        
        table.setEditable(true);
        this.setHgap(10);
        this.setVgap(10);
        this.setPadding(new Insets(10));
        //this.add(cb, 0, 0);
        this.add(table, 1, 0);
        
        vbox.getChildren().add(nameText);
        vbox.getChildren().add(propertyText);
        vbox.getChildren().add(valueText);
        vbox.getChildren().add(addBtn);
        vbox.getChildren().add(deleteBtn);
        vbox.getChildren().add(exportBtn);
        vbox.getChildren().add(newEntityBtn);
        vbox.getChildren().add(switchBtn);
        
        this.add(vbox, 2, 0);
        propertyCol.setCellValueFactory(new PropertyValueFactory<EntityModel, String>("property"));
        valueCol.setCellValueFactory(new PropertyValueFactory<EntityModel, String>("value"));
        table.getColumns().addAll(propertyCol, valueCol);
        nameText.setPromptText("name of the entity");
        propertyText.setPromptText("property here");
        valueText.setPromptText("value here");
        
    }
   
}