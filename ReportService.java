import java.io.*;
import java.util.*;

public class ReportService {

    static void monthlySpendingSummary(Scanner sc) {

        System.out.print("Enter month prefix (yyyy-mm): ");
        String month = sc.nextLine().trim();

        double personalTotal = 0.0;
        HashMap<String, Double> categoryMap = new HashMap<>();

        for (int i = 0; i < AppData.expenses.size(); i++) {

            Expense e = AppData.expenses.get(i);

            if (e.groupId == 0 &&
                e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username) &&
                e.date.startsWith(month)) {

                personalTotal += e.totalAmount;

                categoryMap.put(
                        e.category,
                        categoryMap.getOrDefault(e.category, 0.0) + e.totalAmount
                );
            }
        }

        System.out.println("\n---- MONTHLY SPENDING SUMMARY ----");
        System.out.println("User: " + AppData.currentUser.username);
        System.out.println("Month: " + month);
        System.out.println("Total Personal Spending: " + String.format("%.2f", personalTotal));
        System.out.println("Category-wise Summary:");

        for (String cat : categoryMap.keySet()) {
            System.out.println(cat + " -> " + String.format("%.2f", categoryMap.get(cat)));
        }
    }

    static void exportReportToTxt(Scanner sc) {

        System.out.print("Enter month prefix (yyyy-mm): ");
        String month = sc.nextLine().trim();

        String fileName = "report_" + AppData.currentUser.username + "_" + month + ".txt";

        double personalTotal = 0.0;
        HashMap<String, Double> categoryMap = new HashMap<>();

        ArrayList<Expense> personalExpenses = new ArrayList<>();
        ArrayList<Settlement> mySettlements = new ArrayList<>();

        for (int i = 0; i < AppData.expenses.size(); i++) {

            Expense e = AppData.expenses.get(i);

            if (e.groupId == 0 &&
                e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username) &&
                e.date.startsWith(month)) {

                personalExpenses.add(e);
                personalTotal += e.totalAmount;

                categoryMap.put(
                        e.category,
                        categoryMap.getOrDefault(e.category, 0.0) + e.totalAmount
                );
            }
        }

        for (int i = 0; i < AppData.settlements.size(); i++) {

            Settlement s = AppData.settlements.get(i);

            if ((s.fromUser.equalsIgnoreCase(AppData.currentUser.username) ||
                 s.toUser.equalsIgnoreCase(AppData.currentUser.username)) &&
                s.date.startsWith(month)) {

                mySettlements.add(s);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {

            bw.write("SMART EXPENSE SHARING SYSTEM REPORT");
            bw.newLine();

            bw.write("User: " + AppData.currentUser.username);
            bw.newLine();

            bw.write("Month: " + month);
            bw.newLine();

            bw.write("Overall Spending Limit: " + AppData.currentUser.spendingLimit);
            bw.newLine();

            bw.write("Total Personal Spending: " + String.format("%.2f", personalTotal));
            bw.newLine();
            bw.newLine();

            bw.write("CATEGORY-WISE SUMMARY");
            bw.newLine();

            for (String cat : categoryMap.keySet()) {
                bw.write(cat + " -> " + String.format("%.2f", categoryMap.get(cat)));
                bw.newLine();
            }

            bw.newLine();
            bw.write("PERSONAL EXPENSE HISTORY");
            bw.newLine();

            for (int i = 0; i < personalExpenses.size(); i++) {

                Expense e = personalExpenses.get(i);

                bw.write(
                        "Expense ID: " + e.expenseId +
                        ", Title: " + e.title +
                        ", Amount: " + e.totalAmount +
                        ", Category: " + e.category +
                        ", Date: " + e.date +
                        ", Note: " + e.note
                );
                bw.newLine();
            }

            bw.newLine();
            bw.write("SETTLEMENT HISTORY");
            bw.newLine();

            for (int i = 0; i < mySettlements.size(); i++) {

                Settlement s = mySettlements.get(i);

                bw.write(
                        "Settlement ID: " + s.settlementId +
                        ", From: " + s.fromUser +
                        ", To: " + s.toUser +
                        ", Amount: " + s.amount +
                        ", Date: " + s.date +
                        ", Mode: " + s.mode
                );
                bw.newLine();
            }

            bw.newLine();
            bw.write("RECURRING EXPENSES");
            bw.newLine();

            for (int i = 0; i < AppData.recurringExpenses.size(); i++) {

                RecurringExpense r = AppData.recurringExpenses.get(i);

                if (r.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {
                    bw.write(
                            "ID: " + r.recurringId +
                            ", Title: " + r.title +
                            ", Amount: " + r.amount +
                            ", Category: " + r.category +
                            ", Frequency: " + r.frequency +
                            ", Next Due: " + r.nextDueDate
                    );
                    bw.newLine();
                }
            }

            System.out.println("Report exported successfully to: " + fileName);

        } catch (Exception e) {
            System.out.println("Error exporting report.");
        }
    }
}