package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferStatusDao implements TransferStatusDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferStatusDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public TransferStatus getTransferStatusById(int id) {
        String sql = "SELECT transfer_status_id, transfer_status_desc FROM transfer_status where transfer_status_id=?";
        TransferStatus transferStatus=null;
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql,id);
            if (result.next()) {
                transferStatus = mapRowToTransferStatus(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transferStatus;
    }

    private TransferStatus mapRowToTransferStatus(SqlRowSet result){
        TransferStatus transferStatus=new TransferStatus();
        transferStatus.setTransferStatusId(result.getInt("transfer_status_id"));
        transferStatus.setName(result.getString("transfer_status_desc"));
        return transferStatus;
    }
}
