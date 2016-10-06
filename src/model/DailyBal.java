/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author BCz
 */
public class DailyBal {
    private StringProperty Name,Party,Fish,Qty,Rate,Subt,Total,Date,Old,Balance;
    
    public DailyBal(String name,String party,String fish,String qty,String rate,String subt,String total,String date,String old,String balance){
        this.Name = new SimpleStringProperty(name);
        this.Party = new SimpleStringProperty(party);
        this.Qty = new SimpleStringProperty(qty);
        this.Fish = new SimpleStringProperty(fish);
        this.Rate = new SimpleStringProperty(rate);
        this.Subt = new SimpleStringProperty(subt);
        this.Total = new SimpleStringProperty(total);
        this.Date = new SimpleStringProperty(date);
        this.Old = new SimpleStringProperty(old);
        this.Balance = new SimpleStringProperty(balance);
        
    }
    public String getName(){
        return Name.get();
    }
    public void setName(String name){
        Name.set(name);
    }
    
    public String getParty(){
        return Party.get();
    }
    public void setParty(String party){
        Party.set(party);
    }
    
    public String getQty(){
        return Qty.get();
    }
    public void setQty(String qty){
        Qty.set(qty);
    }
    
    public String getFish(){
        return Fish.get();
    }
    public void setFish(String fish){
        Fish.set(fish);
    }
    
    public String getRate(){
        return Rate.get();
    }
    public void setRate(String rate){
        Rate.set(rate);
    }
    
    public String getSubt(){
        return Subt.get();
    }
    public void setSubt(String subt){
        Subt.set(subt);
    }
    
    public String getTotal(){
        return Total.get();
    }
    public void setTotal(String total){
        Total.set(total);
    }
    
    public String getDate(){
        return Date.get();
    }
    public void setDate(String date){
        Date.set(date);
    }
    
    public String getOld(){
        return Old.get();
    }
    public void setOld(String old){
        Old.set(old);
    }
    
    public String getBalance(){
        return Balance.get();
    }
    public void setBalance(String balance){
        Balance.set(balance);
    }
}
