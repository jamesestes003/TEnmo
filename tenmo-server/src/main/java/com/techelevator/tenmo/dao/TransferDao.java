package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    Transfer createTransfer(Transfer transfer);
    List<Transfer> getAllTransferReceived(User user);
    List<Transfer> getAllTransferSent(User user);
    List<Transfer> getPendingTransfersByUserIdWhenSender(int userId);
    List<Transfer> getPendingTransfersByUserIdWhenReceiver(int userId);
    Transfer getTransferById(int transferId);
    Transfer updateTransfer(Transfer transfer);

   /* int deleteTransferById(int transferId);*/

   /* List<Transfer> getTransfersByAccountId(int accountId);*/

   /* List<Transfer> getApprovedTransfersByAccountId(int i);*/

    /*void updateAccountBalances(int fromId, int toId, BigDecimal amount);*/
}
