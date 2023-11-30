package model.impl;

import db.DBConnection;
import dto.OrderDto;
import model.ItemModel;
import model.OrderDetailModel;
import model.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderModelImpl implements OrderModel {
    private OrderDetailModel orderDetailModel = new OrderDetailModelImpl();
    private ItemModel itemModel = new ItemModelImpl();
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
    public boolean saveOrder(OrderDto dto) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, dto.getId());
            pstm.setString(2, dto.getDate());
            pstm.setString(3, dto.getCustomerId());

            if (pstm.executeUpdate() > 0) {

                boolean isDetailsSaved = orderDetailModel.saveOrderDetails(dto.getList());
                boolean isItemsUpdated = itemModel.updateAllItems(dto.getList());
                if (isDetailsSaved && isItemsUpdated) {
                    connection.commit();
                    return true;
                }
            }
        }catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
            connection.rollback();
        }finally {
            connection.setAutoCommit(true);
        }
        return false;
    }

    @Override
    public OrderDto getLastOrder() throws SQLException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return new OrderDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    null
            );
        }
        return null;
    }
}
