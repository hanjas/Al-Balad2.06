/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package al.balad2;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author BCz
 */
public class AlBalad2 extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AL-BALAD");
        try{
            Pane myPane = (Pane)FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
            Scene myScene = new Scene(myPane,800,400);
            primaryStage.setScene(myScene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            primaryStage.setOnCloseRequest(e->{
                System.exit(0);
            });
        }catch (Exception e) { System.out.println(e); }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
