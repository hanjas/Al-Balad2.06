/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.CheckListView;

/**
 *
 * @author BCz
 */
public class RemoveMembers extends GridPane{
    
    CheckListView listView;
    CheckBox checkBox;
    int i=0;
            
    public RemoveMembers(){
        listView= new CheckListView();
        checkBox = new CheckBox("Select All");
        
        add(checkBox, 0, 0);
        add(listView, 0, 1);
        
        setVgap(10);
        setAlignment(Pos.CENTER);
        setAction();
    }
     public void setListItems(ObservableList list){
         listView.setItems(list);
     }
     public ObservableList getSelectedItems(){
         if(checkBox.isSelected()){
             return listView.getItems();
         } else {
            return listView.getCheckModel().getCheckedItems();
         }
     }
     public boolean checkList(){
         return listView.getSelectionModel().getSelectedItems().isEmpty();
     }
     public void clearItems(){
         listView.getItems().clear();
         this.getChildren().clear();
     }
     private void setAction(){
         checkBox.selectedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if(i==0){
                        listView.getCheckModel().checkAll();
                        listView.setDisable(true);
                        i=1;
                    } else if (i==1){
                        listView.setDisable(false);
                        listView.getCheckModel().clearChecks();
                        i=0;
                    }
                } 
         });
     }
}
