/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
 * @author Haxx
 */
public class AddMembers extends GridPane{
    
    private Label dateLabel,nameLabel,balanceLabel;
    private DatePicker datePicker;
    private TextField nameT,balanceT;
    private String pattern="d-M-yyyy";
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("d-M-yyyy");;
    
    public AddMembers(){
        dateLabel = new Label("Date");
        nameLabel = new Label("Name");
        balanceLabel = new Label("Balance");
        nameT = new TextField();
        balanceT = new TextField();
        datePicker = new DatePicker();
        
        add(dateLabel, 0, 0);
        add(datePicker, 1, 0);
        
        add(nameLabel, 0, 1);
        add(nameT, 1, 1);
        
        add(balanceLabel, 0, 2);
        add(balanceT, 1, 2);
        
        setFieldProtocol();
        setDate();
        setAlignment(Pos.CENTER);
        setVgap(5);
        setHgap(10);
    }
    public String getDate(){ return datePicker.getValue().format(format); }
    public String getName(){ return nameT.getText().toUpperCase().replaceAll("\\s+", "_"); }
    public String getBalance(){ return balanceT.getText();}
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
    public void clearFields(){
        nameT.setText("");
        balanceT.setText("");
    }
    private void setFieldProtocol(){
//        textField only letters
//        {
//          @Override
//          public void replaceText(int start,int end,String text){
//              String oldVal = getText();
//              if(text.matches("[a-z]") && !text.matches("[\\\\!\"#$%&()*+,./:;<=>?@\\[\\]^_{|}~]+"))
//                  super.replaceText(start, end, text);
//          }
//        };
        balanceT.textProperty().addListener(new ChangeListener<String>(){
                @Override
                public void changed(ObservableValue<?extends String>observable,String oldValue,String newValue){        
                    try{
                        Integer.parseInt(newValue);
                        if(newValue.endsWith("f")|| newValue.endsWith("d")){
                            balanceT.setText(newValue.substring(0,newValue.length()-1));
                        }
                    }catch(Exception e){balanceT.setText(oldValue);}
                }
            });
    }
    public boolean checkText(){
        return !(nameT.getText().equals("")
                || balanceT.getText().equals(""));
    }
    public void setList(ObservableList list){
        TextFields.bindAutoCompletion(nameT, list);
    }
}
