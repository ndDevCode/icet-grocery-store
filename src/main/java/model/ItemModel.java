package model;

import dto.CustomerDto;
import dto.ItemDto;

import java.sql.SQLException;
import java.util.List;

public interface ItemModel {
    boolean saveItem(ItemDto dto) throws SQLException;
    boolean updateItem(ItemDto dto) throws SQLException;
    boolean deleteItem(String itemCode) throws SQLException;
    List<ItemDto> getAllItem() throws SQLException;
    ItemDto searchItem(String search);
}
