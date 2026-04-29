import java.util.*;

public class BudgetService {

    static void setCategoryBudget(Scanner sc) {

        User u = AppData.currentUser;

        System.out.println("Current categories:");

        for (String key : u.categoryBudgets.keySet()) {
            System.out.println("- " + key + " : " + u.categoryBudgets.get(key));
        }

        System.out.print("Enter category name exactly: ");
        String category = sc.nextLine().trim();

        if (!u.categoryBudgets.containsKey(category)) {
            System.out.println("Invalid category.");
            return;
        }

        System.out.print("Enter budget amount for " + category + ": ");
        double amount = InputUtil.readDouble(sc);

        u.categoryBudgets.put(category, amount);
        AppData.saveAll();

        System.out.println("Budget updated successfully.");
    }

    static double getMonthlySpentForCategory(String username, String category, String monthPrefix) {

        double total = 0.0;

        for (int i = 0; i < AppData.expenses.size(); i++) {

            Expense e = AppData.expenses.get(i);

            if (e.groupId == 0 &&
                e.ownerUsername.equalsIgnoreCase(username) &&
                e.category.equalsIgnoreCase(category) &&
                e.date.startsWith(monthPrefix)) {

                total += e.totalAmount;
            }
        }

        return total;
    }

    static double getTotalMonthlyPersonalSpent(String username, String monthPrefix) {

        double total = 0.0;

        for (int i = 0; i < AppData.expenses.size(); i++) {

            Expense e = AppData.expenses.get(i);

            if (e.groupId == 0 &&
                e.ownerUsername.equalsIgnoreCase(username) &&
                e.date.startsWith(monthPrefix)) {

                total += e.totalAmount;
            }
        }

        return total;
    }

    static void checkBudgetWarning(String username, String category, double addedAmount, String date) {

        User u = AppData.findUserByUsername(username);

        if (u == null) {
            return;
        }

        String monthPrefix;

        if (date.length() >= 7) {
            monthPrefix = date.substring(0, 7);
        } else {
            monthPrefix = date;
        }

        double categorySpent = getMonthlySpentForCategory(username, category, monthPrefix);
        double totalSpent = getTotalMonthlyPersonalSpent(username, monthPrefix);

        double categoryBudget = u.categoryBudgets.getOrDefault(category, 0.0);

        if (categoryBudget > 0 && categorySpent > categoryBudget) {
            System.out.println("WARNING: Category budget exceeded for " + category);
        }

        if (u.spendingLimit > 0 && totalSpent > u.spendingLimit) {
            System.out.println("WARNING: Overall monthly spending limit exceeded.");
        }
    }

    static void viewBudgetStatus() {

        User u = AppData.currentUser;
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter month prefix (yyyy-mm): ");
        String monthPrefix = sc.nextLine().trim();

        System.out.println("\n---- BUDGET STATUS ----");

        for (String category : u.categoryBudgets.keySet()) {

            double budget = u.categoryBudgets.get(category);
            double spent = getMonthlySpentForCategory(u.username, category, monthPrefix);
            double remaining = budget - spent;

            System.out.println("Category: " + category);
            System.out.println("Budget: " + String.format("%.2f", budget));
            System.out.println("Spent: " + String.format("%.2f", spent));
            System.out.println("Remaining: " + String.format("%.2f", remaining));

            if (budget > 0 && spent > budget) {
                System.out.println("Status: Exceeded");
            } else {
                System.out.println("Status: Within limit");
            }

            System.out.println("---------------------");
        }

        double totalSpent = getTotalMonthlyPersonalSpent(u.username, monthPrefix);

        System.out.println("Overall monthly spending limit: " + u.spendingLimit);
        System.out.println("Total personal spent this month: " + String.format("%.2f", totalSpent));

        if (u.spendingLimit > 0 && totalSpent > u.spendingLimit) {
            System.out.println("Overall Status: Limit exceeded");
        } else {
            System.out.println("Overall Status: Within limit");
        }
    }
}