package com.atm.handler;

import com.atm.dto.Bank;

public class CommandProcessor {

    private static final String LOGIN = "login";
    private static final String DEPOSIT = "deposit";
    private static final String WITHDRAW = "withdraw";
    private static final String TRANSFER = "transfer";
    private static final String LOGOUT = "logout";

    private final Bank bank;
    private final DebtTracker debtTracker;

    private String loggedInCustomer = null;

    public CommandProcessor(Bank bank, DebtTracker debtTracker) {
        this.bank = bank;
        this.debtTracker = debtTracker;
    }

    public void process(String input) {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
                case LOGIN:
                    handleLogin(parts);
                    break;
                case DEPOSIT:
                    handleDeposit(parts);
                    break;
                case WITHDRAW:
                    handleWithdraw(parts);
                    break;
                case TRANSFER:
                    handleTransfer(parts);
                    break;
                case LOGOUT:
                    handleLogout();
                    break;
                default:
                    System.out.printf("Unknown command: %s", command);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (Exception e) {
            System.out.println("Error processing command: " + e.getMessage());
        }
    }

    // ------------------------------
    // Command Handlers
    // ------------------------------

    private void handleLogin(String[] parts) {
        if (loggedInCustomer != null) {
            System.out.println("Error:" + loggedInCustomer +" has already logged in. Please logout first.");
            return;
        }

        if (parts.length < 2) {
            System.out.println("Example: login <name>");
            return;
        }

        loggedInCustomer = parts[1];
        // If customer doesn't exist, create an account
        bank.createAccountIfNotExists(loggedInCustomer);
        System.out.println("Hello, " + loggedInCustomer + "!");

        // Print Customer Balance and Debts
        printCustomerBalanceDetails(loggedInCustomer);
    }

    private void handleDeposit(String[] parts) {
        // Check if the Customer has logged in
        if (isSessionActive()) {
            return;
        }
        if (parts.length < 2) {
            System.out.println("Example: deposit <amount>");
            return;
        }

        int amount = Integer.parseInt(parts[1]);
        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }

        // Clear debts if present
        int leftover = debtTracker.repayDebtsUponDepositing(loggedInCustomer, amount, bank);

        // Deposit the remainder to the logged-in customer's balance
        if (leftover > 0) {
            bank.deposit(loggedInCustomer, leftover);
        }

        // Print Customer Balance and Debts
        printCustomerBalanceDetails(loggedInCustomer);
    }

    private void handleWithdraw(String[] parts) {
        if (isSessionActive()) {
            return;
        }
        if (parts.length < 2) {
            System.out.println("Example: withdraw <amount>");
            return;
        }

        int amount = Integer.parseInt(parts[1]);
        if (amount <= 0) {
            System.out.println("Invalid amount. Please try again!");
            return;
        }

        int currentBalance = bank.getBalance(loggedInCustomer);
        if (currentBalance < amount) {
            System.out.println("Insufficient funds. Please try again!");
        } else {
            bank.withdraw(loggedInCustomer, amount);
        }

        printCustomerBalanceDetails(loggedInCustomer);
    }

    private void handleTransfer(String[] parts) {
        if (isSessionActive()) {
            return;
        }
        if (parts.length < 3) {
            System.out.println("Example: transfer <target> <amount>");
            return;
        }

        String target = parts[1];
        int amount = Integer.parseInt(parts[2]);

        if (amount <= 0) {
            System.out.println("Invalid amount");
            return;
        }
        if (target.equals(loggedInCustomer)) {
            System.out.println("Cannot transfer to yourself. Please try a different customer!");
            return;
        }

        // Ensure target account exists
        bank.createAccountIfNotExists(target);

        // Check if target owes logged-in customer
        int owed = debtTracker.getDebt(target, loggedInCustomer);
        if (owed > 0) {
            int repayment = Math.min(amount, owed);
            debtTracker.clearDebt(target, loggedInCustomer, repayment);

            System.out.println("Transferred $" + repayment + " to " + target);
            System.out.println("Your balance is $" + bank.getBalance(loggedInCustomer));

            int remainingOwed = debtTracker.getDebt(target, loggedInCustomer);
            if (remainingOwed > 0) {
                System.out.println("Owed $" + remainingOwed + " from " + target);
                System.out.printf("%n%n");
            }
        } else {
            int transferableAmount = Math.min(bank.getBalance(loggedInCustomer), amount);
            bank.withdraw(loggedInCustomer, transferableAmount);
            bank.deposit(target, transferableAmount);

            int shortfall = amount - transferableAmount;

            System.out.println("Transferred $" + transferableAmount + " to " + target);

            if (shortfall > 0) {
                debtTracker.addDebt(loggedInCustomer, target, shortfall);
                System.out.println("Your balance is $" + bank.getBalance(loggedInCustomer));
                System.out.println("Owed $" + shortfall + " to " + target);
                System.out.printf("%n%n");
            } else {
                printCustomerBalanceDetails(loggedInCustomer);
            }
        }
    }

    private void handleLogout() {
        if (isSessionActive()) {
            return;
        }
        System.out.println("Goodbye, " + loggedInCustomer + "!");
        System.out.printf("%n%n");
        loggedInCustomer = null;
    }

    private boolean isSessionActive() {
        if (loggedInCustomer == null) {
            System.out.println("No Customer has currently logged in.");
            return true;
        }
        return false;
    }

    private void printCustomerBalanceDetails(String customerName) {
        int balance = bank.getBalance(customerName);
        System.out.println("Your balance is $" + balance);

        // Print how much others owe the logged-in customer
        debtTracker.printOwedToCustomer(customerName);

        // Print how much logged-in customer owes to others
        debtTracker.printCustomerDebts(customerName);

        System.out.printf("%n%n");
    }
}

