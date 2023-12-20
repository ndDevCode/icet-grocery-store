package dao.custom.impl;

import dao.util.CrudUtil;
import dto.OrderDto;
import dao.custom.ItemDao;
import dao.custom.OrderDetailDao;
import dao.custom.OrderDao;
import entity.OrderDetail;
import entity.Orders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    private final OrderDetailDao orderDetailDao = new OrderDetailDaoImpl();
    private final ItemDao itemDao = new ItemDaoImpl();

    @Override
    public boolean save(Orders entity, List<OrderDetail> orderDetailList) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO orders VALUES(?,?,?)";

        if (CrudUtil.execute(sql, entity.getId(), entity.getDate(), entity.getCustomerId())) {
            boolean isDetailsSaved = orderDetailDao.saveAll(orderDetailList);
            boolean isItemsUpdated = itemDao.updateAll(orderDetailList);
            if (isDetailsSaved && isItemsUpdated) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Orders getLastOrder() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        ResultSet resultSet = CrudUtil.execute(sql);
        if (resultSet.next()) {
            return new Orders(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
            );
        }
        return null;
    }

    @Override
    public boolean save(Object entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Object entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List getAll() throws SQLException, ClassNotFoundException {
        return null;
    }
}
