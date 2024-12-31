package com.atm.handlers;

import com.atm.dto.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferTransactionTest {

    private Bank bank;
    private DebtTracker debtTracker;
    private CommandProcessor processor;
    private Transaction transferTransaction;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        debtTracker = new DebtTracker();
        processor = new CommandProcessor(bank, debtTracker);
        transferTransaction = new TransferTransaction();
    }

    @Test
    void testExecuteTransferWhenNoDebtAndSufficientFunds() {
        String[] loginArgs = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Alice", 100);

        String[] transferArgs = {"transfer", "Bob", "50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        transferTransaction.execute(transferArgs, bank, debtTracker, processor);

        assertEquals(50, bank.getBalance("Alice"), "Alice's balance should be 50 after transferring 50.");
        assertEquals(50, bank.getBalance("Bob"), "Bob's balance should be 50 after receiving 50.");
        assertEquals(0, debtTracker.getDebt("Bob", "Alice"), "No debt should be recorded.");

        String expectedOutput = "Transferred $50 to Bob\nYour balance is $50\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should confirm transfer and show new balance.");

        System.setOut(System.out);
    }

    @Test
    void testExecuteTransferWhenNoDebtAndInsufficientFunds() {
        String[] loginArgs = {"login", "Sagar"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Sagar", 30);

        String[] transferArgs = {"transfer", "Satvik", "50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        transferTransaction.execute(transferArgs, bank, debtTracker, processor);

        assertEquals(0, bank.getBalance("Sagar"), "Sagar's balance should be 0 after transferring all funds.");
        assertEquals(30, bank.getBalance("Satvik"), "Satvik's balance should be 30 after receiving 30.");
        assertEquals(20, debtTracker.getDebt("Sagar", "Satvik"), "Debt of 20 should be recorded to Satvik.");

        String expectedOutput = "Transferred $30 to Satvik\nYour balance is $0\nOwed $20 to Satvik\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should confirm partial transfer and record debt.");

        System.setOut(System.out);
    }

    @Test
    void testExecuteTransferWhenDebtExists() {
        String[] loginAlice = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginAlice, bank, debtTracker, processor);

        String[] loginBob = {"login", "Bob"};
        loginTransaction.execute(loginBob, bank, debtTracker, processor);

        bank.deposit("Bob", 100);
        bank.deposit("Alice", 200);

        String[] transferToAlice = {"transfer", "Alice", "50"};
        transferTransaction.execute(transferToAlice, bank, debtTracker, processor);

        String[] transferBack = {"transfer", "Bob", "30"};

        debtTracker.addDebt("Bob", "Alice", 40);

        String[] transferArgs = {"transfer", "Bob", "30"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        transferTransaction.execute(transferArgs, bank, debtTracker, processor);

        assertEquals(10, debtTracker.getDebt("Bob", "Alice"), "Debt should be reduced from 40 to 10.");
        assertEquals(200, bank.getBalance("Alice"), "Alice's balance should remain unchanged as per previous implementation.");
        assertEquals(100, bank.getBalance("Bob"), "Bob's balance should remain unchanged as per previous implementation.");

        String expectedOutput = "Transferred $30 to Bob\nYour balance is $200\nOwed $10 from Bob\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should confirm debt repayment.");

        System.setOut(System.out);
    }

    @Test
    void testExecuteTransferToSelf() {
        String[] loginArgs = {"login", "Neelam"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Neelam", 100);

        String[] transferArgs = {"transfer", "Neelam", "50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        transferTransaction.execute(transferArgs, bank, debtTracker, processor);

        assertEquals(100, bank.getBalance("Neelam"), "Neelam's balance should remain unchanged.");
        assertEquals(0, debtTracker.getDebt("Neelam", "Neelam"), "No debt should be recorded.");

        String expectedOutput = "Cannot transfer to yourself. Please try a different customer!\n";
        assertEquals(expectedOutput, outContent.toString(), "Should prevent transferring to self.");

        System.setOut(System.out);
    }

    @Test
    void testExecuteTransferWithInvalidAmount() {
        String[] loginArgs = {"login", "Satya"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        bank.deposit("Satya", 100);

        String[] transferArgs = {"transfer", "Reyaaz", "-50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        transferTransaction.execute(transferArgs, bank, debtTracker, processor);

        assertEquals(100, bank.getBalance("Satya"), "Satya's balance should remain unchanged.");
        assertEquals(0, debtTracker.getDebt("Satya", "Reyaaz"), "No debt should be recorded.");

        String expectedOutput = "Invalid amount\n";
        assertEquals(expectedOutput, outContent.toString(), "Should reject invalid transfer amount.");

        System.setOut(System.out);
    }
}
