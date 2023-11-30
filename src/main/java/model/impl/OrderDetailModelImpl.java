package model.impl;

import db.DBConnection;
import dto.OrderDetailsDto;
import dto.OrderDto;
import model.OrderDetailModel;
import model.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailModelImpl implements OrderDetailModel {
    @Override
    public boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        for (OrderDetailsDto dto:list) {
            pstm.setString(1,dto.getOrderId());
            pstm.setString(2,dto.getItemCode());
            pstm.setInt(3,dto.getQty());
            pstm.setDouble(4,dto.getUnitPrice());
            if (!(pstm.executeUpdate()>0)){
                return false;
            }
        }
        return true;
    }
}
