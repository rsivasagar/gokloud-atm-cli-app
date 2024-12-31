package com.atm.handlers;

import com.atm.dto.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DebtTrackerTest {

    private DebtTracker debtTracker;
    private Bank bank;

    @BeforeEach
    void setUp() {
        debtTracker = new DebtTracker();
        bank = new Bank();
    }

    @Test
    void testAddDebt() {
        String fromCustomer = "Bob";
        String toCustomer = "Alice";

        debtTracker.addDebt(fromCustomer, toCustomer, 50);
        assertEquals(50, debtTracker.getDebt(fromCustomer, toCustomer), "Debt should be 50 after adding.");

        debtTracker.addDebt(fromCustomer, toCustomer, 30);
        assertEquals(80, debtTracker.getDebt(fromCustomer, toCustomer), "Debt should be 80 after adding 30.");
    }

    @Test
    void testClearDebt() {
        String fromCustomer = "Alice";
        String toCustomer = "Bob";

        debtTracker.addDebt(fromCustomer, toCustomer, 100);
        debtTracker.clearDebt(fromCustomer, toCustomer, 40);
        assertEquals(60, debtTracker.getDebt(fromCustomer, toCustomer), "Debt should be reduced to 60.");

        debtTracker.clearDebt(fromCustomer, toCustomer, 60);
        assertEquals(0, debtTracker.getDebt(fromCustomer, toCustomer), "Debt should be cleared to 0.");

        debtTracker.addDebt(fromCustomer, toCustomer, 20);
        debtTracker.clearDebt(fromCustomer, toCustomer, 20);
        assertEquals(0, debtTracker.getDebt(fromCustomer, toCustomer), "Debt should be removed after full clearance.");
    }

    @Test
    void testRepayDebtsUponDepositing() {
        String fromCustomer1 = "Alice";
        String fromCustomer2 = "Bob";
        String toCustomer = "Sagar";

        debtTracker.addDebt(fromCustomer1, toCustomer, 70);
        debtTracker.addDebt(fromCustomer2, toCustomer, 30);

        int leftover = debtTracker.repayDebtsUponDepositing(toCustomer, 50, bank);
        assertEquals(70, debtTracker.getDebt(fromCustomer1, toCustomer), "Alice should now owe 20.");
        assertEquals(30, debtTracker.getDebt(fromCustomer2, toCustomer), "Bob's debt remains unchanged.");
        assertEquals(50, leftover, "Leftover deposit should be 20.");

        leftover = debtTracker.repayDebtsUponDepositing(toCustomer, 60, bank);
        assertEquals(70, debtTracker.getDebt(fromCustomer1, toCustomer), "Alice's debt should be 70.");
        assertEquals(30, debtTracker.getDebt(fromCustomer2, toCustomer), "Bob's debt should be 30.");
        assertEquals(60, leftover, "Leftover deposit should be 10.");
    }

    @Test
    void testPrintOwedToCustomer() {
        String fromCustomer1 = "Alice";
        String fromCustomer2 = "Satvik";
        String toCustomer = "Sagar";

        debtTracker.addDebt(fromCustomer1, toCustomer, 40);
        debtTracker.addDebt(fromCustomer2, toCustomer, 60);

        // Redirecting System.out to capture output
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        debtTracker.printOwedToCustomer(toCustomer);
        String expectedOutput = "Owed $40 from Alice\nOwed $60 from Satvik\n";
        assertEquals(expectedOutput, outContent.toString(), "Should list all debts owed to Sagar.");

        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void testPrintCustomerDebts() {
        String fromCustomer = "Alice";
        String toCustomer1 = "Bob";
        String toCustomer2 = "Sagar";

        debtTracker.addDebt(fromCustomer, toCustomer1, 25);
        debtTracker.addDebt(fromCustomer, toCustomer2, 75);

        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        debtTracker.printCustomerDebts(fromCustomer);
        String expectedOutput = "Owed $25 to Bob\nOwed $75 to Sagar\n";
        assertEquals(expectedOutput, outContent.toString(), "Should list all debts owed by Alice.");

        System.setOut(System.out);
    }
}
