
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DBAccess {
    
    private Connection con;
    private Statement stmt;
    private ResultSet rs,rsI,rsOld,rsM,rsP,rsG,rsC,rsBal,rsDeb,rsDate,rsBalance,rsUp;
    private float subT,total,old;
    private ObservableList list;
    private float getTotal,getOldBal,tOld,tTotal,bal,gTotal,check= 1;
    private int id,deb,newDate;
    private String date,balance;
    
    public DBAccess(){
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:datas.db");
            stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            list = FXCollections.observableArrayList();
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Members ("       // Members table
                    + "ID INTEGER PRIMARY KEY,"
                    + "date TEXT,"
                    + "name TEXT unique,"
                    + "balance TEXT"
                    + "); ");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS AllDetails ("    // AllDetails table
                    + "ID INTEGER PRIMARY KEY,"
                    + "name TEXT,"
                    + "date TEXT,"
                    + "party TEXT,"
                    + "fish TEXT,"
                    + "qty TEXT,"
                    + "rate TEXT,"
                    + "subT TEXT,"
                    + "total TEXT,"
                    + "oldBal TEXT,"
                    + "grandT TEXT,"
                    + "dDate TEXT,"
                    + "debit TEXT,"
                    + "balance TEXT,"
                    + "type TEXT"
                    + ");");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS DebitDetails ("  // DebitDetails Table
                    + "ID INTEGER PRIMARY KEY,"
                    + "name TEXT ,"
                    + "date TEXT ,"
                    + "debit TEXT"
                    + "); ");
            
            
            
//            stmt.executeUpdate("delete from AllDetails where  date='18-5-2016' ");
//            stmt.executeUpdate("delete from AllDetails where  date='19-5-2016' ");
//            stmt.executeUpdate("delete from DebitDetails where  date='18-5-2016' ");
//            stmt.executeUpdate("delete from DebitDetails where  date='19-5-2016' ");
//            stmt.executeUpdate("Delete from AllDetails where name='TPB_AJ]' ");
            
        } catch (Exception ex) {Logger.getLogger(DBAccess.class.getName()).log(Level.SEVERE, null, ex);}
    }
    public void upd() throws SQLException{  //used to insert first debit of every customer
        for(Object name : getMembers()){
                float debi=99999999;
                String dates="";
                ResultSet crct = stmt.executeQuery("select * from AllDetails where name='"+name.toString()+"'");
                while(crct.next()){
                    if(debi==99999999){
                        if(crct.getFloat("debit")!= 0){
                            debi = crct.getFloat("debit");
                            dates=crct.getString("date");
                        }
                    }
                }
                if(debi!=99999999){
                    System.out.println(name.toString()+" "+dates+" "+debi);
                    updateDebit(dates,name.toString() ,""+debi);
                }
            }
    }
    public void insertFishDetails(String date,String name,String party,String fish,String qty,String rate,float discount,String total,String oldBal,String grandT,String dDate,String debit,String balance) throws SQLException{
        subT = Float.parseFloat(qty) * Float.parseFloat(rate) - discount;
        ResultSet rss = stmt.executeQuery("select * from AllDetails where name = '"+name+"' and date='"+date+"'");
        int i=0,idd=0;
        while(rss.next()){
            if(i==0){
                if(rss.getString("Party").equals("")){
                    idd = rss.getInt("id");
                    i=1;
                }
            }
        }
        if(i==1){
            stmt.executeUpdate("UPDATE AllDetails SET party='"+party+"',fish='"+fish+"',qty='"+qty+"',rate='"+rate+"',subT='"+subT+"' where id ="+idd);
        } else if (i==0) {
            stmt.executeUpdate("INSERT INTO AllDetails (name,date,party,fish,qty,rate,subT,total,oldBal,grandT,dDate,debit,balance,type) VALUES ("
                    + "'"+name+"',"
                    + "'"+date+"',"
                    + "'"+party+"',"
                    + "'"+fish+"',"
                    + "'"+qty+"',"
                    + "'"+rate+"',"
                    + "'"+subT+"',"
                    + "'"+total+"',"
                    + "'"+oldBal+"',"
                    + "'"+grandT+"',"
                    + "'"+dDate+"',"
                    + "'"+debit+"',"
                    + "'"+balance+"',"
                    + "'F'"
                    + ")");
        }
//        clearAttribute(date, name);
        setOtherDetails(date,name);
    }
    public void insertMembers(String date,String name,String balance) throws SQLException{
        stmt.executeUpdate("INSERT INTO Members (date,name,balance) VALUES ('"+date+"','"+name+"','"+balance+"'); ");
        stmt.executeUpdate("INSERT INTO AllDetails (name,date,dDate,oldBal,grandT,balance) VALUES ("
                + "'"+name+"',"
                + "'"+date+"',"
                + "'"+date+"',"
                + "'"+balance+"',"
                + "'"+balance+"',"
                + "'"+balance+"'"
                + ")");
    }
    
    public void setOtherDetails(String date,String name) throws SQLException{
        float deb=0,debt=0,bal;
        String prevBal = getPrevBalance(prevtDate(date), name);
        getTotal = getTotal(date,name);
        tTotal = getTotal + Float.parseFloat(prevBal);
        ResultSet rss = stmt.executeQuery("select debit from DebitDetails where name='"+name+"' and date='"+date+"'");
        while(rss.next()){
            deb = rss.getFloat("debit");
            debt=1;
        }
        if(debt==1){
            bal = tTotal - deb;
            stmt.executeUpdate("UPDATE AllDetails SET total = '"+ String.valueOf(getTotal) +"' , oldBal = '"+prevBal+"' "
                    + ", grandT = '"+ String.valueOf(tTotal) +"' ,"
                    + "dDate = '"+getDate(name)+"',"
                    + "debit = '"+deb+"',"
                    + "balance = '" + bal +"' "
                    + "  WHERE id = "+getID(date, name,0)+";");
            
        } else {
            stmt.executeUpdate("UPDATE AllDetails SET total = '"+ String.valueOf(getTotal) +"' , oldBal = '"+prevBal+"' "
                    + ", grandT = '"+ String.valueOf(tTotal) +"' ,"
                    + "balance = '" + String.valueOf(tTotal) +"' "
                    + "  WHERE id = "+getID(date, name,0)+";");
        }
        int dateD = Integer.parseInt(getDate(name).split("-")[0]);
        int dateM = Integer.parseInt(getDate(name).split("-")[1]);
        int dateY = Integer.parseInt(getDate(name).split("-")[2]);
        int dateDt = Integer.parseInt(date.split("-")[0]);
        int dateMt = Integer.parseInt(date.split("-")[1]);
        int dateYt = Integer.parseInt(date.split("-")[2]);
        // System.out.println(getDate(name)+" "+date);
        if(dateD>dateDt && dateM>=dateMt && dateY>=dateYt){
            String nextDate = date;
            do{
                nextDate=nextDate(nextDate);
                correctAfterRemove(name, nextDate, getPrevBalance(prevtDate(nextDate), name));
            }while(!getDate(name).equals(nextDate));
            if(getDate(name).equals(nextDate)){
               correctAfterRemove(name, nextDate, getPrevBalance(prevtDate(nextDate), name)); 
            }
        }
    }
    
    public String getDate(String name) throws SQLException{
        rsDate = stmt.executeQuery("select * from AllDetails where name ='"+name+"'");
        int d=0,m=0,y=0;
        while(rsDate.next()){
            if(d<Integer.parseInt(rsDate.getString("date").split("-")[0])){
                if(m<=Integer.parseInt(rsDate.getString("date").split("-")[1]) && y<=Integer.parseInt(rsDate.getString("date").split("-")[2])){
                    d=Integer.parseInt(rsDate.getString("date").split("-")[0]);
                    m=Integer.parseInt(rsDate.getString("date").split("-")[1]);
                    y=Integer.parseInt(rsDate.getString("date").split("-")[2]);
                }
            }else{
                if(m<Integer.parseInt(rsDate.getString("date").split("-")[1]) && y<=Integer.parseInt(rsDate.getString("date").split("-")[2])){
                    d=Integer.parseInt(rsDate.getString("date").split("-")[0]);
                    m=Integer.parseInt(rsDate.getString("date").split("-")[1]);
                    y=Integer.parseInt(rsDate.getString("date").split("-")[2]);
                }
            }
        }
        if(d==0 && m==0 && y==0){
            rsDate = stmt.executeQuery("select * from Members where name = '"+name+"' ");
            while(rsDate.next()){
                d=Integer.parseInt(rsDate.getString("date").split("-")[0]);
                m=Integer.parseInt(rsDate.getString("date").split("-")[1]);
                y=Integer.parseInt(rsDate.getString("date").split("-")[2]);
            }
        }
//        System.out.println("getDate "+name+"  "+d+"-"+m+"-"+y);
        return ""+d+"-"+m+"-"+y;
    }
    public String getdDate(String name) throws SQLException{
        rsDate = stmt.executeQuery("select * from AllDetails where name ='"+name+"'");
        String dDate = "not";
        while(rsDate.next()){
            // System.out.println(rsDate.getString("party"));
            try{
                if(rsDate.getString("party").equals("") || rsDate.getString("party").equals(null)){
                    
                }else {
                    dDate = rsDate.getString("Date");
                }
            }catch(NullPointerException ne){
                stmt.executeUpdate("UPDATE AllDetails SET party='',fish='',qty='',rate='' where id="+rsDate.getInt("id"));
            }
        }
        if(dDate.equals("not")){
            rsDate = stmt.executeQuery("select * from Members where name = '"+name+"'");
            while(rsDate.next()){
                dDate = rsDate.getString("Date");
            }
        }
//        System.out.println("dDate =    "+dDate);
        return dDate;
    }
    public float getTotal(String date,String name) throws SQLException{
        total=0;
        rs = stmt.executeQuery("select * from AllDetails where name = '"+name+"' and date = '"+date+"'");
        while(rs.next()){
            total = rs.getFloat("subT")+total;
        }
        return total;
    }
    public String getPrevBalance(String date,String name) throws SQLException{
        String ba="";
        int chk=0;
        do{
            ResultSet rsr = stmt.executeQuery("select * from AllDetails where name='"+name+"' and date='"+date+"'");
            int i=0;
            while(rsr.next()){
                if(i==0){
                    ba=rsr.getString("balance");
                    chk=1;
                    i=1;
                }
            }
            date=prevtDate(date);
            if(date.equals("1-2-2016"))
                break;
        }while(ba.equals(""));
        
        if(chk==0){
            ResultSet rst = stmt.executeQuery("select * from Members where name='"+name+"'");   //to check is it first detail
            while(rst.next()){
                ba = rst.getString("balance");
            }
        }
        return ba;
    } // for insert fd
    public String getPrevBalanceDebit(String date,String name) throws SQLException{
        String ba="";
        do{
            ResultSet rss = stmt.executeQuery("select * from AllDetails where name='"+name+"' and date='"+date+"'");
            int i=0;
            while(rss.next()){
                if(i==0){
                    ba=rss.getString("balance");
                    i=1;
                }
            }
            date=prevtDate(date);
        }while(ba.equals(""));
        return ba;
    }
    
    public int getID(String date,String name,int mod) throws SQLException{
        id=0;
        rsI = stmt.executeQuery("select * from AllDetails where date = '"+date+"' and name = '"+name+"'  order by CAST(date AS INTEGER)");
        while(rsI.next()){
            id = rsI.getInt("id");
            if(mod == 0)
                stmt.executeUpdate("UPDATE AllDetails SET total = '' , oldBal = '' , grandT = '' , debit = '' , balance = '' WHERE id = "+id+";");
            else if(mod == 1)
                stmt.executeUpdate("UPDATE AllDetails SET  debit = '' , balance = '' WHERE id = "+id+";");
        }
        return id;
    }
    public ResultSet getAllDetails(String date,String name) throws SQLException{
        return stmt.executeQuery("select * from AllDetails where name = '"+name+"' and date like '%"+date+"%' order by CAST(date AS INTEGER)");
    }
    public ResultSet getDailyList(String date) throws SQLException{
        return stmt.executeQuery("select * from AllDetails where date = '"+date+"' order by name");
    }
    public ResultSet getDailyBalance(String name, String toDate) throws SQLException{
        int check = 0;
//        rs = stmt.executeQuery("select ad.fish, ad.date, ad.name, ad.party, ad.qty, ad.rate, ad.subT, ad.total, ad.oldBal, ad.grandT, ad.debit, ad.balance, (select dDate from AllDetails where name=ad.name and date='"+toDate+"') as dDates  from AllDetails ad where name='"+name+"' and date=dDates;");
//        rs = stmt.executeQuery("select ad.fish, ad.date, ad.name, ad.party, ad.qty, ad.rate, ad.subT, ad.total, \n" +
//            " \n" +
//            " (select dDate from AllDetails where name=ad.name and date='"+toDate+"') as dDates,\n" +
//            " (select oldBal from AllDetails where name=ad.name and date='"+toDate+"') as oldBal,\n" +
//            " (select grandT from AllDetails where name=ad.name and date='"+toDate+"') as grandT,\n" +
//            " (select debit from AllDetails where name=ad.name and date='"+toDate+"') as debit,\n" +
//            " (select balance from AllDetails where name=ad.name and date='"+toDate+"') as balance\n" +
//            "\n" +
//            "  from AllDetails ad where name='"+name+"' and date=dDates;");
        rs = stmt.executeQuery("select * from AllDetails where name = '"+ name +"' and date ='"+ toDate +"'");
        while(rs.next()) {
            check = 1;
        }
//        System.out.println("returning");
        if(check == 1)
//            return stmt.executeQuery("select ad.fish, ad.date, ad.name, ad.party, ad.qty, ad.rate, ad.subT, ad.total, \n" +
//            " \n" +
//            " (select dDate from AllDetails where name=ad.name and date='"+toDate+"') as dDates,\n" +
//            " (select oldBal from AllDetails where name=ad.name and date='"+toDate+"') as oldBal,\n" +
//            " (select grandT from AllDetails where name=ad.name and date='"+toDate+"') as grandT,\n" +
//            " (select debit from AllDetails where name=ad.name and date='"+toDate+"') as debit,\n" +
//            " (select balance from AllDetails where name=ad.name and date='"+toDate+"') as balance\n" +
//            "\n" +
//            "  from AllDetails ad where name='"+name+"' and date=dDates;");
            return stmt.executeQuery("select * from AllDetails where name = '"+ name +"' and date ='"+ toDate +"'");
        String prevDate = getPrevDate(name, toDate);
//        System.out.println(prevDate);
//        return stmt.executeQuery("select ad.fish, ad.date, ad.name, ad.party, ad.qty, ad.rate, ad.subT, ad.total, \n" +
//            " \n" +
//            " (select dDate from AllDetails where name=ad.name and date='"+prevDate+"') as dDates,\n" +
//            " (select oldBal from AllDetails where name=ad.name and date='"+prevDate+"') as oldBal,\n" +
//            " (select grandT from AllDetails where name=ad.name and date='"+prevDate+"') as grandT,\n" +
//            " (select debit from AllDetails where name=ad.name and date='"+prevDate+"') as debit,\n" +
//            " (select balance from AllDetails where name=ad.name and date='"+prevDate+"') as balance\n" +
//            "\n" +
//            "  from AllDetails ad where name='"+name+"' and date=dDates;");
        return stmt.executeQuery("select * from AllDetails where name = '"+ name +"' and date ='"+ prevDate +"'");
    }
    public String getPrevDate(String name, String date) throws SQLException{
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy");
        try {
            Date fstDate = sdf.parse(getFirsttDate(name));
            Date todayDate = sdf.parse(date);
//            System.out.println(sdf.format(fstDate)+" "+sdf.format(todayDate));
            if(fstDate.before(todayDate) || fstDate.equals(todayDate)) {
                String ba="", prevDate = date;
                do {      
                    prevDate = prevtDate(prevDate);
                    rs = stmt.executeQuery("select * from AllDetails where name ='"+name+"' and date = '"+ prevDate +"'");
                    while(rs.next()) {
                        ba = rs.getString("date");
                    }
                } while (ba.equals(""));
                return ba;
            }
        }catch(Exception ex) {System.out.println(ex);}
        
        return "";
    }
    public String getFirsttDate(String name) throws SQLException{
        int d=0,m=0,y=0;
        rs = stmt.executeQuery("select * from AllDetails where name ='"+name+"'");
        while(rs.next()) {
            d=Integer.parseInt(rs.getString("date").split("-")[0]);
            m=Integer.parseInt(rs.getString("date").split("-")[1]);
            y=Integer.parseInt(rs.getString("date").split("-")[2]);
            break;
        }
        rs = stmt.executeQuery("select * from AllDetails where name ='"+name+"'");
        while(rs.next()){
            if(d>Integer.parseInt(rs.getString("date").split("-")[0])){
                if(m>=Integer.parseInt(rs.getString("date").split("-")[1]) && y>=Integer.parseInt(rs.getString("date").split("-")[2])){
                    d=Integer.parseInt(rs.getString("date").split("-")[0]);
                    m=Integer.parseInt(rs.getString("date").split("-")[1]);
                    y=Integer.parseInt(rs.getString("date").split("-")[2]);
                }
            }else{
                if(m>Integer.parseInt(rs.getString("date").split("-")[1]) && y>=Integer.parseInt(rs.getString("date").split("-")[2])){
                    d=Integer.parseInt(rs.getString("date").split("-")[0]);
                    m=Integer.parseInt(rs.getString("date").split("-")[1]);
                    y=Integer.parseInt(rs.getString("date").split("-")[2]);
                }
            }
        }
//        System.out.println("getDate "+name+"  "+d+"-"+m+"-"+y);
        return ""+d+"-"+m+"-"+y;
    }
    public ObservableList getMembers() throws SQLException{
        list.clear();
        rsM = stmt.executeQuery("select name from Members ORDER BY name");
        while(rsM.next()){
            list.add(rsM.getString("name"));
        }
        return list;
    }
    
    public void updateDebit(String date,String name,String amount) throws SQLException{ //update debit and update all rest amount
        rsUp = stmt.executeQuery("select * from AllDetails where name = '"+name+"' and date = '"+date+"'");
        int i=0;
        int idd = 0,debMod=0;
        String balanc="";
        float tot=0,oldBal,grand=0;
        while(rsUp.next()){
            if(i==0){
                idd = rsUp.getInt("id");
                tot = rsUp.getFloat("total");
                grand = rsUp.getFloat("grandT");
                i=1;
            }
        }
        rsUp = stmt.executeQuery("select * from DebitDetails where name = '"+name+"' and date = '"+date+"'");
        while(rsUp.next()){
            debMod=1;
//            System.out.println(rsUp.getFloat("debit")+" this this this this "+debMod);
        }
        float old,ba;
        if(i==1){
//            grand = Float.parseFloat(grandT);
            
            oldBal = Float.parseFloat(getPrevBalanceDebit(prevtDate(date), name));
            grand = tot+oldBal;
            ba = grand-Float.parseFloat(amount);
            stmt.executeUpdate("UPDATE AllDetails SET type ='D' , balance='"+ba+"' ,dDate = '"+getdDate(name)+"' ,debit = '"+amount+"', oldBal='"+oldBal+"', grandT='"+grand+"' where id="+idd);
            if(debMod==0){
                stmt.executeUpdate("insert into DebitDetails (name,date,debit) values ("
                    + "'"+name+"',"
                    + "'"+date+"',"
                    + "'"+amount+"'"
                    + ")");
            } else {
                stmt.executeUpdate("UPDATE DebitDetails SET debit='"+amount+"' where name='"+name+"' and date='"+date+"' ");
            }
        } else if(i==0){
            stmt.executeUpdate("INSERT INTO AllDetails (name,party,fish,qty,rate,date,oldBal,grandT,dDate,debit,balance,type) VALUES ("
                    + "'"+name+"',"
                    + "'',"
                    + "'',"
                    + "'',"
                    + "'',"
                    + "'"+date+"',"
                    + "'"+getPrevBalance(prevtDate(date), name)+"',"
                    + "'"+getPrevBalance(prevtDate(date), name)+"',"
                    + "'"+getdDate(name)+"',"
                    + "'"+amount+"',"
                    + "'"+(Float.parseFloat(getPrevBalance(prevtDate(date), name))-Float.parseFloat(amount))+"',"
                    + "'D'"
                    + ")");
            stmt.executeUpdate("insert into DebitDetails (name,date,debit) values ("
                    + "'"+name+"',"
                    + "'"+date+"',"
                    + "'"+amount+"'"
                    + ")");
            // System.out.println("insert");
        }
        int dateD = Integer.parseInt(getDate(name).split("-")[0]);
        int dateM = Integer.parseInt(getDate(name).split("-")[1]);
        int dateY = Integer.parseInt(getDate(name).split("-")[2]);
        int dateDt = Integer.parseInt(date.split("-")[0]);
        int dateMt = Integer.parseInt(date.split("-")[1]);
        int dateYt = Integer.parseInt(date.split("-")[2]);
        // System.out.println(getDate(name)+" "+date);
        System.out.println(dateD+">"+dateDt+" "+dateM+">="+dateMt);
        if(dateD>dateDt && dateM>=dateMt && dateY>=dateYt){
            String nextDate = date;
            do{
                nextDate=nextDate(nextDate);     //correcting all of rest balance and grandtotal and oldbalance
                correctRestData(nextDate, name, getPrevBalanceDebit(prevtDate(nextDate), name));    //correcting nextdetails with prevBalance
            }while(!nextDate.equals(getDate(name)));
            if(getDate(name).equals(nextDate)){
              correctRestData(nextDate, name, getPrevBalanceDebit(prevtDate(nextDate), name));    //correcting nextdetails with prevBalance
            }
        }else if(dateD<dateDt && dateM>dateMt && dateY>=dateYt){
            String nextDate = date;
            do{
                nextDate=nextDate(nextDate);     //correcting all of rest balance and grandtotal and oldbalance
                correctRestData(nextDate, name, getPrevBalanceDebit(prevtDate(nextDate), name));    //correcting nextdetails with prevBalance
            }while(!nextDate.equals(getDate(name)));
            if(getDate(name).equals(nextDate)){
              correctRestData(nextDate, name, getPrevBalanceDebit(prevtDate(nextDate), name));    //correcting nextdetails with prevBalance
            }
        }
    }
    public void correctRestData(String date,String name,String prevBalance) throws SQLException{
        ResultSet rss = stmt.executeQuery("select id,total,debit from AllDetails where name = '"+name+"' and date='"+date+"'");
        int idd=0,i=0,debt=0;
        float tot=0,grandT,ba,deb=0;
        while(rss.next()){
            if(i==0){
                idd = rss.getInt("id");
                tot = rss.getFloat("total");
                deb=rss.getFloat("debit");
                i=1;
                System.out.println(date+" "+tot+" "+prevBalance);
            }
        }
        if(i==1){
            grandT = tot + Float.parseFloat(prevBalance);
            ba = grandT - deb;
            stmt.executeUpdate("UPDATE AllDetails SET oldBal = '"+prevBalance+"', grandT ='"+grandT+"' , balance = '"+ba+"' where id="+idd);
            i=2;
        }
    }
    public void removeDetails(String date,String name,String party,String fish) throws SQLException{
        float tot,oldBal,gTotal,deb=0,ba;
        int idd=0,idR=0,iR=0,i=0,debMod=0;
        oldBal = Float.parseFloat(getPrevBalance(prevtDate(date), name));
        ResultSet rss = stmt.executeQuery("select debit from DebitDetails where name='"+name+"' and date='"+date+"'");
        while(rss.next()){
//            deb = rss.getFloat("debit");
            debMod=1;
        }
        if(debMod==1){
            rss = stmt.executeQuery("select * from AllDetails where name='"+name+"' and date='"+date+"'");
            while(rss.next()){
                if(iR==0){
                    idR= rss.getInt("id");
                    deb = rss.getFloat("debit");
                    iR=1;
                }
            }
        }
        stmt.executeUpdate("DELETE FROM AllDetails where name='"+name+"' and date='"+date+"' and party='"+party+"' and fish='"+fish+"'");
        System.out.println("deleted "+name+date+party+fish);
        tot = getTotal(date, name);
        gTotal = tot+oldBal;
        ba = gTotal-deb;
        rss = stmt.executeQuery("select * from AllDetails where name ='"+name+"' and date='"+date+"'");
        while(rss.next()){
            if(i==0){
                idd=rss.getInt("id");
                i=1;
            }
        }
        if(i==1){
            stmt.executeUpdate("UPDATE AllDetails SET total='"+tot+"' , "
                    + "oldBal='"+oldBal+"',"
                    + "grandT='"+gTotal+"',"
                    + "debit='"+deb+"',"
                    + "balance='"+ba+"' where id="+idd);
            i=2;
        }
        if(i==0){
            if(debMod==1){
                stmt.executeUpdate("INSERT INTO AllDetails (name,id,date,party,fish,qty,rate,subT,total,oldBal,grandT,dDate,debit,balance,type) VALUES ("
                        + "'"+name+"',"
                        + ""+idR+","
                        + "'"+date+"',"
                        + "'',"
                        + "'',"
                        + "'',"
                        + "'',"
                        + "'',"
                        + "'"+tot+"',"
                        + "'"+oldBal+"',"
                        + "'"+gTotal+"',"
                        + "'"+getdDate(name)+"',"
                        + "'"+deb+"',"
                        + "'"+ba+"',"
                        + "'F'"
                        + ")");
            }
        }
        int dateD = Integer.parseInt(getDate(name).split("-")[0]);
        int dateM = Integer.parseInt(getDate(name).split("-")[1]);
        int dateY = Integer.parseInt(getDate(name).split("-")[2]);
        int dateDt = Integer.parseInt(date.split("-")[0]);
        int dateMt = Integer.parseInt(date.split("-")[1]);
        int dateYt = Integer.parseInt(date.split("-")[2]);
        // System.out.println(getDate(name)+" "+date);
        if(dateD>dateDt && dateM>=dateMt && dateY>=dateYt){
            String nextDate = date;
            do{
                nextDate=nextDate(nextDate);
                correctAfterRemove(name, nextDate, getPrevBalance(prevtDate(nextDate), name));
            }while(!getDate(name).equals(nextDate));
            if(getDate(name).equals(nextDate)){
               correctAfterRemove(name, nextDate, getPrevBalance(prevtDate(nextDate), name)); 
            }
        }
    }
    public void correctAfterRemove(String name,String date,String oldBal) throws SQLException{
        float gTotal=0,ba=0,tot=0,i=0,deb=0;
        int debMod=0;
        ResultSet rss = stmt.executeQuery("select debit from AllDetails where name='"+name+"' and date='"+date+"'");
        // System.out.println("date                                      "+date+"            name="+name);
        while(rss.next()){
            if(i==0){
                deb = rss.getFloat("debit");
                debMod=1;
                i=1;
            }
            // System.out.println("debit ==========="+deb+"  date"+date+"  name+"+name);
        }
        i=0;
        int idd=0;
            tot = getTotal(date, name);
            gTotal = tot+Float.parseFloat(oldBal);
            ba = gTotal;
        rss = stmt.executeQuery("select * from AllDetails where name='"+name+"' and date='"+date+"'");
        while(rss.next()){
            if(i==0){
                idd= rss.getInt("id");
                i=1;
            }
        }
        if(i==1){
            if(debMod==1){
                stmt.executeUpdate("UPDATE AllDetails SET "
                            + "total='"+tot+"' ,"
                            + "oldBal='"+oldBal+"' ,"
                            + "grandT='"+gTotal+"',"
                            + "balance='"+(ba-deb)+"' where id="+idd);
            } else {
                stmt.executeUpdate("UPDATE AllDetails SET "
                            + "total='"+tot+"' ,"
                            + "oldBal='"+oldBal+"' ,"
                            + "grandT='"+gTotal+"',"
                            + "balance='"+(ba-deb)+"' where id="+idd);
            }
        }
        // System.out.println(name+" "+date+" "+tot+" "+oldBal+" "+gTotal+" "+ba);
    }
    
    public void removeMembers(ObservableList list) throws SQLException{
        for(Object name : list){
            stmt.executeUpdate("DELETE FROM Members WHERE name = '"+name+"'");
            stmt.executeUpdate("DELETE FROM DebitDetails WHERE name = '"+name+"'");
            stmt.executeUpdate("DELETE FROM AllDetails WHERE name = '"+name+"'");
        }
    }
    public void clearAttribute(String date,String name) throws SQLException{
        id = 0;
        rsC = stmt.executeQuery("select * from AllDetails where date = '"+date+"' and name = '"+name+"' order by date");
        while(rsC.next()){
            stmt.executeUpdate("UPDATE AllDetails SET total = '' , oldBal = '' , grandT = '' , debit = '' , balance = '' WHERE id = "+rsC.getInt("id")+";");
        }
    }
    public void printDB(String name,String date) throws SQLException{
        System.out.println("rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        rsP = stmt.executeQuery("select * from AllDetails where name = '"+name+"' ");
        while(rsP.next()){
//            System.out.println(
//                    " id "+rsP.getString("id")+
//                    " ,party "+rsP.getString("party"));
        }
    }
    public void printDebitDB(String name,String date) throws SQLException{
        System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        rsP = stmt.executeQuery("select * from DebitDetails where name = '"+name+"' ");
        while(rsP.next()){
//            System.out.println(
//                    " id "+rsP.getString("id")+
//                    " ,Date "+rsP.getString("date")+
//                    " ,  debit "+rsP.getString("debit")
//            );
        }
    }
    
    public String nextDate(String date){
        String nextDAte;
        int d,m,y;
        d = Integer.parseInt(date.split("-")[0]);
        m = Integer.parseInt(date.split("-")[1]);
        y = Integer.parseInt(date.split("-")[2]);
        if(d==31){
            d=1;
            if(m == 12){
                m = 1;
                y++;
            } else {
                m++;
            }
        } else {
            d++;
        }
        return ""+d+"-"+m+"-"+y;
    }
    public String prevtDate(String date){
        String nextDAte;
        int d,m,y;
        d = Integer.parseInt(date.split("-")[0]);
        m = Integer.parseInt(date.split("-")[1]);
        y = Integer.parseInt(date.split("-")[2]);
        if(d==1){
            d=31;
            if(m == 1){
                m = 12;
                y--;
            } else {
                m--;
            }
        } else {
            d--;
        }
        return ""+d+"-"+m+"-"+y;
    }
}
