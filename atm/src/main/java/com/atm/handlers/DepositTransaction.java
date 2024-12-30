package com.atm.handlers;

import com.atm.dto.Bank;

public class DepositTransaction implements Transaction {

    @Override
    public void execute(String[] args, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        if (processor.isSessionActive()) {
            return;
        }

        if (args.length < 2) {
            System.out.println("Example: deposit <amount>");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0) {
                System.out.println("Invalid amount");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid integer.");
            return;
        }

        // Clear debts if present
        int leftover = debtTracker.repayDebtsUponDepositing(processor.getLoggedInCustomer(), amount, bank);

        // Deposit the remainder to the logged-in customer's balance
        if (leftover > 0) {
            bank.deposit(processor.getLoggedInCustomer(), leftover);
        }

        // Print Customer Balance and Debts
        processor.printCustomerBalanceDetails(processor.getLoggedInCustomer());
    }
}
