package com.atm.controller;

import com.atm.dto.Bank;
import com.atm.handlers.DebtTracker;
import com.atm.handlers.CommandProcessor;

import java.util.Scanner;

public class ATMController {

    private static final String EXIT = "exit";
    public static void main(String[] args) {
        System.out.println("Welcome to CLI ATM. Type a command (login/deposit/withdraw/transfer/logout) or 'exit' to quit.");

        Bank bank = new Bank();
        DebtTracker debtTracker = new DebtTracker();

        CommandProcessor processor = new CommandProcessor(bank, debtTracker);

        Scanner sc = new Scanner(System.in);

        while (true) {
            String line = sc.nextLine().trim();
            if (EXIT.equalsIgnoreCase(line)) {
                System.out.println("Exiting the application...");
                break;
            }

            if (!line.isEmpty()) {
                processor.process(line);
            }
        }

        sc.close();
    }
}

