/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.View.New;

import Editor.Model.EntityModel;
import Editor.View.Menu.Entity.NewEntity;
import Editor.Controller.NewEntityController;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author linuo
 */
public class NewEntityStage{
    
    public NewEntityStage(Stage parent)  {
        Stage newEntity = new Stage();
        newEntity.initOwner(parent);
        newEntity.initModality(Modality.WINDOW_MODAL);
        NewEntity view = new NewEntity();
        NewEntityController nec = new NewEntityController(new EntityModel(), view);
        nec.setData();
        Scene scene = new Scene(view, 500, 500);
        newEntity.setTitle("Add New Entity");
        newEntity.setScene(scene);
        newEntity.show();
    }

    
    
    
    
}
