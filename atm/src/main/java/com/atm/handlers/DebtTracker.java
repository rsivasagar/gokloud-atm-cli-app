package com.atm.handlers;

import com.atm.dto.Bank;

import java.util.HashMap;
import java.util.Map;

public class DebtTracker {

    private final Map<String, Map<String, Integer>> debts = new HashMap<>();

    public void addDebt(String fromUser, String toUser, int amount) {
        if (amount <= 0) {
            return;
        }
        debts.putIfAbsent(fromUser, new HashMap<>());
        Map<String, Integer> fromUserDebts = debts.get(fromUser);
        fromUserDebts.put(toUser, fromUserDebts.getOrDefault(toUser, 0) + amount);
    }

    public int getDebt(String fromUser, String toUser) {
        return debts.getOrDefault(fromUser, new HashMap<>()).getOrDefault(toUser, 0);
    }

    public void clearDebt(String fromUser, String toUser, int amount) {
        if (amount <= 0) {
            return;
        }
        if (debts.containsKey(fromUser)) {
            Map<String, Integer> fromUserDebts = debts.get(fromUser);
            int currentDebt = fromUserDebts.getOrDefault(toUser, 0);
            int newDebt = currentDebt - amount;
            if (newDebt <= 0) {
                fromUserDebts.remove(toUser);
                if (fromUserDebts.isEmpty()) {
                    debts.remove(fromUser);
                }
            } else {
                fromUserDebts.put(toUser, newDebt);
            }
        }
    }

    public int repayDebtsUponDepositing(String user, int depositAmount, Bank bank) {
        if (depositAmount <= 0) {
            return 0;
        }
        if (!debts.containsKey(user)) {
            return depositAmount;
        }

        Map<String, Integer> oweMap = debts.get(user);

        for (String creditor : oweMap.keySet()) {
            int owed = oweMap.get(creditor);
            if (owed <= 0) {
                continue;
            }
            if (depositAmount <= 0) {
                break;
            }

            if (depositAmount >= owed) {
                depositAmount -= owed;
                clearDebt(user, creditor, owed);
                System.out.println("Transferred $" + owed + " to " + creditor);
                bank.deposit(creditor, owed);
            } else {
                clearDebt(user, creditor, depositAmount);
                System.out.println("Transferred $" + depositAmount + " to " + creditor);
                bank.deposit(creditor, depositAmount);
                depositAmount = 0;
            }
        }

        return depositAmount;
    }

    public void printOwedToCustomer(String user) {
        for (Map.Entry<String, Map<String, Integer>> entry : debts.entrySet()) {
            String debtor = entry.getKey();
            Map<String, Integer> map = entry.getValue();
            int amount = map.getOrDefault(user, 0);
            if (amount > 0) {
                System.out.println("Owed $" + amount + " from " + debtor);
            }
        }
    }

    public void printCustomerDebts(String user) {
        Map<String, Integer> userDebts = debts.get(user);
        if (userDebts == null) return;

        for (Map.Entry<String, Integer> e : userDebts.entrySet()) {
            String creditor = e.getKey();
            int amount = e.getValue();
            if (amount > 0) {
                System.out.println("Owed $" + amount + " to " + creditor);
            }
        }
    }
}
