package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.RestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/transfer")
public class TransferController {

    private TransferDao transferDao;
    private RestUserService restUserService;
    @Autowired
    public TransferController(TransferDao transferDao,RestUserService restUserService) {
        this.transferDao = transferDao;
        this.restUserService=restUserService;
    }

   /* @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Transfer> getAllTransfers() {
        try {
            return transferDao.getTransfersByAccountId(1);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(path = "/{transferId}", method = RequestMethod.GET)
    public Transfer get(@PathVariable int transferId) {
        return transferDao.getTransferById(transferId);
    }*/

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "", method = RequestMethod.POST)
    public Transfer create(@RequestBody Transfer transfer) {
        try {
            return restUserService.sendTeBucks(transfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public Transfer createRequest(@RequestBody Transfer transfer) {
        try {
            return restUserService.requestTeBucks(transfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/transferSent")
    public List<Transfer> getAllTransferSent(@RequestBody User user){
        return restUserService.getAllTransferSent(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/transferReceived")
    public List<Transfer> getAllTransferReceived(@RequestBody User user){
        return restUserService.getAllTransferReceived(user);
    }
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pending1/{currentUserId}")
    public List<Transfer> getAllPendingRequestWhenSender(@PathVariable(name = "currentUserId") int userId){
        return restUserService.getAllPendingRequestWhenSender(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pending2/{currentUserId}")
    public List<Transfer> getAllPendingRequestWhenReceiver(@PathVariable(name = "currentUserId") int userId){
        return restUserService.getAllPendingRequestWhenReceiver(userId);
    }
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/pending/{option}")
    public Transfer updatePendingRequest(@PathVariable int option, @RequestBody Transfer updateTransfer){
        switch (option){
            case 1:
                return restUserService.updateTransferStatusAndUserAccount(updateTransfer);
                //break;
            case 2:
                return restUserService.updateTransferStatus(updateTransfer);
                //break;
        }

        return null;
    }


   /* @RequestMapping(path = "/{transferId}", method = RequestMethod.DELETE)
    public ResponseEntity<Transfer> delete(@PathVariable int transferId) {
        try {
            transferDao.deleteTransferById(transferId);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/

    /*@PutMapping(path = "/{transferId}")
    public ResponseEntity<Transfer> update(@PathVariable int transferId, @RequestBody Transfer transfer) {
        try {
            transferDao.updateTransfer(transfer);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }*/

}






