package dao.custom.impl;

import dao.util.CrudUtil;
import dao.util.HibernateUtil;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dao.custom.ItemDao;
import dao.custom.OrderDetailDao;
import dao.custom.OrderDao;
import entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    @Override
    public boolean save(OrderDto dto) throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();
        Orders order = new Orders(
                dto.getId(),
                dto.getDate()
        );
        order.setCustomer(session.find(Customer.class,dto.getCustomerId()));
        session.save(order);

        List<OrderDetailsDto> list = dto.getList(); //dto type

        for (OrderDetailsDto detailDto:list) {
            OrderDetail orderDetail = new OrderDetail(
                    new OrderDetailsKey(detailDto.getOrderId(), detailDto.getItemCode()),
                    session.find(Item.class, detailDto.getItemCode()),
                    order,
                    detailDto.getQty(),
                    detailDto.getUnitPrice()
            );
            session.save(orderDetail);
        }

        transaction.commit();
        session.close();
        return true;
    }

    @Override
    public Orders getLastOrder() throws SQLException, ClassNotFoundException {
        Session session = HibernateUtil.getSession();
        Query query = session.createQuery("FROM Orders o ORDER BY o.id DESC");
        query.setMaxResults(1);
        try {
            return (Orders) query.getSingleResult();
        }catch (NoResultException e){
            return null;
        }

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
