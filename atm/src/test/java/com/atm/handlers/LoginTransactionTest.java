package com.atm.handlers;

import com.atm.dto.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTransactionTest {

    private Bank bank;
    private DebtTracker debtTracker;
    private CommandProcessor processor;
    private Transaction loginTransaction;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        debtTracker = new DebtTracker();
        processor = new CommandProcessor(bank, debtTracker);
        loginTransaction = new LoginTransaction();
    }

    @Test
    void testExecuteLoginSuccessfully() {
        String[] args = {"login", "Alice"};


        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        loginTransaction.execute(args, bank, debtTracker, processor);

        assertEquals("Alice", processor.getLoggedInCustomer(), "Alice should be logged in.");

        assertEquals(0, bank.getBalance("Alice"), "Alice's balance should be 0.");

        String expectedOutput = "Hello, Alice!\nYour balance is $0\n\n";
        assertEquals(expectedOutput, outContent.toString(), "Output should greet Alice and show balance.");

        System.setOut(System.out);
    }

    @Test
    void testExecuteLoginWhenAlreadyLoggedIn() {
        String[] args1 = {"login", "Bob"};
        loginTransaction.execute(args1, bank, debtTracker, processor);

        String[] args2 = {"login", "Sagar"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        loginTransaction.execute(args2, bank, debtTracker, processor);

        assertEquals("Bob", processor.getLoggedInCustomer(), "Bob should remain logged in.");

        assertEquals(0, bank.getBalance("Sagar"), "Sagar's account should not be created.");

        assertEquals("Error:Bob has already logged in. Please logout first.\n", outContent.toString(), "Bob user has already logged in hence stopped.");

        System.setOut(System.out);
    }

    @Test
    void testExecuteLoginWithMissingName() {
        String[] args = {"login"};

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        loginTransaction.execute(args, bank, debtTracker, processor);

        assertNull(processor.getLoggedInCustomer(), "No user should be logged in.");

        String expectedOutput = "Example: login <name>\n";
        assertEquals(expectedOutput, outContent.toString(), "Should prompt with correct usage.");

        System.setOut(System.out);
    }
}
