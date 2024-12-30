# ATM CLI Application

## Overview
A Command Line Interface (CLI) to simulate an interaction of an ATM with a retail bank.

## Features
* Customer Login : Logs in as this customer and creates the customer if not exist
* Deposit Amount : Deposits this amount to the logged in customer
* Withdraw Amount : Withdraws this amount from the logged in customer
* Transfer Amount : Transfers this amount from the logged in customer to the target customer
* Customer Logout : Logs out of the current customer


## Commands
* `login [name]` 
* `deposit [amount]` 
* `withdraw [amount]` 
* `transfer [target] [amount]` 
* `logout`


## Prerequisites
- Java JDK: The `java` executable in the `PATH` and `JAVA_HOME` environment variable pointing to the `JDK`.
- Maven: Used for project dependency management.
- Bash Shell: To run the `start.sh` script.

## Sample Usage
$ login Alice

Hello, Alice!

Your balance is $0



$ deposit 100

Your balance is $100



$ logout

Goodbye, Alice!



$ login Bob

Hello, Bob!

Your balance is $0



$ deposit 80

Your balance is $80



$ transfer Alice 50

Transferred $50 to Alice

your balance is $30



$ transfer Alice 100

Transferred $30 to Alice

Your balance is $0

Owed $70 to Alice



$ deposit 30

Transferred $30 to Alice

Your balance is $0

Owed $40 to Alice



$ logout

Goodbye, Bob!



$ login Alice

Hello, Alice!

Your balance is $210

Owed $40 from Bob



$ transfer Bob 30

Your balance is $210

Owed $10 from Bob



$ logout

Goodbye, Alice!



$ login Bob

Hello, Bob!

Your balance is $0

Owed $10 to Alice



$ deposit 100

Transferred $10 to Alice

Your balance is $90



$ logout

Goodbye, Bob!


## Notes
1. Stateless Design: The application resets every time `start.sh` is executed. Data is not persistent.
2. Error Handling: The CLI gracefully handles invalid commands and arguments.
