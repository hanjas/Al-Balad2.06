/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author USER
 */
public class RemoveFish extends GridPane {
    
    private final Label name,date,party,fish;
    private final TextField partyT,fishT;
    private final TextField  nameT;
    private final DatePicker datePicker;
    private final String pattern="d-M-yyyy";
    private final DateTimeFormatter format;
    
    public RemoveFish(){
        
        this.format = DateTimeFormatter.ofPattern("d-M-yyyy");
        date = new Label("Date");
        name = new Label("Name");
        party = new Label("Party");
        fish = new Label("Fish");
        
        nameT = new TextField();
        datePicker = new DatePicker();
        partyT = new TextField();
        fishT = new TextField();
        
        add(date, 0, 0);
        add(datePicker, 1, 0);
        
        add(name, 0, 1);
        add(nameT, 1, 1);
        
        add(party, 0, 2);
        add(partyT, 1, 2);
        
        add(fish, 0, 3);
        add(fishT, 1, 3);
        
        nameT.setEditable(true);
        nameT.setPrefWidth(175);
        setAlignment(Pos.CENTER);
        setVgap(5);
        setHgap(10);
        setDate();
    }
    
    public String getDate(){ return datePicker.getValue().format(format); }
    public String getName(){ return nameT.getText(); }
    public String getParty(){ return partyT.getText(); }
    public String getFish(){ return fishT.getText(); }
    
    public boolean checkText(){
        return !(nameT.getText().equals("")
                || partyT.getText().equals("")
                || fishT.getText().equals(""));
    }
    
    private void setDate(){
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override 
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override 
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        datePicker.setValue(LocalDate.now());
    }
    public void setList(ObservableList list){
//       nameT.setItems(list);
        TextFields.bindAutoCompletion(nameT, list);
    }
}
