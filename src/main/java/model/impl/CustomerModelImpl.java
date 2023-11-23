package model.impl;

import db.DBConnection;
import dto.CustomerDto;
import model.CustomerModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerModelImpl implements CustomerModel {

    private final static Connection connection;

    static {
        //----Initialize the db Connection
        try {
            connection = DBConnection.getInstance().getConnection();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean saveCustomer(CustomerDto dto) throws SQLException {
        String sql = "INSERT INTO customer VALUES(?,?,?,?)";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, dto.getId());
        pstm.setString(2, dto.getName());
        pstm.setString(3, dto.getAddress());
        pstm.setDouble(4,dto.getSalary());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean updateCustomer(CustomerDto dto) throws SQLException {
        String sql = "UPDATE customer SET name=?,address=?,salary=? WHERE id=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,dto.getName());
        pstm.setString(2,dto.getAddress());
        pstm.setDouble(3,dto.getSalary());
        pstm.setString(4, dto.getId());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean deleteCustomer(String customerId) throws SQLException {

        String sql = "DELETE FROM Customer WHERE id=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,customerId);

        return pstm.executeUpdate()>0;
    }

    @Override
    public List<CustomerDto> getAllCustomer() throws SQLException {

        String sql = "SELECT * FROM customer";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
        List<CustomerDto> customerDtoList = new ArrayList<>();

        while (result.next()){
            CustomerDto customer = new CustomerDto(
                    result.getString(1),
                    result.getString(2),
                    result.getString(3),
                    result.getDouble(4)
                    );

            customerDtoList.add(customer);
        }
        return customerDtoList;
    }

    @Override
    public CustomerDto searchCustomer(String search) {
        return null;
    }
}
