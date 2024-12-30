package com.atm.handlers;

import com.atm.dto.Bank;

public class LogoutTransaction implements Transaction {

    @Override
    public void execute(String[] args, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        if (processor.isSessionActive()) {
            return;
        }
        System.out.println("Goodbye, " + processor.getLoggedInCustomer() + "!\n");
        processor.setLoggedInCustomer(null);
    }
}
