/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Editor.Main;

import Editor.View.New.NewMap;
import Editor.View.New.NewProject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author A
 */
public class EditorSplashScreen {
    
    public EditorSplashScreen(Stage parent){
        Stage splashScreenStage = new Stage();
        
        splashScreenStage.initStyle(StageStyle.UNDECORATED);
        splashScreenStage.initOwner(parent);
        
        Text optikWelcome = new Text("Welcome To");
        Text optikName = new Text("Optik Editor");
        optikWelcome.setFont(Font.font("Fantasy", FontWeight.EXTRA_BOLD, 60));
        optikName.setFont(Font.font("Fantasy", FontWeight.EXTRA_BOLD, 50));
        //optikTitle.set
        
        Button newProject = setupButton("New Project");
        Button openProject = setupButton("Open Project");
        Button closeSS = setupButton("Close");
        
        Region space = new Region();
        VBox.setVgrow(space, Priority.ALWAYS);
        
        new SplashScreenController(newProject, openProject, closeSS, splashScreenStage, parent);
        
        VBox splashContent = new VBox(optikWelcome, optikName, space, newProject, openProject, closeSS);
        splashContent.setAlignment(Pos.CENTER);
        
        splashContent.setSpacing(10);
        splashContent.setPadding(new Insets(25, 25, 25, 25));
        
        Scene splashScene = new Scene(splashContent, 500, 500);
        
        splashScreenStage.setScene(splashScene);
        splashScreenStage.show();
    }
    
    private Button setupButton(String text){
        Button temp = new Button(text);
        temp.setMaxWidth(Double.MAX_VALUE);
        temp.setPrefHeight(60);
        temp.setFont(Font.font("Verdana", FontWeight.BOLD, 25));
        
        return temp;
    }
}

class SplashScreenController{

    public SplashScreenController(Button newP, Button openP, Button close, Stage current, Stage parent) {
        newP.setOnAction(e -> {new NewProject(parent);});
        
        openP.setOnAction(e -> {});
        
        close.setOnAction(e -> {current.close();});
        
        current.focusedProperty().addListener(((observable, pastFoc, isFoc) -> {
            if(!isFoc)
                current.close();
        }));
    }
}