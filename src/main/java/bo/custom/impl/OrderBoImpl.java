package bo.custom.impl;

import bo.custom.OrderBo;
import dao.DaoFactory;
import dao.custom.OrderDao;
import dao.util.DaoType;
import dto.OrderDto;

import java.sql.SQLException;

public class OrderBoImpl implements OrderBo {
    private final OrderDao orderDao = DaoFactory.getInstance().getDao(DaoType.ORDER);

    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        return orderDao.save(dto);
    }

    @Override
    public String generateId() throws SQLException, ClassNotFoundException {
        String id = null;
        try {
            id = orderDao.getLastOrder().getId();
        } catch (NullPointerException e) {
            return "D001";
        }
        int num = Integer.parseInt(id.split("[D]")[1]);
        num++;
        id = String.format("D%03d", num);

        return id;
    }
}
