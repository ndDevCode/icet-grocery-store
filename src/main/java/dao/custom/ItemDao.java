package dao.custom;

import dao.CrudDao;
import dto.ItemDto;
import entity.Item;
import entity.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public interface ItemDao extends CrudDao<Item> {
    boolean updateAll(List<OrderDetail> list) throws SQLException, ClassNotFoundException;
    ItemDto searchItem(String search);
}
