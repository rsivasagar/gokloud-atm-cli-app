package com.atm.handlers;

import com.atm.dto.Bank;

public class LoginTransaction implements Transaction {

    @Override
    public void execute(String[] args, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        if (processor.getLoggedInCustomer() != null) {
            System.out.println("Error:" + processor.getLoggedInCustomer() +" has already logged in. Please logout first.");
            return;
        }

        if (args.length < 2) {
            System.out.println("Example: login <name>");
            return;
        }

        String customerName = args[1];
        processor.setLoggedInCustomer(customerName);
        bank.createAccountIfNotExists(customerName);
        System.out.println("Hello, " + customerName + "!");

        processor.printCustomerBalanceDetails(customerName);
    }
}
