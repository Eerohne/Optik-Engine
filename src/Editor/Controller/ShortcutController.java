/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.Controller;

import Editor.Main.MapEditor;
import Editor.Model.Profile.WallProfile;
import Editor.View.New.NewWallProfile;
import Editor.View.Help.HelpView;
import Editor.View.Menu.ShortcutBar;
import Editor.View.Hierarchy.WallHierarchy;
import Editor.View.New.NewEntityStage;
import Editor.View.New.NewMap;
import Engine.Core.Game;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

/**
 *
 * @author A
 */
public class ShortcutController{

    public ShortcutController(ShortcutBar shortcutBar, Stage owner, WallHierarchy wallList) {
        //Set the Help Page Shortcut Event
        shortcutBar.getHelp().setOnAction(e -> {
            new HelpView(owner);
        });
       
        //Set the Run Shortcut Event
        shortcutBar.getRunShort().setOnAction(e -> {
            try {
                MenuController.save();
                Stage engine = new Stage();
                Game game = new Game();
                engine.initOwner(owner);
                game.editorModeStart(engine, "levels/" + MapEditor.getProject().getSelectedMap().getName() + ".lvl");
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Engine Error");
                alert.setContentText("Engine Not Executed");
                alert.showAndWait();
            }
        });
        
        //Set the New Wall Shortcut Event
        shortcutBar.getWallShort().setOnAction(e -> {
            new NewWallProfile(owner, MapEditor.getProject().getImageFolder(), wallList);
        });
        
        //Set the New Entity Shortcut Event
        shortcutBar.getEntityShort().setOnAction(e -> {
            try {
                new NewEntityStage(owner);
            } catch (MalformedURLException ex) {
                Logger.getLogger(ShortcutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        //Set the New Map Shortcut Event
        shortcutBar.getMapShort().setOnAction(e -> {
            new NewMap(owner);
        });
        
        //Set the Save Map Shortcut Event
        shortcutBar.getSaveShort().setOnAction(e -> {
            try {
                MenuController.save();
            } catch (ParseException ex) {
                Logger.getLogger(ShortcutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
}
