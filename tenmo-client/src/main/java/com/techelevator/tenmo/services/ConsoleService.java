package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public User promptForUserId(String prompt, User[] userList){
        int userID=promptForInt(prompt);
        for (User user:userList){
            if (user.getId()==userID){
                return user;
            }
        }
        return null;
    }
    public Transfer promptForTransferId(String prompt, Transfer[] allTransferSent, Transfer[] allTransferReceived){
        int transferId=promptForInt(prompt);
        if(transferId==0){
            return null;
        }else{
            if(allTransferSent.length!=0 || allTransferReceived.length!=0){

                for (Transfer transfer:allTransferSent){
                    if (transfer.getTransferId()==transferId){
                        return transfer;
                    }
                }
                for (Transfer transfer:allTransferReceived){
                    if (transfer.getTransferId()==transferId){
                        return transfer;
                    }
                }
            }
        }

        return null;
    }

    public Transfer promptForPendingTransferId(String prompt, List<Transfer> allPendingTransferWhenSender){
        int transferId=promptForInt(prompt);
        if(allPendingTransferWhenSender.size()!=0){
            for (Transfer transfer:allPendingTransferWhenSender){
                if (transfer.getTransferId()==transferId){
                    return transfer;
                }
            }
        }
        return null;
    }

    public int promptForOption(String prompt) {
        System.out.println("1:Approve");
        System.out.println("2:Reject");
        System.out.println("0:Don't approve or reject");
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }



}
