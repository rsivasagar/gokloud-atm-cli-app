package com.atm.handlers;

import com.atm.dto.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WithdrawTransactionTest {

    private Bank bank;
    private DebtTracker debtTracker;
    private CommandProcessor processor;
    private Transaction withdrawTransaction;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        debtTracker = new DebtTracker();
        processor = new CommandProcessor(bank, debtTracker);
        withdrawTransaction = new WithdrawTransaction();
    }

    @Test
    void testWithdrawWithSufficientFunds() {
        String[] loginArgs = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Alice", 100);

        String[] withdrawArgs = {"withdraw", "50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        withdrawTransaction.execute(withdrawArgs, bank, debtTracker, processor);

        assertEquals(50, bank.getBalance("Alice"), "Alice's balance should be 50 after withdrawing 50.");

        System.setOut(System.out);
    }

    @Test
    void testWithdrawWithInsufficientFunds() {
        // Setup
        String[] loginArgs = {"login", "Bob"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Bob", 30);

        String[] withdrawArgs = {"withdraw", "50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        withdrawTransaction.execute(withdrawArgs, bank, debtTracker, processor);

        assertEquals(30, bank.getBalance("Bob"), "Bob's balance should remain 30 after failed withdrawal.");

        String expectedOutput = "Insufficient funds. Please try again!\nYour balance is $30\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should indicate insufficient funds and show balance.");

        System.setOut(System.out);
    }

    @Test
    void testWithdrawWithInvalidAmount() {
        // Setup
        String[] loginArgs = {"login", "Sagar"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Sagar", 100);

        String[] withdrawArgs = {"withdraw", "-20"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        withdrawTransaction.execute(withdrawArgs, bank, debtTracker, processor);

        assertEquals(100, bank.getBalance("Sagar"), "Sagar's balance should remain 100 after invalid withdrawal.");

        String expectedOutput = "Invalid amount. Please try again!\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should indicate invalid amount and show balance.");

        System.setOut(System.out);
    }
}
