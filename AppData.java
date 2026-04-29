import java.util.*;

public class AppData {

    static ArrayList<User> users = new ArrayList<>();
    static ArrayList<Group> groups = new ArrayList<>();
    static ArrayList<Expense> expenses = new ArrayList<>();
    static ArrayList<Settlement> settlements = new ArrayList<>();
    static ArrayList<RecurringExpense> recurringExpenses = new ArrayList<>();

    static User currentUser = null;

    // load all data from files
    static void loadAll() {

        users = FileManager.loadUsers();
        groups = FileManager.loadGroups();
        expenses = FileManager.loadExpenses();
        settlements = FileManager.loadSettlements();
        recurringExpenses = FileManager.loadRecurringExpenses();
    }

    // save all data to files
    static void saveAll() {

        FileManager.saveUsers(users);
        FileManager.saveGroups(groups);
        FileManager.saveExpenses(expenses);
        FileManager.saveSettlements(settlements);
        FileManager.saveRecurringExpenses(recurringExpenses);
    }

    // find user by username
    static User findUserByUsername(String username) {

        for (int i = 0; i < users.size(); i++) {

            User u = users.get(i);

            if (u.username.equalsIgnoreCase(username)) {
                return u;
            }
        }

        return null;
    }

    // find group using id
    static Group findGroupById(int groupId) {

        for (int i = 0; i < groups.size(); i++) {

            Group g = groups.get(i);

            if (g.groupId == groupId) {
                return g;
            }
        }

        return null;
    }

    // find expense using id
    static Expense findExpenseById(int expenseId) {

        for (int i = 0; i < expenses.size(); i++) {

            Expense e = expenses.get(i);

            if (e.expenseId == expenseId) {
                return e;
            }
        }

        return null;
    }
}