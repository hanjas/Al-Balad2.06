package controller;

import database.DBAccess;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import model.AddMembers;
import model.DebitDetails;
import model.FishDetails;
import model.Individual;
import model.RemoveFish;
import model.RemoveMembers;

public class IndividualController implements Initializable {

    @FXML private TableView tableView,tableViewT;
    @FXML private TableColumn col1,col2,col3,col4,col5,col6,col7,col8,col9,col10,col11;
    @FXML private ListView listView;
    @FXML private Label nameLabel,dateLabel,pgNo;
    @FXML private Button prevDate;
    @FXML private BorderPane printBorder,title;
    
    private BorderPane printBorderT;
    private DBAccess db;
    private ResultSet rs;
    private String tDate,month;
    private int monthT,year;
    private ObservableList tableList;
    private Format format;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
            format = new SimpleDateFormat("M-yyyy");
            dateLabel.setText(format.format(new Date()));
            db = new DBAccess();
            tableList = FXCollections.observableArrayList();
            Rectangle2D d = Screen.getPrimary().getVisualBounds();
            prevDate.setText("<");
            tableView.setPrefWidth(d.getWidth()-260);
            tableView.setPrefHeight(d.getHeight()-132);
            setColumnSize((d.getWidth()-262)/11);
            setList();
//            db.upd();
        } catch ( Exception e ) { System.out.println(e);}
    }    
    
    @FXML private void newWindow(ActionEvent e) throws SQLException{  //  ADD REPORT Button function
        
        Alert alert = new Alert(AlertType.NONE);
        TabPane tabPane = new TabPane();
        Tab fishDetail = new Tab("Fish Detail");
        Tab debitDetail = new Tab("Debit Detail");
        FishDetails fd = new FishDetails();                 // fish details object
        DebitDetails dd = new DebitDetails();               // debit details object
        
        fd.setList(db.getMembers());
        dd.setList(db.getMembers());
        
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.tabMinWidthProperty().set(80);
        
        alert.setTitle("Add Reports");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(tabPane);
        alert.initModality(Modality.APPLICATION_MODAL);
//        alert.initStyle(StageStyle.UNDECORATED);
        fishDetail.setContent(fd);                          //setting content of ff
        debitDetail.setContent(dd);                         //setting content of dd
        tabPane.getTabs().addAll(fishDetail,debitDetail);
        alert.getDialogPane().setPrefSize(800, 500);
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.NEXT,ButtonType.OK,ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        while(result.get()==ButtonType.NEXT){
            try{
                if(tabPane.getSelectionModel().getSelectedItem()==fishDetail){
                    //To do when next btn of fd is clicked
                    if(fd.checkText()==true){
                        //Insert details of fish to database FishDetails table
                        db.insertFishDetails(fd.getDate(), fd.getName(), fd.getParty(), fd.getFish(), fd.getQty(), fd.getRate(),fd.getDiscount(),"", "", "", fd.getDate(), "", "");
                        fd.clearFields();
                        fd.setFocus();
                    }
                } else if(tabPane.getSelectionModel().getSelectedItem()==debitDetail){
                    //To do when next btn of dd is clicked
                    if(dd.checkText()==true) {
                        //Insert details of debit to database DebitDetails table
                        db.updateDebit(dd.getDate(), dd.getName(), dd.getAmount());
                        dd.clearFields();
                        dd.setFocus();
                    }
                }
                setTable();     //Inserting datas to the table
            } catch (Exception ex) {Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, ex);}
            result = alert.showAndWait();   //  ReDisplaying the alert by pressing next btn
        }
        if(result.get()==ButtonType.OK){
            try{
                if(tabPane.getSelectionModel().getSelectedItem()==fishDetail){
                    //TODO when ok btn of fd is clicked
                    if(fd.checkText()==true){
                        db.insertFishDetails(fd.getDate(), fd.getName(), fd.getParty(), fd.getFish(), fd.getQty(), fd.getRate(),fd.getDiscount(),"", "", "", fd.getDate(), "", "");
                        fd.clearFields();
                        fd.setFocus();
                    }
                } else if(tabPane.getSelectionModel().getSelectedItem()==debitDetail){
                    //TODO when ok btn of dd is clicked
                    if(dd.checkText()==true) {
                        db.updateDebit(dd.getDate(), dd.getName(), dd.getAmount());
                        dd.clearFields();
                        dd.setFocus();
                    }
                }
                setTable();     //Inserting datas to the table
            } catch (Exception ex) {Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, ex);}
        } else { alert.close(); }
    }
    @FXML private void getDetails(MouseEvent me){
        try{
            nameLabel.setText(listView.getSelectionModel().getSelectedItem().toString().toUpperCase() );
            setTable();
            db.printDB(nameLabel.getText(), dateLabel.getText());
            db.printDebitDB(nameLabel.getText(), dateLabel.getText());
        } catch (Exception ex) {Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, ex);}
    }
    @FXML private void setDetailsKey(KeyEvent ke) throws SQLException{
        if(ke.getCode()==KeyCode.ENTER)
        nameLabel.setText(listView.getSelectionModel().getSelectedItem().toString().toUpperCase());
        setTable();
    }
    @FXML public void addMembers() throws SQLException{
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Add Reports");
        alert.setHeaderText(null);
        AddMembers members = new AddMembers();      // Members
        alert.getDialogPane().setContent(members);
        alert.getDialogPane().setPrefSize(600, 500);
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.NEXT,ButtonType.OK,ButtonType.CANCEL);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UNDECORATED);
        members.setList(db.getMembers());
        Optional<ButtonType> result = alert.showAndWait();
        while(result.get()==ButtonType.NEXT){
            try{
                if(members.checkText())
                db.insertMembers(members.getDate(), members.getName().toUpperCase(), members.getBalance());
                setList();
                members.clearFields();
            } catch (Exception ex) {Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, ex);}
//            members.setList(db.getMembers());
            result = alert.showAndWait();
        }
        if(result.get()==ButtonType.OK){
            try{
                if(members.checkText())
                db.insertMembers(members.getDate(), members.getName().toUpperCase(), members.getBalance());
                setList();
            } catch (Exception ex) {Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, ex);}
        } else { alert.close(); }
    }
    
    @FXML private void removeWindow() throws SQLException{
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Remove Report");
        alert.setHeaderText(null);
        
        RemoveFish rFish = new RemoveFish();
        rFish.setList(db.getMembers());
        
        alert.getDialogPane().setContent(rFish);
        alert.initModality(Modality.APPLICATION_MODAL);
//        alert.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().setPrefSize(600, 500);
        alert.getDialogPane().getButtonTypes().addAll(ButtonType.OK,ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK){
            if(rFish.checkText()==true){
                db.removeDetails(rFish.getDate(), rFish.getName(), rFish.getParty(), rFish.getFish());
                setTable();     //Inserting datas to the table
                alert.close();
            }
        }
    }
    @FXML private void removeBtn() throws SQLException{
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Add Reports");
        alert.setHeaderText(null);
        RemoveMembers rMembers = new RemoveMembers();
        rMembers.setListItems(db.getMembers());
        alert.getDialogPane().setContent(rMembers);
        alert.getDialogPane().setPrefSize(100, 500);
        ButtonType remove = new ButtonType("Remove");
        alert.getDialogPane().getButtonTypes().addAll(remove,ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==remove){
            try{
                db.removeMembers(rMembers.getSelectedItems());
                rMembers.clearItems();
                setTable();
                nameLabel.setText("");
                alert.close();
            } catch (Exception ex) {Logger.getLogger(IndividualController.class.getName()).log(Level.SEVERE, null, ex);}
        } else if(result.get()==ButtonType.CANCEL){
            rMembers.clearItems();
        }
        setList();
    }
    @FXML private void prevMonth() throws SQLException{
        monthT = Integer.parseInt(dateLabel.getText().split("-")[0]);
        year = Integer.parseInt(dateLabel.getText().split("-")[1]);
        if(monthT==1){
            month = "12";
            year--;
        } else {
            monthT--;
            month = ""+monthT;
        }
        dateLabel.setText(month+"-"+String.valueOf(year));
        setTable();
    }
    @FXML private void nextMonth() throws SQLException{
        monthT = Integer.parseInt(dateLabel.getText().split("-")[0]);
        year = Integer.parseInt(dateLabel.getText().split("-")[1]);
        if(monthT==12){
            month = "1";
            year++;
        } else {
            monthT++;
            month = ""+monthT;
        }
        dateLabel.setText(month+"-"+String.valueOf(year));
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
        Label nameLabelT,dateLabelT;
        BorderPane titleT;
    
        printBorderT = new BorderPane();
        titleT = new BorderPane();
        nameLabelT = new Label();
        dateLabelT = new Label();
        tableViewT = new TableView<Individual>();
        
        col1t = new TableColumn("date");
        col1t.setCellValueFactory( new PropertyValueFactory("Date"));
        col1t.setPrefWidth(65);
        
        col2t = new TableColumn("Party");
        col2t.setCellValueFactory( new PropertyValueFactory("Party"));
        col2t.setPrefWidth(50);
        
        col3t = new TableColumn("Fish");
        col3t.setCellValueFactory( new PropertyValueFactory("Fish"));
        col3t.setPrefWidth(60);
        
        
        col4t = new TableColumn("Qty");
        col4t.setCellValueFactory( new PropertyValueFactory("Qty"));
        col4t.setPrefWidth(30);
        
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
        col10t.setPrefWidth(60);
        
        col11t = new TableColumn("Balance");
        col11t.setCellValueFactory( new PropertyValueFactory("Balance"));
        col11t.setPrefWidth(60);
        
        tableViewT.getColumns().addAll(col1t,col2t,col3t,col4t,col5t,col6t,col7t,col8t,col9t,col10t,col11t);
        tableViewT.setItems(tableView.getItems());
        tableViewT.getStylesheets().add("/css/style.css");
        tableViewT.setPrefHeight(Paper.A4.getHeight()+20);
//        tableViewT.scrollTo(tableView.getSelectionModel().getFocusedIndex());
        nameLabelT.setText(nameLabel.getText());
        dateLabelT.setText(dateLabel.getText());
        pgNo = new Label();
        titleT.setLeft(nameLabelT);
        titleT.setCenter(dateLabelT);
        titleT.setRight(pgNo);
        titleT.setPadding(new Insets(0, 70, 0, 0));
        
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
        col11.setPrefWidth(width);
    }
    public void setList() throws SQLException{
        listView.setItems(db.getMembers());
    }
    public void setTable() throws SQLException{
        tableList.clear();
        rs = db.getAllDetails(dateLabel.getText(), nameLabel.getText());
        tDate = "";
        while(rs.next()){
            if(tDate.equals(rs.getString("date")))
                tableList.add(new Individual("", rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"), rs.getString("oldBal"), rs.getString("grandT"), rs.getString("debit"), rs.getString("balance")));
            else
                tableList.add(new Individual(rs.getString("date"), rs.getString("party"), rs.getString("fish"), rs.getString("qty"), rs.getString("rate"), rs.getString("subT"), rs.getString("total"), rs.getString("oldBal"), rs.getString("grandT"), rs.getString("debit"), rs.getString("balance")));
            tDate = rs.getString("date");
        }
        for(int i=0;i<33;i++){
            tableList.add(new Individual("", "", "", "", "", "", "", "", "", "", ""));
        }
        tableView.setItems(tableList);
    }
}
