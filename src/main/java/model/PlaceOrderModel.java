package model;

import dto.tm.PlaceOrderTm;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public interface PlaceOrderModel {
    ObservableList<String> getAllCustomerId() throws SQLException;
    ObservableList<String> getAllItemCode() throws SQLException;
    String getCustomerName(String id) throws SQLException;
    String getItemDescription(String code) throws SQLException;
    double getUnitPrice(String code) throws SQLException;
    int getQtyOnHand(String code) throws SQLException;
    boolean placeOrder(ObservableList<PlaceOrderTm> orderItems, String customerId) throws SQLException;
}
