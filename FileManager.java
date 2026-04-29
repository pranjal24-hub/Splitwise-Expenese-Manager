import java.io.*;
import java.util.*;

public class FileManager implements FileOperations {

    static final String USERS_FILE = "users.txt";
    static final String GROUPS_FILE = "groups.txt";
    static final String EXPENSES_FILE = "expenses.txt";
    static final String SETTLEMENTS_FILE = "settlements.txt";
    static final String RECURRING_FILE = "recurring.txt";

    @Override
    public void saveData() {
        AppData.saveAll();
    }

    @Override
    public void loadData() {
        AppData.loadAll();
    }

    static ArrayList<User> loadUsers() {

        ArrayList<User> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(User.fromFileString(line));
                }
            }

        } catch (Exception e) {
        }

        return list;
    }

    static void saveUsers(ArrayList<User> list) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE))) {

            for (int i = 0; i < list.size(); i++) {
                User u = list.get(i);
                bw.write(u.toFileString());
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Error saving users.");
        }
    }

    static ArrayList<Group> loadGroups() {

        ArrayList<Group> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(GROUPS_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(Group.fromFileString(line));
                }
            }

        } catch (Exception e) {
        }

        return list;
    }

    static void saveGroups(ArrayList<Group> list) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(GROUPS_FILE))) {

            for (int i = 0; i < list.size(); i++) {
                Group g = list.get(i);
                bw.write(g.toFileString());
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Error saving groups.");
        }
    }

    static ArrayList<Expense> loadExpenses() {

        ArrayList<Expense> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(EXPENSES_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(Expense.fromFileString(line));
                }
            }

        } catch (Exception e) {
        }

        return list;
    }

    static void saveExpenses(ArrayList<Expense> list) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EXPENSES_FILE))) {

            for (int i = 0; i < list.size(); i++) {
                Expense e = list.get(i);
                bw.write(e.toFileString());
                bw.newLine();
            }

        } catch (Exception ex) {
            System.out.println("Error saving expenses.");
        }
    }

    static ArrayList<Settlement> loadSettlements() {

        ArrayList<Settlement> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(SETTLEMENTS_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(Settlement.fromFileString(line));
                }
            }

        } catch (Exception e) {
        }

        return list;
    }

    static void saveSettlements(ArrayList<Settlement> list) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SETTLEMENTS_FILE))) {

            for (int i = 0; i < list.size(); i++) {
                Settlement s = list.get(i);
                bw.write(s.toFileString());
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Error saving settlements.");
        }
    }

    static ArrayList<RecurringExpense> loadRecurringExpenses() {

        ArrayList<RecurringExpense> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(RECURRING_FILE))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    list.add(RecurringExpense.fromFileString(line));
                }
            }

        } catch (Exception e) {
        }

        return list;
    }

    static void saveRecurringExpenses(ArrayList<RecurringExpense> list) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECURRING_FILE))) {

            for (int i = 0; i < list.size(); i++) {
                RecurringExpense r = list.get(i);
                bw.write(r.toFileString());
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("Error saving recurring expenses.");
        }
    }
}