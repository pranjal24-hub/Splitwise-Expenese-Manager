import java.util.*;

public class ExpenseService {

    static final String[] CATEGORIES = {
            "Food", "Travel", "Rent", "Shopping", "Bills", "Other"
    };

    static void addGroupExpense(Scanner sc) {

        GroupService.viewMyGroups();

        System.out.print("Enter group ID: ");
        int groupId = InputUtil.readInt(sc);

        Group g = AppData.findGroupById(groupId);

        if (g == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!g.members.contains(AppData.currentUser.username)) {
            System.out.println("You are not a member of this group.");
            return;
        }

        System.out.print("Enter expense title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter total amount: ");
        double totalAmount = InputUtil.readDouble(sc);

        System.out.print("Paid by (username): ");
        String paidBy = sc.nextLine().trim();

        if (!g.members.contains(paidBy)) {
            System.out.println("Paid by user must be in group.");
            return;
        }

        String category = chooseCategory(sc);

        System.out.print("Enter date (yyyy-mm-dd): ");
        String date = sc.nextLine().trim();

        System.out.print("Enter note: ");
        String note = sc.nextLine().trim();

        System.out.println("Choose split type:");
        System.out.println("1. Equal");
        System.out.println("2. Exact");
        System.out.println("3. Percentage");
        System.out.print("Enter choice: ");

        int splitChoice = InputUtil.readInt(sc);

        ArrayList<Split> splits = null;
        String splitType = "";

        if (splitChoice == 1) {
            splitType = "EQUAL";
            splits = SplitService.equalSplit(g.members, totalAmount);
        }
        else if (splitChoice == 2) {
            splitType = "EXACT";
            splits = SplitService.exactSplit(g.members, sc, totalAmount);
        }
        else if (splitChoice == 3) {
            splitType = "PERCENTAGE";
            splits = SplitService.percentageSplit(g.members, sc, totalAmount);
        }
        else {
            System.out.println("Invalid split choice.");
            return;
        }

        if (splits == null) return;

        int expenseId = AppData.expenses.size() + 1;

        Expense e = new Expense(
                expenseId,
                groupId,
                "",
                title,
                paidBy,
                totalAmount,
                splitType,
                category,
                date,
                note,
                splits
        );

        AppData.expenses.add(e);
        AppData.saveAll();

        System.out.println("Group expense added successfully.");
    }

    static void addPersonalExpense(Scanner sc) {

        System.out.print("Enter expense title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter amount: ");
        double amount = InputUtil.readDouble(sc);

        String category = chooseCategory(sc);

        System.out.print("Enter date (yyyy-mm-dd): ");
        String date = sc.nextLine().trim();

        System.out.print("Enter note: ");
        String note = sc.nextLine().trim();

        ArrayList<Split> splits = new ArrayList<>();
        splits.add(new Split(AppData.currentUser.username, amount));

        Expense e = new Expense(
                AppData.expenses.size() + 1,
                0,
                AppData.currentUser.username,
                title,
                AppData.currentUser.username,
                amount,
                "PERSONAL",
                category,
                date,
                note,
                splits
        );

        AppData.expenses.add(e);
        AppData.saveAll();

        System.out.println("Personal expense added successfully.");

        BudgetService.checkBudgetWarning(
                AppData.currentUser.username,
                category,
                amount,
                date
        );
    }

    static void showExpenseHistory(Scanner sc) {

        System.out.println("\n1. Show personal expenses");
        System.out.println("2. Show my group expenses");
        System.out.print("Enter choice: ");

        int ch = InputUtil.readInt(sc);

        if (ch == 1) {

            boolean found = false;
            System.out.println("\n---- PERSONAL EXPENSES ----");

            for (int i = 0; i < AppData.expenses.size(); i++) {

                Expense e = AppData.expenses.get(i);

                if (e.groupId == 0 &&
                    e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {

                    printExpense(e);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No personal expenses found.");
            }
        }
        else if (ch == 2) {

            boolean found = false;
            System.out.println("\n---- GROUP EXPENSES ----");

            for (int i = 0; i < AppData.expenses.size(); i++) {

                Expense e = AppData.expenses.get(i);

                if (e.groupId != 0) {

                    Group g = AppData.findGroupById(e.groupId);

                    if (g != null &&
                        g.members.contains(AppData.currentUser.username)) {

                        printExpense(e);
                        found = true;
                    }
                }
            }

            if (!found) {
                System.out.println("No group expenses found.");
            }
        }
        else {
            System.out.println("Invalid choice.");
        }
    }

    static void searchExpense(Scanner sc) {

        System.out.print("Enter title or category to search: ");
        String key = sc.nextLine().trim().toLowerCase();

        boolean found = false;

        System.out.println("\n---- SEARCH RESULT ----");

        for (int i = 0; i < AppData.expenses.size(); i++) {

            Expense e = AppData.expenses.get(i);

            boolean visible = false;

            if (e.groupId == 0 &&
                e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {

                visible = true;
            }
            else if (e.groupId != 0) {

                Group g = AppData.findGroupById(e.groupId);

                if (g != null &&
                    g.members.contains(AppData.currentUser.username)) {

                    visible = true;
                }
            }

            if (visible &&
                (e.title.toLowerCase().contains(key) ||
                 e.category.toLowerCase().contains(key))) {

                printExpense(e);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching expense found.");
        }
    }

    static void addRecurringExpense(Scanner sc) {

        System.out.print("Enter recurring expense title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter amount: ");
        double amount = InputUtil.readDouble(sc);

        String category = chooseCategory(sc);

        System.out.print("Enter frequency (MONTHLY/WEEKLY): ");
        String frequency = sc.nextLine().trim().toUpperCase();

        System.out.print("Enter next due date (yyyy-mm-dd): ");
        String nextDate = sc.nextLine().trim();

        System.out.print("Enter note: ");
        String note = sc.nextLine().trim();

        RecurringExpense r = new RecurringExpense(
                AppData.recurringExpenses.size() + 1,
                AppData.currentUser.username,
                title,
                amount,
                category,
                frequency,
                nextDate,
                note
        );

        AppData.recurringExpenses.add(r);
        AppData.saveAll();

        System.out.println("Recurring expense added successfully.");
    }

    static void viewRecurringExpenses() {

        boolean found = false;

        System.out.println("\n---- RECURRING EXPENSES ----");

        for (int i = 0; i < AppData.recurringExpenses.size(); i++) {

            RecurringExpense r = AppData.recurringExpenses.get(i);

            if (r.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {

                found = true;

                System.out.println("ID: " + r.recurringId);
                System.out.println("Title: " + r.title);
                System.out.println("Amount: " + r.amount);
                System.out.println("Category: " + r.category);
                System.out.println("Frequency: " + r.frequency);
                System.out.println("Next Due Date: " + r.nextDueDate);
                System.out.println("Note: " + r.note);
                System.out.println("-------------------------");
            }
        }

        if (!found) {
            System.out.println("No recurring expenses found.");
        }
    }

    static String chooseCategory(Scanner sc) {

        System.out.println("Choose category:");

        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println((i + 1) + ". " + CATEGORIES[i]);
        }

        System.out.print("Enter choice: ");
        int c = InputUtil.readInt(sc);

        if (c >= 1 && c <= CATEGORIES.length) {
            return CATEGORIES[c - 1];
        }

        return "Other";
    }

    static void printExpense(Expense e) {

        System.out.println("Expense ID: " + e.expenseId);
        System.out.println("Title: " + e.title);
        System.out.println("Amount: " + e.totalAmount);
        System.out.println("Paid By: " + e.paidBy);
        System.out.println("Category: " + e.category);
        System.out.println("Split Type: " + e.splitType);
        System.out.println("Date: " + e.date);
        System.out.println("Note: " + e.note);

        if (e.groupId == 0) {
            System.out.println("Type: Personal Expense");
        }
        else {
            Group g = AppData.findGroupById(e.groupId);
            System.out.println("Group: " + (g != null ? g.groupName : "Unknown"));
        }

        System.out.println("Splits:");

        for (int i = 0; i < e.splits.size(); i++) {

            Split s = e.splits.get(i);

            System.out.println("   " + s.username +
                    " -> " + String.format("%.2f", s.amount));
        }

        System.out.println("----------------------------");
    }
}