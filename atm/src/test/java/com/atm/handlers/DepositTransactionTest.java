package com.atm.handlers;

import com.atm.dto.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepositTransactionTest {

    private Bank bank;
    private DebtTracker debtTracker;
    private CommandProcessor processor;
    private Transaction depositTransaction;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        debtTracker = new DebtTracker();
        processor = new CommandProcessor(bank, debtTracker);
        depositTransaction = new DepositTransaction();
    }

    @Test
    void testSuccessfulDepositWithoutDebts() {
        String[] loginArgs = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        String[] depositArgs = {"deposit", "100"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        depositTransaction.execute(depositArgs, bank, debtTracker, processor);

        assertEquals(100, bank.getBalance("Alice"), "Alice's balance should be $100 after deposit.");

        assertEquals(0, debtTracker.getDebt("Bob", "Alice"), "Bob should not owe Alice anything.");
        assertEquals(0, debtTracker.getDebt("Reyaaz", "Alice"), "Reyaaz should not owe Alice anything.");

        String expectedOutput = "Your balance is $100\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should show updated balance.");

        System.setOut(System.out);
    }

    @Test
    void testDepositWithExistingDebtsPartialRepayment() {
        String[] loginAlice = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginAlice, bank, debtTracker, processor);

        String[] loginBob = {"login", "Bob"};
        loginTransaction.execute(loginBob, bank, debtTracker, processor);

        debtTracker.addDebt("Bob", "Alice", 70);

        processor.process("logout");
        processor.process("login Bob");

        String[] depositArgs = {"deposit", "50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        depositTransaction.execute(depositArgs, bank, debtTracker, processor);

        assertEquals(50, bank.getBalance("Alice"), "Alice's balance should be $50 after partial debt repayment.");

        assertEquals(20, debtTracker.getDebt("Bob", "Alice"), "Bob should owe Alice $20 after partial repayment.");

        String expectedOutput = "Transferred $50 to Alice\nYour balance is $0\nOwed $20 to Alice\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should show updated balance after debt repayment.");

        System.setOut(System.out);
    }

    @Test
    void testDepositFullyRepaysDebtsAndAddsToBalance() {
        String[] loginAlice = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginAlice, bank, debtTracker, processor);

        String[] loginBob = {"login", "Bob"};
        loginTransaction.execute(loginBob, bank, debtTracker, processor);

        debtTracker.addDebt("Bob", "Alice", 40);

        processor.process("logout");
        processor.process("login Bob");

        String[] depositArgs = {"deposit", "100"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        depositTransaction.execute(depositArgs, bank, debtTracker, processor);

        assertEquals(40, bank.getBalance("Alice"), "Alice's balance should be $60 after full debt repayment and remaining deposit.");

        assertEquals(0, debtTracker.getDebt("Bob", "Alice"), "Bob should not owe Alice anything after full repayment.");

        String expectedOutput = "Transferred $40 to Alice\nYour balance is $60\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should show updated balance after full debt repayment.");

        System.setOut(System.out);
    }

    @Test
    void testDepositWithNegativeAmount() {
        String[] loginArgs = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        String[] depositArgs = {"deposit", "-50"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        depositTransaction.execute(depositArgs, bank, debtTracker, processor);

        assertEquals(0, bank.getBalance("Alice"), "Alice's balance should remain $0 after invalid deposit.");

        assertEquals(0, debtTracker.getDebt("Bob", "Alice"), "Bob should not owe Alice anything.");

        String expectedOutput = "Invalid amount\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should indicate invalid deposit amount.");

        System.setOut(System.out);
    }

    @Test
    void testDepositWithInvalidAmountFormat() {
        String[] loginArgs = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        String[] depositArgs = {"deposit", "fifty"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        depositTransaction.execute(depositArgs, bank, debtTracker, processor);

        assertEquals(0, bank.getBalance("Alice"), "Alice's balance should remain $0 after invalid deposit.");

        assertEquals(0, debtTracker.getDebt("Bob", "Alice"), "Bob should not owe Alice anything.");

        String expectedOutput = "Invalid amount format. Please enter a valid integer.\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should indicate invalid amount format.");

        System.setOut(System.out);
    }

    @Test
    void testDepositWithoutAmountParameter() {
        String[] loginArgs = {"login", "Alice"};
        Transaction loginTransaction = new LoginTransaction();
        loginTransaction.execute(loginArgs, bank, debtTracker, processor);

        String[] depositArgs = {"deposit"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        depositTransaction.execute(depositArgs, bank, debtTracker, processor);

        assertEquals(0, bank.getBalance("Alice"), "Alice's balance should remain $0 after incomplete deposit command.");

        assertEquals(0, debtTracker.getDebt("Bob", "Alice"), "Bob should not owe Alice anything.");

        String expectedOutput = "Example: deposit <amount>\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should show correct deposit command usage.");

        System.setOut(System.out);
    }
}
