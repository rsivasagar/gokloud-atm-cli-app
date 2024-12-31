package com.atm.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    private Bank bank;

    @BeforeEach
    void setUp() {
        bank = new Bank();
    }

    @Test
    void testCreateAccountIfNotExists() {
        String user = "Alice";
        bank.createAccountIfNotExists(user);
        assertEquals(0, bank.getBalance(user), "New account should have 0 balance.");

        bank.createAccountIfNotExists(user);
        assertEquals(0, bank.getBalance(user), "Existing account details must not change.");
    }

    @Test
    void testGetBalanceForNonExistentUser() {
        String user = "Sagar";
        assertEquals(0, bank.getBalance(user), "New users should have 0 balance.");
    }

    @Test
    void testDeposit() {
        String user = "Bob";
        bank.createAccountIfNotExists(user);
        bank.deposit(user, 100);
        assertEquals(100, bank.getBalance(user), "Balance should be 100 after depositing 100.");

        bank.deposit(user, 50);
        assertEquals(150, bank.getBalance(user), "Balance should be 150 after depositing additional 50.");

        bank.deposit(user, -50);
        assertEquals(150, bank.getBalance(user), "Balance should not change for invalid deposit.");
    }

    @Test
    void testWithdraw() {
        String user = "Sagar";
        bank.createAccountIfNotExists(user);
        bank.deposit(user, 100);

        bank.withdraw(user, 100);
        assertEquals(0, bank.getBalance(user), "Balance should be 0 after withdrawing all funds.");

        bank.withdraw(user, -30);
        assertEquals(0, bank.getBalance(user), "Balance should not change for invalid withdrawal.");
    }
}
