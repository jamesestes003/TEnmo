package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {
    private int accountId;
    private User user;
    private BigDecimal currentBalance;

    public Account(int accountId, User user, double balance){
        this.accountId=accountId;
        this.user=user;
        this.currentBalance=new BigDecimal(balance);
    }

    public Account() {
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
}
