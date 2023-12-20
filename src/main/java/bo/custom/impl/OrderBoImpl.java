package bo.custom.impl;

import bo.custom.OrderBo;
import dao.DaoFactory;
import dao.custom.OrderDao;
import dao.util.DaoType;
import dto.OrderDetailsDto;
import dto.OrderDto;
import entity.OrderDetail;
import entity.Orders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderBoImpl implements OrderBo {
    private final OrderDao orderDao = DaoFactory.getInstance().getDao(DaoType.ORDER);

    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (OrderDetailsDto orderDetail : dto.getList()){
            orderDetailList.add(new OrderDetail(
                    orderDetail.getOrderId(),
                    orderDetail.getItemCode(),
                    orderDetail.getQty(),
                    orderDetail.getUnitPrice()));
        }
        return orderDao.save(new Orders(dto.getId(),dto.getDate(),dto.getCustomerId()),orderDetailList);
    }

    @Override
    public String generateId() throws SQLException, ClassNotFoundException {
        String id = orderDao.getLastOrder().getId();
        if (id!=null){
            int num = Integer.parseInt(id.split("[D]")[1]);
            num++;
            id = String.format("D%03d",num);
        }
        return id;
    }
}
