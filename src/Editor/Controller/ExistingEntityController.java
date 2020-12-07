/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.Controller;
import Editor.Model.EntityModel;
import Editor.Model.SignalModel;
import Editor.View.New.NewEntityStage;
import Editor.View.Menu.Entity.ExistingEntityModification;
import Editor.View.Menu.Entity.SignalEditorStage;
import Editor.View.Menu.Entity.SignalEditorView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author linuo
 */
public class ExistingEntityController{
    EntityModel model = new EntityModel();
    ExistingEntityModification view = new ExistingEntityModification();
    ObservableList<EntityModel> list = FXCollections.observableArrayList();
    ObservableList<EntityModel> templist = FXCollections.observableArrayList();
    ObservableList selectedCells = FXCollections.observableArrayList();
    Gson gson = new GsonBuilder().setPrettyPrinting().create(); 

    public ExistingEntityController(EntityModel model, ExistingEntityModification view) throws IOException, FileNotFoundException, ParseException {
        this.model = model;
        this.view = view;
        
        try {
            allEntities();
        } catch (ParseException ex) {
            Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        launchSignalEditor();
        comboBoxHandler();
        editEntity();
        removeEntity();
        deleteRow();
        switchWindow();
        addRow();   
        //editName();
    }

    // getting all entities from the json file and display their name in the combobox
    private void allEntities() throws ParseException{
        JSONParser parser = new JSONParser();
        
        try(FileReader reader = new FileReader("entities.json")){
            
            JSONObject allEntity = (JSONObject) parser.parse(reader);
            JSONArray entitiesArray = (JSONArray) allEntity.get("entities");
            for(int i = 0; i < entitiesArray.size(); i++){
                JSONObject entity = new JSONObject();
                entity = (JSONObject) entitiesArray.get(i);
                String str = (String) entity.get("name");
                view.cb.getItems().add(str);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void launchSignalEditor(){
        view.signalBtn.setOnAction((event) -> {
            try {
                Stage stage = new Stage();
                new SignalEditorStage(stage);
            } catch (IOException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // display the selected entity's properties to the table 
    private void comboBoxHandler(){
        view.cb.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                JSONParser parser = new JSONParser();
                try(FileReader reader = new FileReader("entities.json")){
                    
                    JSONObject allEntity = (JSONObject) parser.parse(reader);
                    JSONArray entitiesArray = (JSONArray) allEntity.get("entities");
                    
                    for(int i = 0; i < entitiesArray.size(); i++){
                        JSONObject entity = (JSONObject) entitiesArray.get(i);
                        String str = (String) entity.get("name");
                        list.clear();
                        
                        if(newValue.equals(str)){
                            ObservableList<String> keylist = FXCollections.observableArrayList(entity.keySet());
                            ObservableList<String> valuelist = FXCollections.observableArrayList(entity.values());
                            view.classNameTf.setText((String) entity.get("classname"));
                            view.nameTf.setText((String) entity.get("name"));
                            for(int j = 0; j < entity.keySet().size(); j++){
                                if((valuelist.get(j) instanceof String) && !keylist.get(j).equals("classname") && !keylist.get(j).equals("name")){
                                    list.add(new EntityModel(keylist.get(j), valuelist.get(j)));
                                    view.table.getItems().setAll(list);
                                } else {
                                    continue;
                                }
                            }      
                        }
                    }
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException | ParseException ex) {
                    Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private void editEntity() throws FileNotFoundException, IOException, ParseException{  
        view.saveEdit.setOnAction((event) -> {
            FileReader reader = null;
            try {
                EntityModel model = new EntityModel(view.propertyText.getText(), view.valueText.getText());
                int selectedIndex = view.table.getSelectionModel().getSelectedIndex();
                JSONParser parser = new JSONParser();
                reader = new FileReader("entities.json");
                JSONObject allEntity = (JSONObject) parser.parse(reader);
                JSONArray entitiesArray = (JSONArray) allEntity.get("entities");
                String name = view.cb.getValue();
                int index = view.cb.getItems().indexOf(name);
                
                if(selectedIndex >= 0){
                    
                    EntityModel tempModel = new EntityModel();
                    for(int i = 0; i < entitiesArray.size(); i++){
                        JSONObject entity = (JSONObject) entitiesArray.get(i);
                        if(entity.get("name").equals(name)){
                            
                            entity.put("name", view.nameTf.getText());
                            entity.put("classname", view.classNameTf.getText());
                            tempModel = (EntityModel) view.table.getItems().get(selectedIndex);
                            
                            entity.remove(tempModel.getProperty());
                            entity.put(view.propertyText.getText(), view.valueText.getText());
                            entitiesArray.set(index, entity);
                            allEntity.put("entities", entitiesArray);

                            FileWriter writer = new FileWriter("entities.json");
                            gson.toJson(allEntity, writer);
                            writer.close();
                            
                        }
                        
                    }
                    view.table.getItems().set(selectedIndex, model);
                }
                else if (view.table.getSelectionModel().isEmpty()){
                    for(int i = 0; i < entitiesArray.size(); i++){
                        JSONObject entity = (JSONObject) entitiesArray.get(i);
                        if(entity.get("name").equals(name)){
                            entity.put("name", view.nameTf.getText());
                            entity.put("classname", view.classNameTf.getText());
                            entitiesArray.set(index, entity);
                            allEntity.put("entities", entitiesArray);

                            FileWriter writer = new FileWriter("entities.json");
                            gson.toJson(allEntity, writer);
                            writer.close();
                            
                        }
                        
                    }
                            
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
          
            });
    }
    
    private void removeEntity() throws FileNotFoundException, IOException, ParseException{
        view.removeEntityBtn.setOnAction((event) -> {
            FileReader reader = null;
            try {
                JSONParser parser = new JSONParser();
                reader = new FileReader("entities.json");
                JSONObject allEntities = (JSONObject) parser.parse(reader);
                JSONArray entitiesArray = (JSONArray) allEntities.get("entities");
                String name = view.cb.getValue();
                int index = view.cb.getItems().indexOf(name);
                entitiesArray.remove(index);
                allEntities.put("entities", entitiesArray);
                FileWriter writer = new FileWriter("entities.json");
                gson.toJson(allEntities, writer);
                writer.close();
                view.table.getItems().clear();
                view.cb.getItems().remove(index);
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ParseException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            });
    }
    
    private void deleteRow() throws FileNotFoundException, IOException, ParseException{ 
       view.deleteBtn.setOnAction((event) -> {
           FileReader reader = null;
           try {
               JSONParser parser = new JSONParser();
               reader = new FileReader("entities.json");
               JSONObject allentities = (JSONObject) parser.parse(reader);
               JSONArray entitiesArray = (JSONArray) allentities.get("entities");
               String name = view.cb.getValue();
               
               int selectedIndex = view.table.getSelectionModel().getSelectedIndex();
               int index = view.cb.getItems().indexOf(name);
               
               if(selectedIndex >= 0){
                   
                   EntityModel tempModel = (EntityModel) view.table.getItems().get(selectedIndex);
                   view.table.getItems().get(selectedIndex);
                   
                   JSONObject entity = (JSONObject) entitiesArray.get(index);                   
                   entity.remove(tempModel.getProperty());
                   
                   entitiesArray.set(index, entity);
                   allentities.put("entities", entitiesArray);
                   
                   FileWriter writer = new FileWriter("entities.json");
                   gson.toJson(allentities, writer);
                   writer.close();
                   
                   view.table.getItems().remove(selectedIndex);
                   
               }
               
            } catch (FileNotFoundException ex) {
               Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
           } catch (IOException ex) {
               Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
           } catch (ParseException ex) {
               Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
           }
           
        });
    }
    
    private void addRow(){ 
        view.addBtn.setOnAction((event) -> {
            FileReader reader = null;
            try {
                String property = view.propertyText.getText();
                String value = view.valueText.getText();
                JSONParser parser = new JSONParser();
                reader = new FileReader("entities.json");
                JSONObject allEntities = (JSONObject) parser.parse(reader);
                JSONArray entitiesArray = (JSONArray) allEntities.get("entities");
                String name = view.cb.getValue();
                String key = name.substring(1, name.length()-1);
                int selectedIndex = view.table.getSelectionModel().getSelectedIndex();
                int index = view.cb.getItems().indexOf(name);
                
                //getting the json object that will be added a property
                JSONObject entity = (JSONObject) entitiesArray.get(index);
                //JSONObject currentEntity = (JSONObject) entity.get(key);
                //JSONObject updatedEntity = new JSONObject();
                entity.put(property, value);
                
                entitiesArray.set(index, entity);
                allEntities.put("entities", entitiesArray);
                
                FileWriter writer = new FileWriter("entities.json");
                gson.toJson(allEntities, writer);
                writer.close();
                
                //display the newly added property and its value to table
                List keyList = new ArrayList(entity.keySet());
                List values = new ArrayList(entity.values());
                
                list.clear();
                for(int i = 0; i < keyList.size(); i++){
                    if(!keyList.get(i).equals("signal") && !keyList.get(i).equals("name") && !keyList.get(i).equals("classname")){
                        ObservableList<String> propertyList = FXCollections.observableArrayList();
                        propertyList.add(keyList.get(i).toString());
                        ObservableList<String> valueList = FXCollections.observableArrayList();
                        valueList.add(values.get(i).toString()); 
                        
                        for(int j = 0; j < propertyList.size(); j++){
                            list.add(new EntityModel(propertyList.get(j), valueList.get(j)));
                        }
                    }
                }
                view.table.getItems().setAll(list);
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(ExistingEntityController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    private void switchWindow(){
        view.switchBtn.setOnAction((event) -> {
           Stage stage = new Stage();
           new NewEntityStage(stage);
        });
    }
}
