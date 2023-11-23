package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.tm.PlaceOrderTm;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import model.PlaceOrderModel;
import model.impl.PlaceOrderModelImpl;

import java.sql.SQLException;

public class PlaceOrderController {

    @FXML
    private BorderPane containerPane;

    @FXML
    private MFXComboBox<String> cmbxCustomerId;

    @FXML
    private MFXComboBox<String> cmbxItemCode;

    @FXML
    private MFXTextField txtCustomerName;

    @FXML
    private MFXTextField txtItemDesc;

    @FXML
    private MFXTextField txtUnitPrice;

    @FXML
    private MFXTextField txtBuyQty;

    @FXML
    private Label lblTotalAmount;

    @FXML
    private MFXButton btnPlaceOrder;

    @FXML
    private MFXButton btnAddToCart;

    @FXML
    private JFXTreeTableView<PlaceOrderTm> tblOrder;

    private final PlaceOrderModel placeOrderModel = new PlaceOrderModelImpl();
    private final ObservableList<PlaceOrderTm> orderItems = FXCollections.observableArrayList();

    public void initialize() throws SQLException {

        // Loading items to combo boxes and text boxes
        cmbxCustomerId.setItems(placeOrderModel.getAllCustomerId());
        cmbxItemCode.setItems(placeOrderModel.getAllItemCode());
        cmbxCustomerId.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    txtCustomerName.setText(placeOrderModel.getCustomerName(cmbxCustomerId.getSelectedItem()));
                    orderItems.clear();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        cmbxItemCode.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    txtItemDesc.setText(placeOrderModel.getItemDescription(cmbxItemCode.getSelectedItem()));
                    txtUnitPrice.setText(Double.toString(placeOrderModel.getUnitPrice(cmbxItemCode.getSelectedItem())));
                    String toolTipMsg = "Remaining Qty is "+
                            Integer.toString(placeOrderModel.getQtyOnHand(cmbxItemCode.getSelectedItem()));
                    Tooltip toolTip = new Tooltip(toolTipMsg);
                    toolTip.setStyle("-fx-background-color: #cc4e5c");
                    txtBuyQty.setTooltip(toolTip);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Setting up the table

        //---- Declaration of Table Cols
        TreeTableColumn<PlaceOrderTm, String> colCode = new TreeTableColumn<>("Item Code");
        TreeTableColumn<PlaceOrderTm, String> colDescription = new TreeTableColumn<>("Description");
        TreeTableColumn<PlaceOrderTm, Integer> colUnitPrice = new TreeTableColumn<>("Unit Price");
        TreeTableColumn<PlaceOrderTm, Double> colBuyQty = new TreeTableColumn<>("Buying Qty");
        TreeTableColumn<PlaceOrderTm, JFXButton> colAction = new TreeTableColumn<>("Action");

        //---- Col Width Setup
        colCode.setPrefWidth(150);
        colDescription.setPrefWidth(230);
        colUnitPrice.setPrefWidth(150);
        colBuyQty.setPrefWidth(150);
        colAction.setPrefWidth(104);

        //---- Mapping the CustomerTm object with the Cols
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colBuyQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colAction.setCellValueFactory(new TreeItemPropertyValueFactory<>("btnDelete"));

        //----Adding the cols to table
        tblOrder.getColumns().addAll(colCode, colDescription, colUnitPrice, colBuyQty, colAction);
    }


    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        JFXButton btn = new JFXButton("❌");
        double amount = Double.parseDouble(txtUnitPrice.getText()) * Integer.parseInt(txtBuyQty.getText());
        PlaceOrderTm orderTm = new PlaceOrderTm(
                cmbxItemCode.getSelectedItem(),
                txtItemDesc.getText(),
                Integer.parseInt(txtBuyQty.getText()),
                Double.parseDouble(txtUnitPrice.getText()),
                amount,
                btn
        );

        btn.setOnAction(actionEvent -> {
            orderItems.remove(orderTm);
            lblTotalAmount.setText(String.valueOf(getTotalAmount()));
        });

        orderItems.add(orderTm);
        loadTable();
        lblTotalAmount.setText(String.valueOf(getTotalAmount()));
    }

    @FXML
    void btnPlaceOrderOnAction() throws SQLException {
        boolean isPlaced = placeOrderModel.placeOrder(orderItems, cmbxCustomerId.getSelectedItem());
        if (isPlaced) {
            new Alert(Alert.AlertType.INFORMATION,"Order Placed Successfully!").show();
        }
        orderItems.clear();
        clearFields();
    }

    private void loadTable() {
        TreeItem<PlaceOrderTm> root = new RecursiveTreeItem<>(orderItems, RecursiveTreeObject::getChildren);
        tblOrder.setRoot(root);
        tblOrder.setShowRoot(false);
        tblOrder.refresh();
    }

    private double getTotalAmount() {
        double totalAmount = 0;
        for (PlaceOrderTm item : orderItems) {
            totalAmount += item.getAmount();
        }
        return totalAmount;
    }

    private void clearFields() {
        cmbxItemCode.clear();
        cmbxCustomerId.clear();
        txtCustomerName.clear();
        txtItemDesc.clear();
        txtUnitPrice.clear();
        txtBuyQty.clear();
        tblOrder.refresh();
        lblTotalAmount.setText("0.00");
    }
}
