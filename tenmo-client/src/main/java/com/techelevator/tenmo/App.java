package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;
import java.util.List;

public class  App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private UserService userService=null;

    private Account currentBalance;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        userService=new UserService(API_BASE_URL,currentUser);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }


    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        userService.printUserBalance();
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        userService.printTransferSentAndReceived();
        Transfer transferToGetDetail=consoleService.promptForTransferId("Please enter transfer ID to view details (0 to cancel):", userService.getAllTransferSent(), userService.getAllTransferReceived());
        if (transferToGetDetail==null){}else{
            userService.printTransferDetails(transferToGetDetail);
        }

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        userService.printPendingTransfer();
        Transfer transferToUpdate = consoleService.promptForPendingTransferId("Please enter transfer ID to approve/reject (0 to cancel):",userService.getPendingTransferRequestsWhenSender());
        if(transferToUpdate!=null){
            int optionSelected=consoleService.promptForOption("Please choose an option:");
            userService.updatePendingRequest(transferToUpdate,optionSelected);
        }else{
            System.out.println("You can't process this pending transfer because you are receiver!");
        }
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		userService.printUserList();
        User userToSentTo=null;
        userToSentTo=consoleService.promptForUserId("Enter ID of user you are sending to (0 to cancel):", userService.getRegisteredUser());
        if(userToSentTo.getId()==currentUser.getUser().getId()){
            System.out.println("You can't send Te bucks to yourself!");
        }else{
            BigDecimal amount=consoleService.promptForBigDecimal("Enter amount:");
            BigDecimal zero=new BigDecimal(0);
            if(amount.compareTo(zero)<=0){
                System.out.println("You can't send zero or negative amount");
            } else if (amount.compareTo(userService.getAuthenticatedUserBalance().getCurrentBalance())>0) {
                System.out.println("You can't send more than you have");
            }else{
                    System.out.println("You wanna send TE Bucks to "+userToSentTo.getUsername()+" amount:"+amount);
                    Transfer transfer =userService.sendTeBucks(currentUser.getUser(),userToSentTo,amount);
                    if(transfer!=null){
                        System.out.println("send Request successfully and "+userToSentTo.getUsername()+" Received:"+transfer.getAmount()+" Bucks");
                    }else{
                    System.out.println("An error occurs!");
                }
            }
        }

	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        userService.printUserList();
        User userToRequestFrom = null;
        userToRequestFrom = consoleService.promptForUserId("Enter ID of user you are requesting from (0 to cancel):", userService.getRegisteredUser());
        if(userToRequestFrom.getId()==currentUser.getUser().getId()){
            System.out.println("You can't Request Te bucks to yourself!");
        }else{
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount:");
            BigDecimal zero=new BigDecimal(0);
            if(amount.compareTo(zero)<=0){
                System.out.println("You can't Request zero or negative amount");
            }else{
                System.out.println("You want to request TE Bucks from " + userToRequestFrom.getUsername() + " amount:" + amount);
                userService.requestTeBucks(userToRequestFrom, amount);
            }
        }
	}
}
