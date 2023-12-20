package dao.custom.impl;

import dao.util.CrudUtil;
import dto.ItemDto;
import dao.custom.ItemDao;
import entity.Item;
import entity.OrderDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO item VALUES(?,?,?,?)";
        return CrudUtil.execute(sql, entity.getCode(),entity.getDescription(),entity.getUnitPrice(),entity.getQtyOnHand());
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET description=?,unitPrice=?,qtyOnHand=? WHERE code=?";
        return CrudUtil.execute(sql, entity.getDescription(), entity.getUnitPrice(),entity.getQtyOnHand(),entity.getCode());
    }

    @Override
    public boolean delete(String itemCode) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM item WHERE code=?";
        return CrudUtil.execute(sql, itemCode);
    }

    @Override
    public boolean updateAll(List<OrderDetail> list) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET qtyOnHand = qtyOnHand-? WHERE code=?";
        for (OrderDetail entity:list) {
            if (!(Boolean) CrudUtil.execute(sql,entity.getQty(),entity.getItemCode())){
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Item> getAll() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM item";
        ResultSet result = CrudUtil.execute(sql);
        List<Item> itemList = new ArrayList<>();

        while (result.next()){
            Item item = new Item(
                    result.getString(1),
                    result.getString(2),
                    result.getDouble(3),
                    result.getInt(4)
                    );

            itemList.add(item);
        }
        return itemList;
    }

    @Override
    public ItemDto searchItem(String search) {
        return null;
    }
}
