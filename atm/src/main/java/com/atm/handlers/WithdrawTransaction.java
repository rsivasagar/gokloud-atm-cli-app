package com.atm.handlers;

import com.atm.dto.Bank;

public class WithdrawTransaction implements Transaction {

    @Override
    public void execute(String[] args, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        if (processor.isSessionActive()) {
            return;
        }

        if (args.length < 2) {
            System.out.println("Example: withdraw <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                System.out.println("Invalid amount. Please try again!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid integer.");
            return;
        }

        int currentBalance = bank.getBalance(processor.getLoggedInCustomer());
        if (currentBalance < amount) {
            System.out.println("Insufficient funds. Please try again!");
        } else {
            bank.withdraw(processor.getLoggedInCustomer(), amount);
        }

        processor.printCustomerBalanceDetails(processor.getLoggedInCustomer());
    }
}

