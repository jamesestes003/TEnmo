package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.RestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private RestUserService restUserService;
    @Autowired
    public UserController(RestUserService restUserService){
        this.restUserService=restUserService;
    }

    //getUserBalance handler method to retrieve user's balance and send back to client
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Account getBalanceUser(@RequestBody User user){
        Account account=restUserService.getBalanceByUser(user);
        return account;
    }

    //getAllRegisteredUser handler method to retrieve list of registered user and send back to client
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<User> getAllRegisteredUser(){
        return restUserService.getRegisteredUser();
    }

}
