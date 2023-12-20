package dao.custom.impl;

import dao.util.CrudUtil;
import db.DBConnection;
import dao.custom.OrderDetailDao;
import entity.OrderDetail;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailDao {
    @Override
    public boolean saveAll(List<OrderDetail> list) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        for (OrderDetail entity:list) {
            if (!(Boolean) CrudUtil.execute(sql,entity.getOrderId(),entity.getItemCode(),entity.getQty(),entity.getUnitPrice())){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean save(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(OrderDetail entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<OrderDetail> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }
}
