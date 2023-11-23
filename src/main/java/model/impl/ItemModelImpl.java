package model.impl;

import db.DBConnection;
import dto.CustomerDto;
import dto.ItemDto;
import model.CustomerModel;
import model.ItemModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemModelImpl implements ItemModel {

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
    public boolean saveItem(ItemDto dto) throws SQLException {
        String sql = "INSERT INTO item VALUES(?,?,?,?)";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(1, dto.getCode());
        pstm.setString(2, dto.getDescription());
        pstm.setDouble(3, dto.getUnitPrice());
        pstm.setInt(4, dto.getQtyOnHand());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException {
        String sql = "UPDATE item SET description=?,unitPrice=?,qtyOnHand=? WHERE code=?";
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setString(4, dto.getCode());
        pstm.setString(1, dto.getDescription());
        pstm.setDouble(2, dto.getUnitPrice());
        pstm.setInt(3, dto.getQtyOnHand());

        return pstm.executeUpdate()>0;
    }

    @Override
    public boolean deleteItem(String itemCode) throws SQLException {

        String sql = "DELETE FROM item WHERE code=?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1,itemCode);

        return pstm.executeUpdate()>0;
    }

    @Override
    public List<ItemDto> getAllItem() throws SQLException {

        String sql = "SELECT * FROM item";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet result = pstm.executeQuery();
        List<ItemDto> itemDtoList = new ArrayList<>();

        while (result.next()){
            ItemDto item = new ItemDto(
                    result.getString(1),
                    result.getString(2),
                    result.getDouble(3),
                    result.getInt(4)
                    );

            itemDtoList.add(item);
        }
        return itemDtoList;
    }

    @Override
    public ItemDto searchItem(String search) {
        return null;
    }
}
