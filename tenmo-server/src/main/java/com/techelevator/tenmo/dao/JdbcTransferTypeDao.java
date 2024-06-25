package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransferTypeDao implements TransferTypeDao {
    @Autowired
    private final JdbcTemplate jdbcTemplate;



    public JdbcTransferTypeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TransferType getTransferTypeById(int id) {
        String sql = "SELECT transfer_type_id, transfer_type_desc FROM transfer_type where transfer_type_id=?";
        TransferType transferType=null;
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql,id);
            if (result.next()) {
               transferType = mapRowToTransferType(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transferType;
    }

    private TransferType mapRowToTransferType(SqlRowSet result){
        TransferType transferType=new TransferType();
        transferType.setTransfertTypeId(result.getInt("transfer_type_id"));
        transferType.setName(result.getString("transfer_type_desc"));
        return transferType;
    }
}
