package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    @Autowired
    public JdbcAccountDao(JdbcTemplate jdbcTemplate, UserDao userDao) {

        this.jdbcTemplate = jdbcTemplate;
        this.userDao=userDao;
    }

    @Override
    public Account getAccountByUser(User user) {
        String sql="select account_id, user_id, balance from account where user_id=?";
        Account account=null;

        try{
            SqlRowSet result=jdbcTemplate.queryForRowSet(sql,user.getId());
            if (result.next()){
                account=mapRowToAccount(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to database:"+e);
        }
        return account;
    }

    @Override
    public Account getAccountByAccountId(int accountId) {
        String sql="select account_id, user_id, balance from account where account_id=?";
        Account account=null;

        try{
            SqlRowSet result=jdbcTemplate.queryForRowSet(sql,accountId);
            if (result.next()){
                account=mapRowToAccount(result);
            }
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to database:"+e);
        }
        return account;
    }

    @Override
    public void updateAccountUserFrom(Transfer transfer) {
        String updateUserFromAccountSql="UPDATE account SET balance = ?  WHERE user_id = ?";
        Account currentAccountFrom=getAccountByUser(transfer.getAccountFrom().getUser());
        BigDecimal newBalanceFrom=currentAccountFrom.getCurrentBalance().subtract(transfer.getAmount());

        Account newAccountFrom=new Account();
        newAccountFrom.setUser(transfer.getAccountFrom().getUser());
        newAccountFrom.setCurrentBalance(newBalanceFrom);

        try {
            int rowsAffected = jdbcTemplate.update(updateUserFromAccountSql, newAccountFrom.getCurrentBalance(),newAccountFrom.getUser().getId());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }


    }

    @Override
    public void updateAccountUserTo(Transfer transfer) {
        String updateUserToAccountSql="UPDATE account SET balance = ?  WHERE user_id = ?";
        Account currentAccountTo=getAccountByUser(transfer.getAccountTo().getUser());

        BigDecimal newBalanceTo=currentAccountTo.getCurrentBalance().add(transfer.getAmount());
        Account newAccountTo=new Account();
        newAccountTo.setUser(transfer.getAccountTo().getUser());
        newAccountTo.setCurrentBalance(newBalanceTo);

        try {
            int rowsAffected = jdbcTemplate.update(updateUserToAccountSql, newAccountTo.getCurrentBalance(),newAccountTo.getUser().getId());
            if (rowsAffected == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

    }

    private Account mapRowToAccount(SqlRowSet row){
        Account account=new Account();
        account.setAccountId(row.getInt("account_id"));
        User user=userDao.getUserById(row.getInt("user_id"));
        account.setUser(user);
        account.setCurrentBalance(row.getBigDecimal("balance"));
        return account;
    }
}
