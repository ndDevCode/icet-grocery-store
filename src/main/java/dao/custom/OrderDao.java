package dao.custom;

import dao.CrudDao;
import dto.OrderDto;
import entity.OrderDetail;
import entity.Orders;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao extends CrudDao {
    boolean save(OrderDto dto) throws SQLException, ClassNotFoundException;
    Orders getLastOrder() throws SQLException, ClassNotFoundException;
}
