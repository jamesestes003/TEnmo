package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestUserService implements UserService {

    @Autowired
    private UserDao userdao;
    @Autowired
    private AccountDao accountdao;
    @Autowired
    private TransferDao transferDao;

    public RestUserService(UserDao userdao,AccountDao accountdao,TransferDao transferDao){
        this.userdao=userdao;
        this.accountdao=accountdao;
        this.transferDao=transferDao;
    }
    @Override
    public Account getBalanceByUser(User user) {
        return accountdao.getAccountByUser(user);
    }

    @Override
    public List<User> getRegisteredUser() {
        return userdao.getUsers();
    }

    @Override
    public Transfer sendTeBucks(Transfer transfer) {
        Transfer newTransfer=transferDao.createTransfer(transfer);
        accountdao.updateAccountUserFrom(transfer);
        accountdao.updateAccountUserTo(transfer);
        return newTransfer;
    }

    @Override
    public List<Transfer> getAllTransferSent(User user) {
        return transferDao.getAllTransferSent(user);
    }

    @Override
    public List<Transfer> getAllTransferReceived(User user) {
        return transferDao.getAllTransferReceived(user);
    }

    @Override
    public Transfer requestTeBucks(Transfer transfer) {
        Transfer newTransfer=transferDao.createTransfer(transfer);
        return newTransfer;
    }

    @Override
    public List<Transfer> getAllPendingRequestWhenSender(int userId) {
        return transferDao.getPendingTransfersByUserIdWhenSender(userId);
    }

    @Override
    public List<Transfer> getAllPendingRequestWhenReceiver(int userId) {
        return transferDao.getPendingTransfersByUserIdWhenReceiver(userId);
    }

    @Override
    public Transfer updateTransferStatusAndUserAccount(Transfer transfer) {
        TransferStatus newTransferStatus=new TransferStatus();
        newTransferStatus.setTransferStatusId(2);
        transfer.setTransferStatus(newTransferStatus);
        Transfer updateTransfer=transferDao.updateTransfer(transfer);
        if (updateTransfer.getTransferStatus().getTransferStatusId()==2){
            accountdao.updateAccountUserFrom(transfer);
            accountdao.updateAccountUserTo(transfer);
        }
        return updateTransfer;
    }

    @Override
    public Transfer updateTransferStatus(Transfer transfer) {
        TransferStatus newTransferStatus=new TransferStatus();
        newTransferStatus.setTransferStatusId(3);
        transfer.setTransferStatus(newTransferStatus);
        Transfer updateTransfer=transferDao.updateTransfer(transfer);
        return updateTransfer;
    }


}
