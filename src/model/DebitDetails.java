/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;

/**
 *
 * @author BCz
 */
public class DebitDetails extends GridPane{
    
    private final Label name,date,amount;
    private final TextField amountT,nameT;
    private final DatePicker datePicker;
    private final String pattern="d-M-yyyy";
    private final DateTimeFormatter format;
    
    public DebitDetails(){
        this.format = DateTimeFormatter.ofPattern("d-M-yyyy");
        date = new Label("Date");
        name = new Label("Name");
        amount = new Label("Amount");
        
        nameT = new TextField();
        datePicker = new DatePicker();
        amountT = new TextField();
        
        amountT.setFont(Font.font(25));
        
        add(date, 0, 0);
        add(datePicker, 1, 0);
        
        add(name, 0, 1);
        add(nameT, 1, 1);
        
        add(amount, 0, 2);
        add(amountT, 1, 2);
        
        setDate();
        
        nameT.setEditable(true);
        nameT.setPrefWidth(175);
        setAlignment(Pos.CENTER);
        setVgap(5);
        setHgap(10);
        Platform.runLater(new Runnable() {
            @Override public void run() { nameT.requestFocus(); }
        });
    }
    
    public String getDate(){ return datePicker.getValue().format(format); }
    public String getName(){ return nameT.getText(); }
    public String getAmount(){ return amountT.getText(); }
    public void setFocus() {
        Platform.runLater(new Runnable() {
            @Override public void run() { nameT.requestFocus(); }
        });
    }
    public void clearFields(){
        nameT.setText("");
        amountT.clear();
    }
    public boolean checkText(){
        return !(nameT.getText().equals("") || amountT.getText().equals(""));
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
        TextFields.bindAutoCompletion(nameT, list);
    }
}
