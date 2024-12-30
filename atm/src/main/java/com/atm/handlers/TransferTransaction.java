package com.atm.handlers;

import com.atm.dto.Bank;

public class TransferTransaction implements Transaction {

    @Override
    public void execute(String[] args, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        if (processor.isSessionActive()) {
            return;
        }

        if (args.length < 3) {
            System.out.println("Example: transfer <target> <amount>");
            return;
        }

        String targetCustomer = args[1];
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                System.out.println("Invalid amount");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format. Please enter a valid integer.");
            return;
        }

        if (targetCustomer.equals(processor.getLoggedInCustomer())) {
            System.out.println("Cannot transfer to yourself. Please try a different customer!");
            return;
        }

        // Ensure target account exists
        bank.createAccountIfNotExists(targetCustomer);

        // Check if target owes logged-in customer
        int owed = debtTracker.getDebt(targetCustomer, processor.getLoggedInCustomer());

        if (owed > 0) {
            handleDebtRepayment(targetCustomer, amount, owed, bank, debtTracker, processor);
        } else {
            handleStandardTransfer(targetCustomer, amount, bank, debtTracker, processor);
        }
    }

    private void handleDebtRepayment(String targetCustomer, int amount, int owed, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        int repayment = Math.min(amount, owed);
        debtTracker.clearDebt(targetCustomer, processor.getLoggedInCustomer(), repayment);

        System.out.println("Transferred $" + repayment + " to " + targetCustomer);
        System.out.println("Your balance is $" + bank.getBalance(processor.getLoggedInCustomer()));

        int remainingOwed = debtTracker.getDebt(targetCustomer, processor.getLoggedInCustomer());
        if (remainingOwed > 0) {
            System.out.println("Owed $" + remainingOwed + " from " + targetCustomer + "\n");
        }
    }

    private void handleStandardTransfer(String targetCustomer, int amount, Bank bank, DebtTracker debtTracker, CommandProcessor processor) {
        int transferableAmount = Math.min(bank.getBalance(processor.getLoggedInCustomer()), amount);
        bank.withdraw(processor.getLoggedInCustomer(), transferableAmount);
        bank.deposit(targetCustomer, transferableAmount);

        int remainingAmount = amount - transferableAmount;

        System.out.println("Transferred $" + transferableAmount + " to " + targetCustomer);

        if (remainingAmount > 0) {
            debtTracker.addDebt(processor.getLoggedInCustomer(), targetCustomer, remainingAmount);
            System.out.println("Your balance is $" + bank.getBalance(processor.getLoggedInCustomer()));
            System.out.println("Owed $" + remainingAmount + " to " + targetCustomer + "\n");
        } else {
            processor.printCustomerBalanceDetails(processor.getLoggedInCustomer());
        }
    }
}
