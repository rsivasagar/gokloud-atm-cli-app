package com.atm.dto;

import java.util.HashMap;
import java.util.Map;

public class Bank {

    private final Map<String, Integer> accounts = new HashMap<>();

    public void createAccountIfNotExists(String user) {
        accounts.putIfAbsent(user, 0);
    }

    public int getBalance(String user) {
        return accounts.getOrDefault(user, 0);
    }

    public void deposit(String user, int amount) {
        if (amount <= 0) {
            return;
        }
        accounts.put(user, getBalance(user) + amount);
    }

    public void withdraw(String user, int amount) {
        if (amount <= 0) {
            return;
        }
        int current = getBalance(user);
        if (current < amount) {
            accounts.put(user, 0);
        } else {
            accounts.put(user, current - amount);
        }
    }
}
