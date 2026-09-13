# Splitwise Expense Manager

A Java-based expense sharing and personal finance tracker, inspired by Splitwise. It supports multi-user groups, flexible expense splitting, balance tracking, budgeting, recurring expenses, and reporting — available both as a console (CLI) app and a Swing-based desktop GUI.

## Features

- **User accounts** — register and log in with a username/password, and set an overall monthly spending limit.
- **Groups** — create groups, add members, view your groups, and delete groups.
- **Expenses**
  - Add group expenses with flexible splitting: **equal**, **exact amount**, or **percentage**.
  - Add personal (non-group) expenses.
  - View expense history and search expenses by title or category.
  - Add and view **recurring expenses** (weekly/monthly).
- **Balances & settlements**
  - View group balances and a simplified "who owes whom" breakdown.
  - Settle up between members and view settlement history.
- **Budgeting** — set per-category budgets and view budget status against spending.
- **Reports** — generate a monthly spending summary and export reports to a `.txt` file.
- **Persistence** — all data (users, groups, expenses, settlements, recurring expenses) is saved to plain text files and reloaded on startup.
- **Auto-save** — a background daemon thread periodically saves app data automatically.
- **Two interfaces** — a menu-driven console app (`Main.java`) and a Swing GUI (`MainGUI.java`).

## Tech Stack

- **Language:** Java (uses only the standard library — `java.util`, `java.io`, `javax.swing`)
- **Storage:** Flat text files (`users.txt`, `groups.txt`, `expenses.txt`, `settlements.txt`, `recurring.txt`), read/written via `FileManager`
- **Concurrency:** A daemon `Thread` (`AutoSaveThread`) for periodic auto-saving
- **UI:** `java.util.Scanner`-based CLI, and a `javax.swing` desktop GUI

## Project Structure

```
├── Main.java                 # CLI entry point and menu logic
├── MainGUI.java               # Swing GUI entry point and windows
├── AppData.java                # In-memory data store + load/save orchestration
├── AuthService.java             # Registration and login
├── User.java                    # User model
├── Group.java                   # Group model
├── GroupService.java            # Group creation, membership, deletion
├── Expense.java                 # Expense model
├── ExpenseService.java          # Add/search/view expenses, recurring expenses
├── RecurringExpense.java        # Recurring expense model
├── Split.java                   # A single member's share of an expense
├── SplitService.java            # Equal / exact / percentage split calculations
├── BalanceService.java          # Group balances, who-owes-whom, settle up
├── Settlement.java               # Settlement record model
├── BudgetService.java            # Category budgets and budget status
├── ReportService.java            # Monthly summaries and text report export
├── FileManager.java              # Reads/writes all entities to text files
├── FileOperations.java           # save/load interface implemented by FileManager
├── AutoSaveThread.java           # Background thread that periodically saves data
├── InputUtil.java                 # Input parsing/validation helpers
└── (generated at runtime) users.txt, groups.txt, expenses.txt,
    settlements.txt, recurring.txt
```

## Getting Started

### Prerequisites

- JDK 8 or later installed and available on your `PATH`.

### Running the console app

```bash
git clone https://github.com/pranjal24-hub/Splitwise-Expenese-Manager.git
cd Splitwise-Expenese-Manager

javac *.java
java Main
```

### Running the GUI

```bash
javac *.java
java MainGUI
```

Data files (`users.txt`, `groups.txt`, `expenses.txt`, `settlements.txt`, `recurring.txt`) are created automatically in the working directory the first time data is saved, and are reloaded automatically on the next run.

## Usage (CLI)

After launching `Main`, you'll see:

```
==============================
 SMART EXPENSE SHARING SYSTEM
==============================
1. Register
2. Login
3. Exit
```

Register a user, then log in to access the main menu, which includes options to create groups, add group or personal expenses, view balances, settle up, manage budgets, view/add recurring expenses, and export reports.

## Notes

- Passwords are stored and compared as plain text in the data files — this project is intended as a learning/demo application, **not** a production-ready or security-hardened system.
- Data is persisted in simple comma/colon-delimited text files rather than a database.

## Contributing

Issues and pull requests are welcome. If you spot a bug or want to propose a feature (e.g. password hashing, database storage, multi-currency support), feel free to open an issue first to discuss.

## License

No license has been specified for this repository. Please contact the repository owner before reusing this code.
