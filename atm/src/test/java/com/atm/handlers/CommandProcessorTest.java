package com.atm.handlers;

import com.atm.dto.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandProcessorTest {

    private CommandProcessor processor;

    @BeforeEach
    void setUp() {
        Bank bank = new Bank();
        DebtTracker debtTracker = new DebtTracker();
        processor = new CommandProcessor(bank, debtTracker);
    }

    @Test
    void testFullExampleSession() {
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        processor.process("login Alice");
        processor.process("deposit 210");
        processor.process("logout");

        processor.process("login Bob");
        processor.process("deposit 80");
        processor.process("transfer Alice 50");
        processor.process("transfer Alice 100");
        processor.process("deposit 30");
        processor.process("logout");

        processor.process("login Alice");
        processor.process("transfer Bob 30");
        processor.process("logout");

        processor.process("login Bob");
        processor.process("deposit 100");
        processor.process("logout");

        String actualOutput = outContent.toString();

        String expectedOutput =
                "Hello, Alice!\nYour balance is $0\n\n" +
                        "Your balance is $210\n\n" +
                        "Goodbye, Alice!\n\n" +
                        "Hello, Bob!\nYour balance is $0\n\n" +
                        "Your balance is $80\n\n" +
                        "Transferred $50 to Alice\nYour balance is $30\n\n" +
                        "Transferred $30 to Alice\nYour balance is $0\nOwed $70 to Alice\n\n" +
                        "Transferred $30 to Alice\nYour balance is $0\nOwed $40 to Alice\n\n" +
                        "Goodbye, Bob!\n\n" +
                        "Hello, Alice!\nYour balance is $320\nOwed $40 from Bob\n\n" +
                        "Transferred $30 to Bob\nYour balance is $320\nOwed $10 from Bob\n\n" +
                        "Goodbye, Alice!\n\n" +
                        "Hello, Bob!\nYour balance is $0\nOwed $10 to Alice\n\n" +
                        "Transferred $10 to Alice\nYour balance is $90\n\n" +
                        "Goodbye, Bob!\n\n";

        assertEquals(expectedOutput, actualOutput, "Output should match the expected session flow.");

        // Reset System.out
        System.setOut(System.out);
    }
}
