package controller;

import bo.BoFactory;
import bo.custom.CustomerBo;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.util.BoType;
import db.DBConnection;
import dto.CustomerDto;
import dto.tm.CustomerTm;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.SQLException;
import java.util.List;

public class CustomerViewController {

    @FXML
    private MFXTextField txtCusId;

    @FXML
    private MFXTextField txtCusName;

    @FXML
    private MFXTextField txtCusAddress;

    @FXML
    private MFXTextField txtCusSalary;

    @FXML
    private MFXTextField txtSearch;

    @FXML
    private MFXButton btnSave;

    @FXML
    private MFXButton btnRefresh;

    @FXML
    private MFXButton btnUpdate;

    @FXML
    private MFXButton btnReport;

    @FXML
    private JFXTreeTableView<CustomerTm> customerTable;

    private final CustomerBo customerBo = BoFactory.getInstance().getBo(BoType.CUSTOMER);


    public void initialize() throws SQLException, ClassNotFoundException {
        //---- Declaration of Table Cols
        TreeTableColumn colId = new TreeTableColumn<>("Customer ID");
        TreeTableColumn colName = new TreeTableColumn<>("Customer Name");
        TreeTableColumn colAddress = new TreeTableColumn<>("Customer Address");
        TreeTableColumn colSalary = new TreeTableColumn<>("Customer Salary");
        TreeTableColumn colAction = new TreeTableColumn<>("Action");

        //---- Col Width Setup
        colId.setPrefWidth(120);
        colName.setPrefWidth(120);
        colAddress.setPrefWidth(120);
        colSalary.setPrefWidth(120);
        colAction.setPrefWidth(120);

        //---- Mapping the CustomerTm object with the Cols
        colId.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,String>("id"));
        colName.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,String>("name"));
        colAddress.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,String>("address"));
        colSalary.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,Double>("salary"));
        colAction.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,JFXButton>("btnDelete"));

        //----Adding the cols to table
        customerTable.getColumns().addAll(colId,colName,colAddress,colSalary,colAction);

        //----Adding Selection event to the table
        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        //----Initial table loading
        loadTableData();

        //----Search Table
        txtSearch.
                textProperty().addListener((observableValue, oldValue, newValue) -> customerTable.setPredicate(customerTmTreeItem -> {
                String name = customerTmTreeItem.getValue().getName().toLowerCase();
                String id = customerTmTreeItem.getValue().getId().toLowerCase();
                String address = customerTmTreeItem.getValue().getAddress().toLowerCase();
                return name.contains(newValue.toLowerCase()) ||
                        id.contains(newValue.toLowerCase()) ||
                        address.contains(newValue.toLowerCase());
        }));

    }

    private void setData(TreeItem<CustomerTm> newValue) {
        if (newValue != null) {
            txtCusId.setEditable(false);
            txtCusId.setText(newValue.getValue().getId());
            txtCusName.setText(newValue.getValue().getName());
            txtCusAddress.setText(newValue.getValue().getAddress());
            txtCusSalary.setText(String.valueOf(newValue.getValue().getSalary()));
        }
    }

    @FXML
    private void loadTableData() throws SQLException, ClassNotFoundException {
        ObservableList<CustomerTm> tmListCustomer = FXCollections.observableArrayList();
        List<CustomerDto> customerDtoList = customerBo.allCustomers();

        for(CustomerDto dto:customerDtoList){
            JFXButton btn = new JFXButton("❌");
            CustomerTm customer = new CustomerTm(
                    dto.getId(),
                    dto.getName(),
                    dto.getAddress(),
                    dto.getSalary(),
                    btn);

            btn.setOnAction(actionEvent -> {
                try {
                    deleteCustomer(customer.getId());
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            tmListCustomer.add(customer);
        }

        TreeItem<CustomerTm> root = new RecursiveTreeItem<>(tmListCustomer,RecursiveTreeObject::getChildren);
        customerTable.setRoot(root);
        customerTable.setShowRoot(false);
    }

    private void deleteCustomer(String id) throws SQLException, ClassNotFoundException {

        if (customerBo.deleteCustomer(id)){
            new Alert(Alert.AlertType.INFORMATION,"Customer Deleted!").show();
            loadTableData();
            customerTable.refresh();
        }else{
            new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
        }
    }

    @FXML
    private void btnSaveOnAction() throws SQLException, ClassNotFoundException {

        CustomerDto customerDto = new CustomerDto(
                txtCusId.getText(),
                txtCusName.getText(),
                txtCusAddress.getText(),
                Double.parseDouble(txtCusSalary.getText())
        );

        if (customerBo.saveCustomer(customerDto)){
            new Alert(Alert.AlertType.INFORMATION,"Customer Saved!").show();
            loadTableData();
            clearFields();
        }else{
            new Alert(Alert.AlertType.ERROR,"An Error Occurred!").show();
        }
    }

    @FXML
    private void btnUpdateOnAction() throws SQLException, ClassNotFoundException {
        CustomerDto customerDto = new CustomerDto(
                txtCusId.getText(),
                txtCusName.getText(),
                txtCusAddress.getText(),
                Double.parseDouble(txtCusSalary.getText())
        );

        if (customerBo.updateCustomer(customerDto)){
            new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
            loadTableData();
            clearFields();
        }else {
            new Alert(Alert.AlertType.ERROR,"An Error Occurred!").show();
        }
    }

    @FXML
    private void btnReloadOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        loadTableData();
        customerTable.refresh();
        clearFields();
    }

    @FXML
    private void btnGetReportOnAction(){
        try {
            JasperDesign design = JRXmlLoader.load("src/main/resources/reports/customerRP.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint,false);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }


    private void clearFields() {
        customerTable.refresh();
        txtCusSalary.clear();
        txtCusAddress.clear();
        txtCusName.clear();
        txtCusId.clear();
        txtCusId.setEditable(true);
    }
}
