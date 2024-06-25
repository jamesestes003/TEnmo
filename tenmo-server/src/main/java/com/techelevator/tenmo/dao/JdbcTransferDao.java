package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class JdbcTransferDao implements TransferDao {
    public final String TRANSFER_SELECT = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer";

    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;
    private TransferStatusDao transferStatusDao;
    private TransferTypeDao transferTypeDao;

    @Autowired
    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao, TransferStatusDao transferStatusDao,TransferTypeDao transferTypeDao ) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao=accountDao;
        this.transferStatusDao=transferStatusDao;
        this.transferTypeDao=transferTypeDao;
    }

    @Override
    public Transfer getTransferById(int transferId) {
        String sql = TRANSFER_SELECT + " WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        try {
            if (results.next()) {
                return mapRowToTransfer(results);
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return null;
    }

    @Override
    public Transfer updateTransfer(Transfer transfer) {
        String sql = "UPDATE transfer SET transfer_type_id = ?, transfer_status_id = ?, account_from = ?, account_to = ?, amount = ? WHERE transfer_id = ?";
        try {
            int numRowsAffected = jdbcTemplate.update(sql, transfer.getTransferType().getTransfertTypeId(), transfer.getTransferStatus().getTransferStatusId(), transfer.getAccountFrom().getAccountId(), transfer.getAccountTo().getAccountId(), transfer.getAmount(), transfer.getTransferId());
            if (numRowsAffected == 0) {
                throw new DaoException("Transfer not found");
            }
            return getTransferById(transfer.getTransferId());
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
    }

   /* @Override
    public int deleteTransferById(int transferId) {
        int rowsAffected = 0;
        String sql = "DELETE FROM transfers WHERE transfer_id = ?";
        try {
            rowsAffected = jdbcTemplate.update(sql, transferId);
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return rowsAffected;
    }*/

   /* @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = TRANSFER_SELECT + " WHERE account_from = ? OR account_to = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        try {
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return transfers;
    }*/

    @Override
    public List<Transfer> getPendingTransfersByUserIdWhenSender(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        int transferStatusId=1;
        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount from transfer t\n" +
                "inner join account a on a.account_id=t.account_from\n" +
                "inner join tenmo_user u on u.user_id=a.user_id\n" +
                "where u.user_id=? and transfer_status_id=?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, transferStatusId);
        try {
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return transfers;
    }

    @Override
    public List<Transfer> getPendingTransfersByUserIdWhenReceiver(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        int transferStatusId=1;
        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount from transfer t\n" +
                "inner join account a on a.account_id=t.account_to\n" +
                "inner join tenmo_user u on u.user_id=a.user_id\n" +
                "where u.user_id=? and transfer_status_id=?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, transferStatusId);
        try {
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return transfers;
    }


    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer returnTransfer=null;
        int newTransferId;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        try {
            int transferTypeId=transfer.getTransferType().getTransfertTypeId();
            int transferStatusId=transfer.getTransferStatus().getTransferStatusId();
            Account accountFrom=accountDao.getAccountByUser(transfer.getAccountFrom().getUser());
            Account accountTo=accountDao.getAccountByUser(transfer.getAccountTo().getUser());
            BigDecimal amount=transfer.getAmount();
           newTransferId=jdbcTemplate.queryForObject(sql, int.class, transferTypeId,transferStatusId,accountFrom.getAccountId(),accountTo.getAccountId(),amount);
            returnTransfer=getTransferById(newTransferId);
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return returnTransfer;
    }

    @Override
    public List<Transfer> getAllTransferReceived(User user) {
        List<Transfer> transfers = new ArrayList<>();
        int transferStatusId=2;
        String sql ="select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount  from transfer t\n" +
                "inner join account a on a.account_id=t.account_to\n" +
                "inner join tenmo_user u on u.user_id=a.user_id\n" +
                "where u.user_id=? and t.transfer_status_id=?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId(),transferStatusId);
        try {
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return transfers;
    }

    @Override
    public List<Transfer> getAllTransferSent(User user) {
        List<Transfer> transfers = new ArrayList<>();
        int transferStatusId=2;
        String sql ="select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount from transfer t\n" +
                "inner join account a on a.account_id=t.account_from\n" +
                "inner join tenmo_user u on u.user_id=a.user_id\n" +
                "where u.user_id=? and t.transfer_status_id=?";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, user.getId(),transferStatusId);
        try {
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return transfers;
    }

   /* public List<Transfer> getApprovedTransfersByAccountId(int accountId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = TRANSFER_SELECT + " WHERE transfer_status_id = 2 AND (account_from = ? OR account_to = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
        try {
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (DataAccessException e) {
            throw new DaoException(e.getMessage());
        }
        return transfers;
    }

    @Override
    public void updateAccountBalances(int senderUserId, int receiverUserId, BigDecimal amount) {
        try {
            ChainedTransactionManager transactionTemplate = null;
            TransactionStatus status = transactionTemplate.getTransaction(new DefaultTransactionDefinition());
            try {
                String updateSenderBalanceSql = "UPDATE account SET balance = balance - ? WHERE user_id = ?";
                jdbcTemplate.update(updateSenderBalanceSql, amount, senderUserId);

                String updateReceiverBalanceSql = "UPDATE account SET balance = balance + ? WHERE user_id = ?";
                jdbcTemplate.update(updateReceiverBalanceSql, amount, receiverUserId);

                transactionTemplate.commit(status);
            } catch (DataAccessException e) {
                transactionTemplate.rollback(status);
                throw new DaoException("Error updating account balances: " + e.getMessage());
            }
        } catch (TransactionException e) {
            throw new DaoException("Error starting transaction: " + e.getMessage());
        }
    }*/

    private Transfer mapRowToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        TransferType transferType=new TransferType();
        TransferStatus transferStatus=new TransferStatus();
        Account accountFrom=new Account();
        Account accountTo=new Account();

        transfer.setTransferId(results.getInt("transfer_id"));
        accountFrom.setAccountId(results.getInt("account_from"));
        accountFrom=accountDao.getAccountByAccountId(accountFrom.getAccountId());
        transfer.setAccountFrom(accountFrom);

        accountTo.setAccountId(results.getInt("account_to"));
        accountTo=accountDao.getAccountByAccountId(accountTo.getAccountId());
        transfer.setAccountTo(accountTo);

        transferStatus.setTransferStatusId(results.getInt("transfer_status_id"));
        transferStatus=transferStatusDao.getTransferStatusById(transferStatus.getTransferStatusId());
        transfer.setTransferStatus(transferStatus);

        transferType.setTransfertTypeId(results.getInt("transfer_type_id"));
        transferType=transferTypeDao.getTransferTypeById(transferType.getTransfertTypeId());
        transfer.setTransferType(transferType);

        transfer.setAmount(results.getBigDecimal("amount"));

        return transfer;
    }
}
