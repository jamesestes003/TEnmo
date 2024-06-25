package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.*;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserService {

    private final String baseUrl;
    private AuthenticatedUser authenticatedUser;
    private final RestTemplate restTemplate = new RestTemplate();
    private TransfertType transfertType;
    private TransferStatus transferStatus;

    public UserService(String baseUrl, AuthenticatedUser user){

        this.baseUrl = baseUrl;
        this.authenticatedUser=user;
    }

    public Account getAuthenticatedUserBalance(){
        Account account=null;
        if(authenticatedUser != null){
           try {
               HttpEntity<User> userEntity=createUserEntity(authenticatedUser);
               ResponseEntity<Account> responseEntity=restTemplate.exchange(baseUrl+"user", HttpMethod.POST,userEntity,Account.class);
               account=responseEntity.getBody();
           }catch (RestClientResponseException | ResourceAccessException e){
               BasicLogger.log(e.getMessage());
           }
        }
        return account;
    }

    public User[] getRegisteredUser(){
        User[] userList=null;
        if(authenticatedUser != null){
            try {
                ResponseEntity<User[]> responseEntity=restTemplate.exchange(baseUrl+"user", HttpMethod.GET,makeAuthenEntity(authenticatedUser),User[].class);
                userList=responseEntity.getBody();
            }catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }
        }
        return userList;
    }


    public void printUserBalance(){
        System.out.println("\n CURRENT BALANCE");
        System.out.println("----------------");
        System.out.println("User:"+authenticatedUser.getUser().getUsername());
        System.out.println("Your Current account balance is :"+getAuthenticatedUserBalance().getCurrentBalance()+" bucks");
    }

    public void printUserList(){
        String id="ID";
        String name="Name";
        if(getRegisteredUser()!=null){
            System.out.println("\n ------------------------------------\n");
            System.out.println("Users\n");
            System.out.printf("%-14s%s\n",id,name);
            System.out.println("----------------------------------------");
            for(User user:getRegisteredUser()){
                System.out.printf("%-14d%s\n",user.getId(),user.getUsername());
            }
            System.out.println("--------------");
        }
    }

    private User getUserById(int id){
        for(var user:getRegisteredUser()){
            if(user.getId()==id){
                return user;
            }
        }
        return null;
    }

    public void printTransferSentAndReceived(){
        String id="ID";
        String fromTo="From/To";
        String amount="Amount";
        String to="To";
        String from="From";
        String currency="$ ";
        if(getAllTransferSent()!=null){
            System.out.println("\n ------------------------------------\n");
            System.out.println("Transfers\n");
            System.out.printf("%-14s%-14s%s\n",id,fromTo,amount);
            System.out.println("----------------------------------------");
            for(var transfer:getAllTransferSent()){
                String nameUserTO =transfer.getAccountTo().getUser().getUsername();
                System.out.printf("%-14d%s:%-14s%s%s\n",transfer.getTransferId(),to,nameUserTO,currency,transfer.getAmount());
            }
            for(var transfer:getAllTransferReceived()){
                String nameUserFrom =transfer.getAccountFrom().getUser().getUsername();
                System.out.printf("%-14d%s:%-14s%s%s\n",transfer.getTransferId(),from,nameUserFrom,currency,transfer.getAmount());
            }
            System.out.println("--------------");


        }
    }

    public void printTransferDetails(Transfer transfer){
        if(getAllTransferSent()!=null){
            System.out.println("\n ------------------------------------\n");
            System.out.println("Transfer Details");
            System.out.println("----------------------------------------");
            System.out.println("Id:"+transfer.getTransferId());
            System.out.println("From:"+transfer.getAccountFrom().getUser().getUsername());
            System.out.println("To:"+transfer.getAccountTo().getUser().getUsername());
            System.out.println("Type:"+transfer.getTransferType().getName());
            System.out.println("Status:"+transfer.getTransferStatus().getName());
            System.out.println("Amount:"+transfer.getAmount());
        }
    }

    public Transfer sendTeBucks(User fromUser, User toUser, BigDecimal amount){

        String url = baseUrl + "/transfer";

        Transfer sendTransfer = new Transfer();
        Transfer returnTransferFromServer=null;

        transfertType=new TransfertType();
        transfertType.setTransfertTypeId(2);
        transferStatus=new TransferStatus();
        transferStatus.setTransferStatusId(2);
        Account accountTo = new Account();
        accountTo.setUser(toUser);

        sendTransfer.setTransferType(transfertType);
        sendTransfer.setTransferStatus(transferStatus);
        sendTransfer.setAccountFrom(getAuthenticatedUserBalance());
        sendTransfer.setAccountTo(accountTo);
        sendTransfer.setAmount(amount);

        if(authenticatedUser != null){
            try {
                HttpEntity<Transfer> transferEntity=createTransferEntity(authenticatedUser,sendTransfer);
                ResponseEntity<Transfer> responseEntity=restTemplate.exchange(url, HttpMethod.POST,transferEntity,Transfer.class);
                returnTransferFromServer=responseEntity.getBody();
            }catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }
        }
        return returnTransferFromServer;

    }

    public Transfer[] getAllTransferSent(){
        String url = baseUrl + "/transfer";
        Transfer[] transferSent=null;
        if(authenticatedUser != null){
            try {
                HttpEntity<User> userEntity=createUserEntity(authenticatedUser);
                ResponseEntity<Transfer[]> responseEntity=restTemplate.exchange(url+"/transferSent", HttpMethod.POST,userEntity,Transfer[].class);
                transferSent=responseEntity.getBody();
            }catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }
        }
        return transferSent;
    }

    public Transfer[] getAllTransferReceived(){
        String url = baseUrl + "/transfer";
        Transfer[] transferReceived=null;
        if(authenticatedUser != null){
            try {
                HttpEntity<User> userEntity=createUserEntity(authenticatedUser);
                ResponseEntity<Transfer[]> responseEntity=restTemplate.exchange(url+"/transferReceived", HttpMethod.POST,userEntity,Transfer[].class);
                transferReceived=responseEntity.getBody();
            }catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }
        }
        return transferReceived;
    }

    public void requestTeBucks(User userToRequestFrom, BigDecimal amount){
        String url = baseUrl + "/transfer/request";

        if (authenticatedUser != null){
            try {
                Transfer requestTransfer = new Transfer();
                requestTransfer.setAccountFrom(new Account(0, userToRequestFrom, BigDecimal.ZERO));
                requestTransfer.setAccountTo(new Account(0, authenticatedUser.getUser(), BigDecimal.ZERO));
                requestTransfer.setAmount(amount);

                TransferStatus pendingStatus = new TransferStatus();
                pendingStatus.setTransferStatusId(1);
                pendingStatus.setName("Pending");
                requestTransfer.setTransferStatus(pendingStatus);

                TransfertType requestType = new TransfertType();
                requestType.setTransfertTypeId(1);
                requestType.setName("Request");
                requestTransfer.setTransferType(requestType);

                HttpEntity<Transfer> transferEntity = createTransferEntity(authenticatedUser, requestTransfer);
                ResponseEntity<Transfer> responseEntity = restTemplate.exchange(url, HttpMethod.POST, transferEntity, Transfer.class);

                if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                    System.out.println("Transfer request sent successfully.");
                } else {
                    System.out.println("Failed to send transfer request.");
                }

            }catch (RestClientResponseException | ResourceAccessException e){
                BasicLogger.log(e.getMessage());
            }

        }
    }


    public List<Transfer> getPendingTransferRequestsWhenSender() {
        String url = baseUrl + "/transfer/pending1/";
        List<Transfer> pendingTransfers = new ArrayList<>();
        if (authenticatedUser != null) {
            try {
                int currentUserId = authenticatedUser.getUser().getId();
                HttpEntity<Void> entity = makeAuthenEntity(authenticatedUser);
                ResponseEntity<Transfer[]> response = restTemplate.exchange(url + currentUserId, HttpMethod.GET, makeAuthenEntity(authenticatedUser), Transfer[].class);
                Transfer[] transfers = response.getBody();
                if (transfers != null) {
                    pendingTransfers = Arrays.asList(transfers);
                }
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }
        return pendingTransfers;
    }

    public List<Transfer> getPendingTransferRequestsWhenReceiver() {
        String url = baseUrl + "/transfer/pending2/";
        List<Transfer> pendingTransfers = new ArrayList<>();
        if (authenticatedUser != null) {
            try {
                int currentUserId = authenticatedUser.getUser().getId();
                HttpEntity<Void> entity = makeAuthenEntity(authenticatedUser);
                ResponseEntity<Transfer[]> response = restTemplate.exchange(url + currentUserId, HttpMethod.GET, makeAuthenEntity(authenticatedUser), Transfer[].class);
                Transfer[] transfers = response.getBody();
                if (transfers != null) {
                    pendingTransfers = Arrays.asList(transfers);
                }
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }
        return pendingTransfers;
    }

    public void printPendingTransfer(){
        String id="ID";
        String destination="From/To";
        String to="To";
        String from="From";
        String amount="Amount";
        String currency="$ ";
        List<Transfer> pendingTransferListWhenSender=getPendingTransferRequestsWhenSender();
        List<Transfer> pendingTransferListWhenReceiver=getPendingTransferRequestsWhenReceiver();
        if(pendingTransferListWhenSender!=null){
            System.out.println("\n ------------------------------------\n");
            System.out.println("Pending transfers\n");
            System.out.printf("%-14s%-14s%s\n",id,destination,amount);
            System.out.println("----------------------------------------");
            for(var transfer:pendingTransferListWhenSender){
                String nameUserTO =transfer.getAccountTo().getUser().getUsername();
                System.out.printf("%-14d%s:%-14s%s%s\n",transfer.getTransferId(),to,nameUserTO,currency,transfer.getAmount());
            }
            for(var transfer:getPendingTransferRequestsWhenReceiver()){
                String nameUserFrom =transfer.getAccountFrom().getUser().getUsername();
                System.out.printf("%-14d%s:%-14s%s%s\n",transfer.getTransferId(),from,nameUserFrom,currency,transfer.getAmount());
            }
            System.out.println("--------------");
        }else{
            System.out.println("no pending request for you!");
        }
    }

    public void updatePendingRequest(Transfer updateTransfer,int option){
        switch (option){
            //update transfer status from pending to approved
            //update account user from and user to from the transfer amount
            case 1:
                updateTransferStatusAndUserAccount(updateTransfer,option);
                break;
            //update transfer status from pending to Reject
            //No account update
            case 2:
                updateTransferStatus(updateTransfer,option);
                break;
            case 0:
                System.out.println("action to perform:nothing");
                break;
            default:
                System.out.println("Please choice the right option!");
        }
    }

    private void updateTransferStatusAndUserAccount(Transfer updateTransfer, int option){
        String url = baseUrl + "/transfer/pending/";
        if (authenticatedUser != null) {
            try {
                ResponseEntity<Transfer> response = restTemplate.exchange(url+option, HttpMethod.PUT, createTransferEntity(authenticatedUser, updateTransfer), Transfer.class);
                Transfer transfer = response.getBody();
                if (transfer != null) {
                    System.out.println("pending transfer successfully processing! ");
                }
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }
    }
    private void updateTransferStatus(Transfer updateTransfer, int option){
        String url = baseUrl + "/transfer/pending/";
        if (authenticatedUser != null) {
            try {
                ResponseEntity<Transfer> response = restTemplate.exchange(url+option, HttpMethod.PUT, createTransferEntity(authenticatedUser, updateTransfer), Transfer.class);
                Transfer transfer = response.getBody();
                if (transfer != null) {
                    System.out.println("pending transfer status Id successfully updating to Reject! ");
                }
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }
    }


    private HttpEntity<User> createUserEntity(AuthenticatedUser authenticatedUser){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(authenticatedUser.getUser(),headers);
    }

    private HttpEntity<Transfer> createTransferEntity(AuthenticatedUser authenticatedUser, Transfer transfer){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(transfer,headers);
    }

    private HttpEntity<Void> makeAuthenEntity(AuthenticatedUser authenticatedUser){
        HttpHeaders headers=new HttpHeaders();
        headers.setBearerAuth(authenticatedUser.getToken());
        return new HttpEntity<>(headers);
    }

}
