package controller;

import bo.BoFactory;
import bo.custom.ItemBo;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.util.BoType;
import db.DBConnection;
import dto.ItemDto;
import dto.tm.CustomerTm;
import dto.tm.ItemTm;
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

public class ItemViewController {

    @FXML
    private JFXTreeTableView<ItemTm> itemTable;

    @FXML
    private MFXTextField txtItemCode;

    @FXML
    private MFXTextField txtItemDesc;

    @FXML
    private MFXTextField txtUnitPrice;

    @FXML
    private MFXTextField txtQtyOnHand;

    @FXML
    private MFXTextField txtSearch;

    @FXML
    private MFXButton btnSave;

    @FXML
    private MFXButton btnRefresh;

    @FXML
    private MFXButton btnUpdate;

    private final ItemBo itemBo = BoFactory.getInstance().getBo(BoType.ITEM);


    public void initialize() throws SQLException, ClassNotFoundException {

        //---- Declaration of Table Cols
        TreeTableColumn colCode = new TreeTableColumn<>("Item Code");
        TreeTableColumn colDescription = new TreeTableColumn<>("Description");
        TreeTableColumn colUnitPrice = new TreeTableColumn<>("Unit Price");
        TreeTableColumn colQtyOnHand = new TreeTableColumn<>("Qty On Hand");
        TreeTableColumn colAction = new TreeTableColumn<>("Action");

        //---- Col Width Setup
        colCode.setPrefWidth(100);
        colDescription.setPrefWidth(200);
        colUnitPrice.setPrefWidth(100);
        colQtyOnHand.setPrefWidth(100);
        colAction.setPrefWidth(100);

        //---- Mapping the CustomerTm object with the Cols
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,String>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,String>("description"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,Double>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm,Integer>("qtyOnHand"));
        colAction.setCellValueFactory(new TreeItemPropertyValueFactory<CustomerTm, JFXButton>("btnDelete"));

        //----Adding the cols to table
        itemTable.getColumns().addAll(colCode,colDescription,colUnitPrice,colQtyOnHand,colAction);

        //----Adding Selection event to the table
        itemTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        //----Initial table loading
        loadTableData();

        //----Search Table
        txtSearch.textProperty().addListener((observableValue, oldValue, newValue) -> itemTable.setPredicate(itemTmTreeItem -> {
            String code = itemTmTreeItem.getValue().getCode().toLowerCase();
            String desc = itemTmTreeItem.getValue().getDescription().toLowerCase();

            return code.contains(newValue.toLowerCase()) ||
                    desc.contains(newValue.toLowerCase());
        }));

    }

    private void setData(TreeItem<ItemTm> newValue) {
        if (newValue != null) {
            txtItemCode.setEditable(false);
            txtItemCode.setText(newValue.getValue().getCode());
            txtItemDesc.setText(newValue.getValue().getDescription());
            txtUnitPrice.setText(String.valueOf(newValue.getValue().getUnitPrice()) );
            txtQtyOnHand.setText(String.valueOf(newValue.getValue().getQtyOnHand()) );
        }
    }

    private void loadTableData() throws SQLException, ClassNotFoundException {
        ObservableList<ItemTm> tmListItem = FXCollections.observableArrayList();

        List<ItemDto> itemDtoList = itemBo.getAllItem();

        for(ItemDto dto:itemDtoList){
            JFXButton btn = new JFXButton("❌");
            ItemTm item = new ItemTm(
                    dto.getCode(),
                    dto.getDescription(),
                    dto.getUnitPrice(),
                    dto.getQtyOnHand(),
                    btn);

            btn.setOnAction(actionEvent -> {
                try {
                    deleteItem(item.getCode());
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });

            tmListItem.add(item);
        }

        TreeItem<ItemTm> root = new RecursiveTreeItem<>(tmListItem, RecursiveTreeObject::getChildren);
        itemTable.setRoot(root);
        itemTable.setShowRoot(false);
    }

    private void deleteItem(String code) throws SQLException, ClassNotFoundException {

        if (itemBo.deleteItem(code)){
            new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
        }else{
            new Alert(Alert.AlertType.ERROR,"An Error Occurred!").show();
        }

        loadTableData();
        itemTable.refresh();
    }

    @FXML
    private void btnReloadOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        loadTableData();
        itemTable.refresh();
        clearFields();
    }

    @FXML
    private void btnSaveOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        ItemDto itemDto = new ItemDto(
                txtItemCode.getText(),
                txtItemDesc.getText(),
                Double.parseDouble(txtUnitPrice.getText()) ,
                Integer.parseInt(txtQtyOnHand.getText())
        );

        if (itemBo.saveItem(itemDto)){
            new Alert(Alert.AlertType.INFORMATION,"Item Saved!").show();
            loadTableData();
            clearFields();
        }else{
            new Alert(Alert.AlertType.ERROR,"An Error Occurred!").show();
        }
    }

    @FXML
    private void btnUpdateOnAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        ItemDto itemDto = new ItemDto(
                txtItemCode.getText(),
                txtItemDesc.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );

        if (itemBo.updateItem(itemDto)){
            new Alert(Alert.AlertType.INFORMATION,"Item Updated!").show();
            loadTableData();
            clearFields();
        }
    }

    @FXML
    private void btnGetReportOnAction(){
        try {
            JasperDesign design = JRXmlLoader.load("src/main/resources/reports/itemRP.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DBConnection.getInstance().getConnection());
            JasperViewer.viewReport(jasperPrint,false);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearFields() {
        itemTable.refresh();
        txtItemCode.clear();
        txtItemDesc.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtItemCode.setEditable(true);
    }
}
