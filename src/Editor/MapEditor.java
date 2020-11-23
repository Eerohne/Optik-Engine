/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor;

import Editor.View.Grid.Grid;
import Editor.Controller.GridController;
import Editor.Controller.InfoController;
import Editor.Controller.MenuController;
import Editor.Controller.ShortcutController;
import Editor.View.Info;
import Editor.View.Menu.ShortcutBar;
import Editor.View.Menu.TopMenu;
import Editor.View.Properties.EntityTab;
import Editor.View.Properties.WallTab;
import java.io.File;
import java.net.MalformedURLException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author A
 */
public class MapEditor extends Application {
    
    @Override
    public void start(Stage editorWindow) throws MalformedURLException {
        TopMenu menu = new TopMenu();
        ShortcutBar shortcuts = new ShortcutBar();
        BorderPane tools = new BorderPane(shortcuts, menu, null, null, null);
        
        Info info = new Info();
        Grid gridRender = new Grid(100, 20, 20);
        setChildrenClipping(gridRender);
        
        
        //Create Entity Tab
        EntityTab entityTab = new EntityTab();
        Tab entities = new Tab("Entities", entityTab);
        
        //Create Wall Tab
        WallTab wallTab = new WallTab();
        Tab walls = new Tab("Walls", wallTab);
        
        TabPane properties = new TabPane(walls, entities);
        properties.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        BorderPane gridDisplay = new BorderPane();
        gridDisplay.setCenter(gridRender);
        gridDisplay.setBottom(info);
        
        BorderPane layout = new BorderPane();
        layout.setCenter(gridDisplay);
        layout.setTop(tools);
        layout.setLeft(properties);
        
        
        Scene scene = new Scene(layout, 1920, 1080);
        
        InfoController ic = new InfoController(info);
        GridController gc = new GridController(scene, gridRender, new Button(), new ColorPicker());
        MenuController mc = new MenuController(menu, editorWindow);
        ShortcutController sc = new ShortcutController(shortcuts, editorWindow);
        
        String pathName = "resources/style/style.css" ;
        File file = new File(pathName);
        if (file.exists()) {
            scene.getStylesheets().add(file.toURI().toURL().toExternalForm());
        } else {
           System.out.println("Could not find css file: "+pathName);
        }
        
        editorWindow.setTitle("Optik Editor");
        editorWindow.setScene(scene);
        editorWindow.setMaximized(true);
        editorWindow.show();
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private static void setChildrenClipping(Pane pane) {
        Rectangle clip = new Rectangle();
        pane.setClip(clip);    
        
        pane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });
    }
}
