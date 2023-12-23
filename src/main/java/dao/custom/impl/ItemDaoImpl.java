package dao.custom.impl;

import dao.util.CrudUtil;
import dao.util.HibernateUtil;
import dto.ItemDto;
import dao.custom.ItemDao;
import entity.Customer;
import entity.Item;
import entity.OrderDetail;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Item item = session.find(Item.class, entity.getCode());
        item.setDescription(entity.getDescription());
        item.setUnitPrice(entity.getUnitPrice());
        item.setQtyOnHand(entity.getQtyOnHand());
        session.save(item);
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean delete(String itemCode) {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.find(Item.class,itemCode));
        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public boolean updateAll(List<OrderDetail> list) throws SQLException, ClassNotFoundException {
//        String sql = "UPDATE item SET qtyOnHand = qtyOnHand-? WHERE code=?";
//        for (OrderDetail entity:list) {
//            if (!(Boolean) CrudUtil.execute(sql,entity.getQty(),entity.getItemCode())){
//                return false;
//            }
//        }
        return true;
    }

    @Override
    public List<Item> getAll() throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Query query = session.createQuery("FROM Item");
        List<Item> list = query.list();
        session.close();
        return list;
    }

    @Override
    public ItemDto searchItem(String search) {
        return null;
    }
}
