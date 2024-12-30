package com.atm.handlers;

import com.atm.dto.Bank;

public interface Transaction {
    void execute(String[] args, Bank bank, DebtTracker debtTracker, CommandProcessor processor);
}
