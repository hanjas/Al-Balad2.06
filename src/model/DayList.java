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
public class DayList {
    private StringProperty Name,Party,Fish,Qty,Rate,Subt,Total,Old,Grand,Debit,Balance;
    
    public DayList(String name,String party,String fish,String qty,String rate,String subt,String total,String old,String grand,String debit,String balance){
        this.Name = new SimpleStringProperty(name);
        this.Party = new SimpleStringProperty(party);
        this.Qty = new SimpleStringProperty(qty);
        this.Fish = new SimpleStringProperty(fish);
        this.Rate = new SimpleStringProperty(rate);
        this.Subt = new SimpleStringProperty(subt);
        this.Total = new SimpleStringProperty(total);
        this.Old = new SimpleStringProperty(old);
        this.Grand = new SimpleStringProperty(grand);
        this.Debit = new SimpleStringProperty(debit);
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
    
    public String getOld(){
        return Old.get();
    }
    public void setOld(String old){
        Old.set(old);
    }
    
    public String getGrand(){
        return Grand.get();
    }
    public void setGrand(String grand){
        Grand.set(grand);
    }
    
    public String getDebit(){
        return Debit.get();
    }
    public void setDebit(String debit){
        Debit.set(debit);
    }
    
    public String getBalance(){
        return Balance.get();
    }
    public void setBalance(String balance){
        Balance.set(balance);
    }
}
