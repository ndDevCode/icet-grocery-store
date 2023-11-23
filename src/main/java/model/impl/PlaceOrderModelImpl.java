package model.impl;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import db.DBConnection;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.PlaceOrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.PlaceOrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderModelImpl implements PlaceOrderModel {

    private static Connection connection;

    static {
        //----Initialize the db Connection
        try {
            connection = DBConnection.getInstance().getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public ObservableList<String> getAllCustomerId() throws SQLException {
        String sql = "SELECT id FROM customer";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
        ObservableList<String> customerNameList = FXCollections.observableArrayList();
        while (result.next()){
            customerNameList.add(result.getString(1));
        }
        pstm.close();
        return customerNameList;
    }

    @Override
    public ObservableList<String> getAllItemCode() throws SQLException {
        String sql = "SELECT code FROM item";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
        ObservableList<String> itemCodeList = FXCollections.observableArrayList();
        while (result.next()){
            itemCodeList.add(result.getString(1));
        }
        pstm.close();
        return itemCodeList;
    }

    @Override
    public String getCustomerName(String id) throws SQLException {
        String sql = "SELECT name FROM customer WHERE id=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,id);
        ResultSet result = pstm.executeQuery();
        result.next();
        return result.getString(1);
    }

    @Override
    public String getItemDescription(String code) throws SQLException {
        String sql = "SELECT description FROM item WHERE code=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,code);
        ResultSet result = pstm.executeQuery();
        result.next();
        return result.getString(1);
    }

    @Override
    public double getUnitPrice(String code) throws SQLException {
        String sql = "SELECT unitPrice FROM item WHERE code=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,code);
        ResultSet result = pstm.executeQuery();
        result.next();
        return result.getDouble(1);
    }

    @Override
    public int getQtyOnHand(String code) throws SQLException {
        String sql = "select qtyOnHand from item where code=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,code);
        ResultSet result = pstm.executeQuery();
        result.next();
        return result.getInt(1);
    }

    @Override
    public boolean placeOrder(ObservableList<PlaceOrderTm> orderItems, String customerId) throws SQLException {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String orderId = getNextOrderId();

        OrderDto order = new OrderDto(
                orderId,
                date,
                customerId
        );

        String sql = "INSERT INTO orders VALUES(?,?,?)";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,order.getId());
        pstm.setString(2,order.getDate());
        pstm.setString(3,order.getCustomerId());

        int rsl = pstm.executeUpdate();
        int rsl2 = 0;
        int rsl3 = 0;

        for (PlaceOrderTm orderItem:orderItems) {
            OrderDetailsDto orderDetail = new OrderDetailsDto(
                    orderId,
                    orderItem.getCode(),
                    orderItem.getQty(),
                    orderItem.getUnitPrice()
            );

            String sql2 = "INSERT INTO orderdetail VALUES(?,?,?,?)";
            PreparedStatement pstm2 = connection.prepareStatement(sql2);
            pstm2.setString(1,orderDetail.getOrderId());
            pstm2.setString(2,orderDetail.getItemCode());
            pstm2.setInt(3,orderDetail.getQty());
            pstm2.setDouble(4,orderDetail.getUnitPrice());
            rsl2 += pstm2.executeUpdate();

            String sql3 = "UPDATE item SET qtyOnHand = qtyOnHand-? WHERE code=?";
            PreparedStatement pstm3 = connection.prepareStatement(sql3);
            pstm3.setInt(1,orderDetail.getQty());
            pstm3.setString(2,orderDetail.getItemCode());
            rsl3 += pstm3.executeUpdate();


        }

        return (rsl+rsl2+rsl3) == (orderItems.size()*2+1);
    }

    private String getNextOrderId() throws SQLException {
        String sql = "SELECT id FROM orders ORDER BY id DESC LIMIT 1";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
        result.next();
        int orderId = Integer.parseInt(result.getString(1).substring(1,4));

        return "D" + String.format("%03d", ++orderId);
    }
}
