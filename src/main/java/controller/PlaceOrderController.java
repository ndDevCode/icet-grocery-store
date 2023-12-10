package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.PlaceOrderTm;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import model.CustomerModel;
import model.ItemModel;
import model.OrderModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelImpl;
import model.impl.OrderModelImpl;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    private Label lblOrderId;

    @FXML
    private MFXButton btnPlaceOrder;

    @FXML
    private MFXButton btnAddToCart;

    @FXML
    private JFXTreeTableView<PlaceOrderTm> tblOrder;

    private final ObservableList<PlaceOrderTm> orderItems = FXCollections.observableArrayList();
    private List<CustomerDto> customers;
    private List<ItemDto> items;

    private CustomerModel customerModel = new CustomerModelImpl();
    private ItemModel itemModel = new ItemModelImpl();
    private OrderModel orderModel = new OrderModelImpl();

    public void initialize() {

        // Loading items to combo boxes and text boxes
        try {
            customers = customerModel.getAllCustomer();
            items = itemModel.getAllItem();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadCustomerIds();
        loadItemCodes();
        setOrderId();

        cmbxCustomerId.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                for (CustomerDto dto:customers) {
                    if (dto.getId().equals(newValue)){
                        clearFields();
                        orderItems.clear();
                        txtCustomerName.setText(dto.getName());
                    }
                }
            }

        });

        cmbxItemCode.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                String toolTipMsg = null;
                for (ItemDto dto:items) {
                    if (dto.getCode().equals(newValue.toString())){
                        txtItemDesc.setText(dto.getDescription());
                        txtUnitPrice.setText(String.format("%.2f",dto.getUnitPrice()));
                        toolTipMsg = "Remaining Qty is "+
                                Integer.toString(dto.getQtyOnHand());
                    }
                }
                Tooltip toolTip = new Tooltip(toolTipMsg);
                toolTip.setStyle("-fx-background-color: #cc4e5c");
                txtBuyQty.setTooltip(toolTip);
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
        List<OrderDetailsDto> list = new ArrayList<>();
        for (PlaceOrderTm tm:orderItems) {
            list.add(new OrderDetailsDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount()/tm.getQty()
            ));
        }

        OrderDto dto = new OrderDto(
                lblOrderId.getText(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                cmbxCustomerId.getValue().toString(),
                list
        );


        try {
            boolean isSaved = orderModel.saveOrder(dto);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION, "Order Saved!").show();
                setOrderId();
            }else{
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        clearFields();
        items = itemModel.getAllItem();
    }

    private void setOrderId() {
        try {
            String id = orderModel.getLastOrder().getId();
            if (id!=null){
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                lblOrderId.setText(String.format("D%03d",num));
            }else{
                lblOrderId.setText("D001");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

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

    private void loadItemCodes() {
        ObservableList list = FXCollections.observableArrayList();

        for (ItemDto dto:items) {
            list.add(dto.getCode());
        }

        cmbxItemCode.setItems(list);
    }

    private void loadCustomerIds() {
        ObservableList list = FXCollections.observableArrayList();

        for (CustomerDto dto:customers) {
            list.add(dto.getId());
        }

        cmbxCustomerId.setItems(list);
    }
}
