package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

public class FishDetails extends GridPane{
    
    private final Label name,date,party,qty,fish,rate;
    private final TextField partyT,qtyT,fishT,rateT,discount;
    private final TextField  nameT;
    private final DatePicker datePicker;
    private final String pattern="d-M-yyyy";
    private final DateTimeFormatter format;
    
    public FishDetails(){
        this.format = DateTimeFormatter.ofPattern("d-M-yyyy");
        date = new Label("Date");
        name = new Label("Name");
        party = new Label("Party");
        fish = new Label("Fish");
        qty = new Label("Qty");
        rate = new Label("Rate");
        
        nameT = new TextField();
        datePicker = new DatePicker();
        partyT = new TextField();
        fishT = new TextField();
        qtyT = new TextField();
        rateT = new TextField();
        discount = new TextField();
        discount.setPrefWidth(70);
        discount.setPromptText("discount");
        
        rateT.setFont(Font.font(25));
        
        add(date, 0, 0);
        add(datePicker, 1, 0);
        
        add(name, 0, 1);
        add(nameT, 1, 1);
        
        add(party, 0, 2);
        add(partyT, 1, 2);
        
        add(fish, 0, 3);
        add(fishT, 1, 3);
        
        add(qty, 0, 4);
        add(qtyT, 1, 4);
        
        add(rate, 0, 5);
        add(rateT, 1, 5);
        add(discount, 2, 5);
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
    public String getName(){ return nameT.getText().toUpperCase(); }
    public String getParty(){ return partyT.getText(); }
    public String getFish(){ return fishT.getText(); }
    public String getQty(){ return qtyT.getText(); }
    public String getRate(){ return rateT.getText(); }
    public float getDiscount(){
        if(!discount.getText().equals(""))
            return Float.parseFloat(discount.getText()); 
        else
            return 0;
    }
    public void setFocus() {
        Platform.runLater(new Runnable() {
            @Override public void run() { nameT.requestFocus(); }
        });
    }
    public boolean checkText(){
        return !(nameT.getText().equals("")
                || partyT.getText().equals("")
                || qtyT.getText().equals("")
                || fishT.getText().equals("")
                || rateT.getText().equals(""));
    }
    public void clearFields(){
//        nameT.getSelectionModel().clearSelection();
//        partyT.setText("");
//        fishT.setText("");
        qtyT.setText("");
        rateT.setText("");
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
