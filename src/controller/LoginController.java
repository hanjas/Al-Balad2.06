package controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author BCz
 */
public class LoginController implements Initializable {

    @FXML TextField usrName,pwd;
    @FXML BorderPane mainPane;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    @FXML
    private void check(ActionEvent e){
        Stage stage;
        Parent root;
        try{
//            if(usrName.getText().equals("admin")){
//                if(pwd.getText().equals("admin")){
                    stage = (Stage)usrName.getScene().getWindow();
                    root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setMaximized(true);
                    stage.show();
//                    mainPane.getChildren().clear();
//                    mainPane.setCenter((Pane)FXMLLoader.load(getClass().getResource("/views/Main_View.fxml")) );
//                }
//            }
            usrName.setText("");
        }catch(Exception ex){System.out.println(ex);}
    }
    
}
