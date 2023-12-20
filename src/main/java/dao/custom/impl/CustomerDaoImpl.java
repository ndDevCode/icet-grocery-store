package dao.custom.impl;

import dao.util.CrudUtil;
import dto.CustomerDto;
import dao.custom.CustomerDao;
import entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO customer VALUES(?,?,?,?)";
        return CrudUtil.execute(sql,entity.getId(),entity.getName(),entity.getAddress(),entity.getSalary());
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE customer SET name=?,address=?,salary=? WHERE id=?";
        return CrudUtil.execute(sql,entity.getName(),entity.getAddress(),entity.getSalary(),entity.getId());
    }

    @Override
    public boolean delete(String customerId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM Customer WHERE id=?";
        return CrudUtil.execute(sql, customerId);
    }

    @Override
    public List<Customer> getAll() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM customer";
        ResultSet result = CrudUtil.execute(sql);
        List<Customer> customerList = new ArrayList<>();

        while (result.next()){
            Customer customer = new Customer(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getDouble(4)
                    );
            customerList.add(customer);
        }

        return customerList;
    }

    @Override
    public CustomerDto searchCustomer(String search) {
        return null;
    }
}
