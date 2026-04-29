import java.util.*;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        AppData.loadAll();

        AutoSaveThread auto = new AutoSaveThread();
        auto.setDaemon(true);
        auto.start();

        while (true) {
            System.out.println("\n==============================");
            System.out.println(" SMART EXPENSE SHARING SYSTEM ");
            System.out.println("==============================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int ch = InputUtil.readInt(sc);

            if (ch == 1) {
                AuthService.register(sc);
            }
            else if (ch == 2) {
                User user = AuthService.login(sc);

                if (user != null) {
                    AppData.currentUser = user;
                    userMenu();
                }
            }
            else if (ch == 3) {
                AppData.saveAll();
                System.out.println("Thank you for using the project.");
                break;
            }
            else {
                System.out.println("Invalid choice.");
            }
        }
    }

    static void userMenu() {

        while (true) {
            System.out.println("\n========================================");
            System.out.println("Logged in as: " + AppData.currentUser.username);
            System.out.println("========================================");
            System.out.println("1. Create Group");
            System.out.println("2. Add Members to Group");
            System.out.println("3. View My Groups");
            System.out.println("4. Add Group Expense");
            System.out.println("5. Add Personal Expense");
            System.out.println("6. Show Expense History");
            System.out.println("7. Search Expense by Title/Category");
            System.out.println("8. Show Group Balances");
            System.out.println("9. Show Who Owes Whom");
            System.out.println("10. Settle Up");
            System.out.println("11. View Settlement History");
            System.out.println("12. Set Category Budget");
            System.out.println("13. View Budget Status");
            System.out.println("14. Monthly Spending Summary");
            System.out.println("15. Add Recurring Expense");
            System.out.println("16. View Recurring Expenses");
            System.out.println("17. Export Report to TXT");
            System.out.println("18. Delete Group");
            System.out.println("19. Delete My User");
            System.out.println("20. Logout");
            System.out.print("Enter choice: ");

            int ch = InputUtil.readInt(sc);

            if (ch == 1) {
                GroupService.createGroup(sc);
            }
            else if (ch == 2) {
                GroupService.addMembersToGroup(sc);
            }
            else if (ch == 3) {
                GroupService.viewMyGroups();
            }
            else if (ch == 4) {
                ExpenseService.addGroupExpense(sc);
            }
            else if (ch == 5) {
                ExpenseService.addPersonalExpense(sc);
            }
            else if (ch == 6) {
                ExpenseService.showExpenseHistory(sc);
            }
            else if (ch == 7) {
                ExpenseService.searchExpense(sc);
            }
            else if (ch == 8) {
                BalanceService.showGroupBalances(sc);
            }
            else if (ch == 9) {
                BalanceService.showWhoOwesWhom(sc);
            }
            else if (ch == 10) {
                BalanceService.settleUp(sc);
            }
            else if (ch == 11) {
                BalanceService.showSettlementHistory(sc);
            }
            else if (ch == 12) {
                BudgetService.setCategoryBudget(sc);
            }
            else if (ch == 13) {
                BudgetService.viewBudgetStatus();
            }
            else if (ch == 14) {
                ReportService.monthlySpendingSummary(sc);
            }
            else if (ch == 15) {
                ExpenseService.addRecurringExpense(sc);
            }
            else if (ch == 16) {
                ExpenseService.viewRecurringExpenses();
            }
            else if (ch == 17) {
                ReportService.exportReportToTxt(sc);
            }
            else if (ch == 18) {
                GroupService.deleteGroup(sc);
            }
            else if (ch == 19) {
                boolean deleted = AuthService.deleteCurrentUser(sc);

                if (deleted) {
                    AppData.currentUser = null;
                    return;
                }
            }
            else if (ch == 20) {
                AppData.currentUser = null;
                AppData.saveAll();
                System.out.println("Logged out.");
                return;
            }
            else {
                System.out.println("Invalid choice.");
            }
        }
    }
}