package dao.custom;

import dao.CrudDao;
import entity.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDao extends CrudDao<OrderDetail> {
    boolean saveAll(List<OrderDetail> list) throws SQLException, ClassNotFoundException;
}
