## What is IOU?
A free desktop application that helps two people keep track of money owed by one to the other. It would typically be
useful to a couple, roommates, or any two people that incur shared expenses and/or lend money to each other. IOU
keeps a record of all transactions and calculates the current balance (shown in red below).

![Expense](docs/screenshot.png)

## How does it work?
IOU supports two types of transactions: payments and expenses.

### Expenses
Shared expenses are recorded in the right-hand table. It is assumed that all shared expenses should be
split 50/50. For example, if Ann and Bob have a current balance of $0 (neither owes the other anything), then they
receive a gas bill for $100 of which Ann pays $20 and Bob pays $80. This expense should be entered like so:

![Expense](docs/expense.png)

After this expense is saved, the current balance will indicate that Ann owes Bob $30.

### Payments

A payment occurs when one party gives money directly to the other. Typically, a payment will either be a loan or repayment
of an outstanding debt. For example, following payment of the gas bill mentioned above, Ann owes Bob $30. Ann repays this
debt by transferring $30 to his bank account. This should be entered as a payment like so:

![Payment](docs/payment.png)

After this payment is saved, the current balance will indicate that neither Ann nor Bob owes the other anything.

### Archive

By default all payments and expenses entered since the beginning of time are displayed by IOU. When an archive
is performed all payments and expenses that were entered prior to the archive are permanently hidden, but the current
balance is carried forward. In other words, an archive clears the list of payments and expenses shown in IOU without
affecting the current balance.

## Installation

### Prerequisites

* [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html), version 5 or later
* [MySQL](http://dev.mysql.com/downloads/mysql/), tested with version 5.5 but any modern version should work
* [Maven](http://maven.apache.org/download.cgi), tested with version 3.0.3, but any 2.X or 3.X version should work

### Configuration

The steps below only need to be performed once, before you run the application for the first time:

* Create a schema in MySQL named `iou`. If the MySQL server is not running on the default port (3306) on localhost
(or you didn't name the schema `iou`), make the appropriate changes to the `jdbc.url` config parameter in `resources/config.properties`
* Create two MySQL users that have read-write access to the `iou` schema
* In `config.properties` set `ann.username` to the MySQL login of one of the users and set `bob.username` to the
MySQL login of the other user
* In the same file, set `ann.name` and `bob.name` to the names that IOU will display for each of these users
* If $ is not used as the currency symbol in your locale, also set `currency.symbol` to whatever you wish to use instead

### Run IOU

#### With Maven
To build and run the application with Maven, execute the command below from the project's root directory:

`mvn compile exec:java -Dexec.mainClass=iou.gui.AppLauncher`

When the application starts, the selected user should use their MySQL password to login.

#### Without Maven
If you wish to run the application on a machine that doesn't have Maven installed, you can build a self-contained JAR file
by executing the following Maven command from the project's root directory:

`mvn assembly:assembly`

This will create a jar file in the `target` directory named `iou.jar`. This file can be run on any
machine that has Java installed via the command `java -jar iou.jar`.