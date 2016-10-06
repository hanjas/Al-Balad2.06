
package controller;

import database.DBAccess;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.util.StringConverter;
import model.*;


public class DailyBalanceController implements Initializable {

    @FXML private BorderPane printBorder;
    @FXML private Label nameLabel,dateLabel,totalLabel,pgNo;
    @FXML private Button printB,refresh, showBtn;
    @FXML private TableView tableView,tableViewT;
    @FXML private TableColumn col1,col2,col3,col4,col5,col6,col7,col8,col9,col10;
    @FXML private ImageView processImageView;
    @FXML private DatePicker datePicker;
    private BorderPane printBorderT;
    private DBAccess db;
    private ResultSet rs;
    private String tName,tSubT,tTotal,tOldBal,tGrandT,tDebit,tBalance;
    private Format format;
    private ObservableList tableList;
    private Task getBSDetails;
    private Thread thread;
    private int first = 0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try{
            processImageView.setVisible(false);
            db = new DBAccess();
            format = new SimpleDateFormat("d-M-yyyy");
            setDate();
            tableList = FXCollections.observableArrayList();
            Rectangle2D d = Screen.getPrimary().getVisualBounds();
            tableView.setPrefWidth(d.getWidth()-42);
            tableView.setPrefHeight(d.getHeight()-133);
            setColumnSize((d.getWidth()-45)/10);
            tableView.setItems(tableList);
            setTable();
        } catch ( Exception e ) { System.out.println(e);}
    }    
    private void setColumnSize(double width){
        col1.setPrefWidth(width);
        col2.setPrefWidth(width);
        col3.setPrefWidth(width);
        col4.setPrefWidth(width);
        col5.setPrefWidth(width);
        col6.setPrefWidth(width);
        col7.setPrefWidth(width);
        col8.setPrefWidth(width);
        col9.setPrefWidth(width);
        col10.setPrefWidth(width);
    }
    private void setDate() {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

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
    private String getDate() {
        return datePicker.getValue().format(DateTimeFormatter.ofPattern("d-M-yyyy"));
    }
    @FXML private void prevDate() throws SQLException{
        int day = Integer.parseInt(getDate().split("-")[0]);
        int monthT = Integer.parseInt(getDate().split("-")[1]);
        int year = Integer.parseInt(getDate().split("-")[2]);
        if(day == 1){
            day = 31;
            if(monthT==1){
                monthT = 12;
                year--;
            } else {
                monthT--;
            }
        } else {
            day--;
        }
        
        dateLabel.setText(day+"-"+monthT+"-"+String.valueOf(year));
        nameLabel.setText(getDate());
        setTable();
    }
    @FXML private void nextDate() throws SQLException{
        int day = Integer.parseInt(getDate().split("-")[0]);
        int monthT = Integer.parseInt(getDate().split("-")[1]);
        int year = Integer.parseInt(getDate().split("-")[2]);
        if(day == 31){
            day = 1;
            if(monthT==12){
                monthT = 1;
                year++;
            } else {
                monthT++;
            }
        } else {
            day++;
        }
        dateLabel.setText(day+"-"+monthT+"-"+String.valueOf(year));
        nameLabel.setText(getDate());
        setTable();
    }
    @FXML public void setTable() throws SQLException{
        processImageView.setVisible(true);
        printB.setDisable(true);
        refresh.setDisable(true);
        thread = new Thread( (Task)createWorker() );
        thread.setDaemon(true);
//        thread.interrupt();
        thread.start();
    }
    public Task createWorker() {
        return new Task() {
            long total = 0,tempTotal=0;
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < 1; i++) {
                    total = 0;
                    tableList.clear();
                    for(Object name : db.getMembers()){
                        rs = db.getDailyBalance(name.toString(), getDate());
                        tName = "";tSubT="";tTotal="";tOldBal="";tGrandT="";tDebit="";tBalance="";
                        while(rs.next()){
//                            System.out.println(rs.getString("name")+" name");
//                            System.out.println(thread.getName());
                            if(rs.getString("balance").equals("0.0") || rs.getString("balance").equals("0")){
                            } else {
                                if(tName.equals(rs.getString("Name")))
                                    if(tOldBal.equals(rs.getString("oldBal")) && tBalance.equals(rs.getString("balance"))) {
                                        tableList.add(new DailyBal("", rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"),"", "", ""));
                                    }
                                    else tableList.add(new DailyBal("", rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"),"", rs.getString("oldBal"), rs.getString("balance")));
                                else {
                                    tableList.add(new DailyBal(rs.getString("name"), rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"), rs.getString("dDate"), rs.getString("oldBal"), rs.getString("balance")));
                                    if(tempTotal!=rs.getLong("balance") && !tName.equals(rs.getString("name")))
                                        total = total+rs.getLong("balance");
                                    tempTotal = rs.getLong("balance");
                                }
                                tName = rs.getString("name");
                                tOldBal = rs.getString("oldBal");
                                tBalance = rs.getString("balance");
                            }
                        }
                    }
                    int temp =0;
                    for(int j=0;j<33;j++){
                        tableList.add(new DailyBal("", "", "", "", "", "", "", "", "", ""));
                        if(temp == 0){
                            tableList.add(new DailyBal("", "", "", "", "", "", "", "", "Total = ", ""+total));
                            temp=1;
                        }
                    }
                    tableView.setItems(tableList);  
                    
                }
                return true;
            }
            @Override
            protected void succeeded() {
                processImageView.setVisible(false);
                printB.setDisable(false);
                refresh.setDisable(false);
                showBtn.setDisable(false);
                totalLabel.setText("Total Balance = "+total);
            }
        };
    }
    
    
    @FXML private void print() throws InterruptedException{
        setPrintLayout();
        int check=0,checker=0,size = tableViewT.getItems().size()-2,page=0;
        while(tableViewT.getSelectionModel().getSelectedIndex() < size){
            page++;
            pgNo.setText("page "+page);
            Printer printer = Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
            PrinterJob job = PrinterJob.createPrinterJob();
            job.getJobSettings().setPageLayout(pageLayout);
            check = tableViewT.getSelectionModel().getSelectedIndex();
            if(job != null){
                boolean success = job.printPage(printBorderT);
                if(success){
                    job.endJob();
                }
            }
            tableViewT.scrollTo(tableViewT.getSelectionModel().getSelectedIndex()+32);
            tableViewT.getSelectionModel().select(tableViewT.getSelectionModel().getSelectedIndex()+32);
            if(tableViewT.getSelectionModel().getSelectedIndex()==check){
                if(checker==0){
                    tableViewT.getSelectionModel().select(tableViewT.getItems().size()-2);
                    tableViewT.scrollTo(tableViewT.getItems().size()-2);
                    checker=1;
                }else if(checker==1){
                    tableViewT.getSelectionModel().select(tableViewT.getItems().size()-1);
                    tableViewT.scrollTo(tableViewT.getItems().size()-1);
                }
            }
            System.out.println((size+1)+"-"+tableViewT.getSelectionModel().getSelectedIndex()+"-"+check);
        }
    }
    
    public void setPrintLayout(){
        TableColumn col1t,col2t,col3t,col4t,col5t,col6t,col7t,col8t,col9t,col10t;
//        TableView tableViewT;
        Label nameLabelT,dateLabelT;
        BorderPane titleT;
    
        printBorderT = new BorderPane();
        titleT = new BorderPane();
        nameLabelT = new Label();
        dateLabelT = new Label();
        pgNo = new Label();
        tableViewT = new TableView<Individual>();
        
        col1t = new TableColumn("Name");
        col1t.setCellValueFactory( new PropertyValueFactory("Name"));
        col1t.setPrefWidth(82);
        
        col2t = new TableColumn("Party");
        col2t.setCellValueFactory( new PropertyValueFactory("Party"));
        col2t.setPrefWidth(47);
        
        col3t = new TableColumn("Fish");
        col3t.setCellValueFactory( new PropertyValueFactory("Fish"));
        col3t.setPrefWidth(57);
        
        col4t = new TableColumn("Qty");
        col4t.setCellValueFactory( new PropertyValueFactory("Qty"));
        col4t.setPrefWidth(25);
        
        col5t = new TableColumn("Rate");
        col5t.setCellValueFactory( new PropertyValueFactory("Rate"));
        col5t.setPrefWidth(50);
        
        col6t = new TableColumn("Sub-Total");
        col6t.setCellValueFactory( new PropertyValueFactory("Subt"));
        col6t.setPrefWidth(50);
        
        col7t = new TableColumn("Total");
        col7t.setCellValueFactory( new PropertyValueFactory("Total"));
        col7t.setPrefWidth(50);
        
        col8t = new TableColumn("Date");
        col8t.setCellValueFactory( new PropertyValueFactory("Date"));
        col8t.setPrefWidth(65);
        
        col9t = new TableColumn("Old-Bal");
        col9t.setCellValueFactory( new PropertyValueFactory("Old"));
        col9t.setPrefWidth(60);
        
        col10t = new TableColumn("Balance");
        col10t.setCellValueFactory( new PropertyValueFactory("Balance"));
        col10t.setPrefWidth(60);
        
        tableViewT.getColumns().addAll(col1t,col8t,col2t,col3t,col4t,col5t,col6t,col7t,col9t,col10t);
        tableViewT.setItems(tableView.getItems());
        tableViewT.getStylesheets().add("/css/style.css");
        tableViewT.getSelectionModel().select(0);
//        tableViewT.scrollTo(tableView.getSelectionModel().getFocusedIndex());
        tableViewT.setPrefHeight(Paper.A4.getHeight()+20);
        tableViewT.setPrefWidth(Paper.A4.getWidth()+7);
        nameLabelT.setText(nameLabel.getText());
        dateLabelT.setText(getDate());
        pgNo.setText("Page 1");
        titleT.setLeft(nameLabelT);
        titleT.setCenter(dateLabelT);
        titleT.setRight(pgNo);
        titleT.setPadding(new Insets(0, 70, 0, 0));
        printBorderT.setPadding(new Insets(0, 0, 0, 10));
        printBorderT.setTop(titleT);
        printBorderT.setCenter(tableViewT);
    }
}
