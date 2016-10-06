package controller;

import database.DBAccess;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import model.*;

public class DayListController implements Initializable {

    @FXML private BorderPane printBorder;
    @FXML private Label nameLabel,dateLabel,totalLabel,debitLabel,pgNo;
    @FXML private Button prevDate,nextDate,printB;
    @FXML private TableView tableView,tableViewT;
    @FXML private TableColumn col1,col2,col3,col4,col5,col6,col7,col8,col9,col10;
    private BorderPane printBorderT;
    private ObservableList tableList;
    private DBAccess db;
    private ResultSet rs;
    private Format format;
    private int monthT,year,day;
    private String month,tName;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try{
            db = new DBAccess();
            tableList = FXCollections.observableArrayList();
            format = new SimpleDateFormat("d-M-yyyy");
            dateLabel.setText(format.format(new Date()));
            nameLabel.setText(dateLabel.getText());
            Rectangle2D d = Screen.getPrimary().getVisualBounds();
            prevDate.setText("<");
            tableView.setPrefWidth(d.getWidth()-45);
            tableView.setPrefHeight(d.getHeight()-142);
            setColumnSize((d.getWidth()-35)/11);
            setTable();
        } catch ( Exception e ) { System.out.println(e);}
    }    
    
    @FXML private void prevDate() throws SQLException{
        day = Integer.parseInt(dateLabel.getText().split("-")[0]);
        monthT = Integer.parseInt(dateLabel.getText().split("-")[1]);
        year = Integer.parseInt(dateLabel.getText().split("-")[2]);
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
        nameLabel.setText(dateLabel.getText());
        setTable();
    }
    @FXML private void nextDate() throws SQLException{
        day = Integer.parseInt(dateLabel.getText().split("-")[0]);
        monthT = Integer.parseInt(dateLabel.getText().split("-")[1]);
        year = Integer.parseInt(dateLabel.getText().split("-")[2]);
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
        nameLabel.setText(dateLabel.getText());
        setTable();
    }
    @FXML private void print(){
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
            tableViewT.scrollTo(tableViewT.getSelectionModel().getSelectedIndex()+33);
            tableViewT.getSelectionModel().select(tableViewT.getSelectionModel().getSelectedIndex()+33);
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
        TableColumn col1t,col2t,col3t,col4t,col5t,col6t,col7t,col8t,col9t,col10t,col11t;
        Label nameLabelT;
        BorderPane titleT;
    
        printBorderT = new BorderPane();
        titleT = new BorderPane();
        nameLabelT = new Label();
        tableViewT = new TableView<Individual>();
        
        col1t = new TableColumn("name");
        col1t.setCellValueFactory( new PropertyValueFactory("Name"));
        col1t.setPrefWidth(80);
        
        col2t = new TableColumn("Party");
        col2t.setCellValueFactory( new PropertyValueFactory("Party"));
        col2t.setPrefWidth(35);
        
        col3t = new TableColumn("Fish");
        col3t.setCellValueFactory( new PropertyValueFactory("Fish"));
        col3t.setPrefWidth(50);
        
        
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
        
        col8t = new TableColumn("Old-Bal");
        col8t.setCellValueFactory( new PropertyValueFactory("Old"));
        col8t.setPrefWidth(60);
        
        col9t = new TableColumn("G-Total");
        col9t.setCellValueFactory( new PropertyValueFactory("Grand"));
        col9t.setPrefWidth(60);
        
        col10t = new TableColumn("Debit");
        col10t.setCellValueFactory( new PropertyValueFactory("Debit"));
        col10t.setPrefWidth(55);
        
        col11t = new TableColumn("Balance");
        col11t.setCellValueFactory( new PropertyValueFactory("Balance"));
        col11t.setPrefWidth(60);
        
        tableViewT.getColumns().addAll(col1t,col2t,col3t,col4t,col5t,col6t,col7t,col8t,col9t,col10t,col11t);
        tableViewT.setItems(tableView.getItems());
        tableViewT.getStylesheets().add("/css/style.css");
        tableViewT.setPrefHeight(Paper.A4.getHeight()+20);
        nameLabelT.setText(nameLabel.getText());
        pgNo = new Label();
        titleT.setPadding(new Insets(0, 70, 0, 30));
        titleT.setLeft(nameLabelT);
        titleT.setRight(pgNo);
        
        printBorderT.setTop(titleT);
        printBorderT.setCenter(tableViewT);
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
    @FXML public void setTable() throws SQLException{
        rs = db.getDailyList(dateLabel.getText());
        float total=0,totalDebit=0;
        tName = "";
        tableList.clear();
        while(rs.next()){
            if(tName.equals(rs.getString("Name")))
                tableList.add(new DayList("", rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"), rs.getString("oldBal"), rs.getString("grandT"), rs.getString("debit"), rs.getString("balance")));
            else {
                tableList.add(new DayList(rs.getString("name"), rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"), rs.getString("oldBal"), rs.getString("grandT"), rs.getString("debit"), rs.getString("balance")));
                total = total+rs.getFloat("total");
                totalDebit = totalDebit+rs.getFloat("debit");
            }
            tName = rs.getString("name");
        }
        int temp=0;
        for(int i=0;i<33;i++){
            tableList.add(new DayList("", "", "", "", "", "", "", "", "", "", ""));
            if(temp == 0){
                tableList.add(new DayList("", "", "", "", "", "", "", "TotalDebit = ", ""+totalDebit, "TotalSale = ", ""+total));
                temp=1;
            }
        }
        tableView.setItems(tableList);
        totalLabel.setText("Total Sale : "+String.valueOf(total));
        debitLabel.setText("Total Debit : "+String.valueOf(totalDebit));
    }
}
