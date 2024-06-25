package com.techelevator.tenmo.model;

public class TransferType {
    private int transfertTypeId;
    private String name;

    public TransferType() {
    }

    public int getTransfertTypeId() {
        return transfertTypeId;
    }

    public void setTransfertTypeId(int transfertTypeId) {
        this.transfertTypeId = transfertTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
