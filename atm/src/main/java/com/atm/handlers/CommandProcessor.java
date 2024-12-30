package com.atm.handlers;

import com.atm.dto.Bank;
import java.util.HashMap;
import java.util.Map;

public class CommandProcessor {

    private static final String LOGIN = "login";
    private static final String DEPOSIT = "deposit";
    private static final String WITHDRAW = "withdraw";
    private static final String TRANSFER = "transfer";
    private static final String LOGOUT = "logout";

    private final Bank bank;
    private final DebtTracker debtTracker;
    private final Map<String, Transaction> transactions;

    private String loggedInCustomer = null;

    public CommandProcessor(Bank bank, DebtTracker debtTracker) {
        this.bank = bank;
        this.debtTracker = debtTracker;
        this.transactions = new HashMap<>();
        initializeTransactions();
    }

    /**
     * Initializes the transaction strategies for each command.
     */
    private void initializeTransactions() {
        transactions.put(LOGIN, new LoginTransaction());
        transactions.put(DEPOSIT, new DepositTransaction());
        transactions.put(WITHDRAW, new WithdrawTransaction());
        transactions.put(TRANSFER, new TransferTransaction());
        transactions.put(LOGOUT, new LogoutTransaction());
    }

    /**
     * Processes the input command by delegating to the appropriate transaction strategy.
     *
     * @param input The raw input command string.
     */
    public void process(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0) {
            System.out.println("No command entered.");
            return;
        }

        String command = parts[0].toLowerCase();

        Transaction transaction = transactions.get(command);
        if (transaction != null) {
            transaction.execute(parts, bank, debtTracker, this);
        } else {
            System.out.printf("Unknown command: %s%n", command);
        }
    }

    // ------------------------------
    // Getters and Setters
    // ------------------------------

    public String getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public void setLoggedInCustomer(String loggedInCustomer) {
        this.loggedInCustomer = loggedInCustomer;
    }

    // ------------------------------
    // Session and Utility Methods
    // ------------------------------

    /**
     * Checks if no user is currently logged in.
     *
     * @return True if no user is logged in; otherwise, false.
     */
    public boolean isSessionActive() {
        if (loggedInCustomer == null) {
            System.out.println("No customer is currently logged in.");
            return true;
        }
        return false;
    }

    /**
     * Prints the balance and debt details of the specified customer.
     *
     * @param customerName The name of the customer.
     */
    public void printCustomerBalanceDetails(String customerName) {
        int balance = bank.getBalance(customerName);
        System.out.println("Your balance is $" + balance);

        // Print how much others owe the logged-in customer
        debtTracker.printOwedToCustomer(customerName);

        // Print how much logged-in customer owes to others
        debtTracker.printCustomerDebts(customerName);

        System.out.println(); // For spacing
    }
}
