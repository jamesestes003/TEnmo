package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public interface AccountDao {

    Account getAccountByUser(User user);
    Account getAccountByAccountId(int accountId);
    void updateAccountUserFrom(Transfer transfer);
    void updateAccountUserTo(Transfer transfer);
}
