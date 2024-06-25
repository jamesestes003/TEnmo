package com.techelevator.tenmo.service;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserService {

    Account getBalanceByUser(User user);
    List<User> getRegisteredUser();
    Transfer sendTeBucks(Transfer transfer);
    List<Transfer> getAllTransferSent(User user);
    List<Transfer> getAllTransferReceived(User user);
    Transfer requestTeBucks(Transfer transfer);
    List<Transfer> getAllPendingRequestWhenSender(int userId);
    List<Transfer> getAllPendingRequestWhenReceiver(int userId);
    Transfer updateTransferStatusAndUserAccount(Transfer transfer);
    Transfer updateTransferStatus(Transfer transfer);


}
