import java.util.*;

public class User {

    int userId;
    String username;
    String password;
    double spendingLimit;

    HashMap<String, Double> categoryBudgets;

    public User(int userId, String username, String password, double spendingLimit) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.spendingLimit = spendingLimit;

        categoryBudgets = new HashMap<>();

        // default categories
        categoryBudgets.put("Food", 0.0);
        categoryBudgets.put("Travel", 0.0);
        categoryBudgets.put("Rent", 0.0);
        categoryBudgets.put("Shopping", 0.0);
        categoryBudgets.put("Bills", 0.0);
        categoryBudgets.put("Other", 0.0);
    }

    // convert budgets to string for saving in file
    public String budgetsToString() {
        StringBuilder sb = new StringBuilder();

        boolean first = true;

        for (String key : categoryBudgets.keySet()) {

            if (!first) {
                sb.append(";");
            }

            sb.append(key);
            sb.append(":");
            sb.append(categoryBudgets.get(key));

            first = false;
        }

        return sb.toString();
    }

    // load budgets from saved string
    public void loadBudgetsFromString(String data) {

        categoryBudgets.clear();

        // again putting default categories
        categoryBudgets.put("Food", 0.0);
        categoryBudgets.put("Travel", 0.0);
        categoryBudgets.put("Rent", 0.0);
        categoryBudgets.put("Shopping", 0.0);
        categoryBudgets.put("Bills", 0.0);
        categoryBudgets.put("Other", 0.0);

        if (data == null || data.trim().isEmpty()) {
            return;
        }

        String[] parts = data.split(";");

        for (int i = 0; i < parts.length; i++) {

            String[] p = parts[i].split(":");

            if (p.length == 2) {
                try {
                    double val = Double.parseDouble(p[1]);
                    categoryBudgets.put(p[0], val);
                } catch (Exception e) {
                    // ignore invalid values
                }
            }
        }
    }

    // convert full user to file format
    public String toFileString() {

        return userId + "," +
                InputUtil.safe(username) + "," +
                InputUtil.safe(password) + "," +
                spendingLimit + "," +
                budgetsToString();
    }

    // create user object from file line
    public static User fromFileString(String line) {

        String[] p = line.split(",", 5);

        User u = new User(
                Integer.parseInt(p[0]),
                p[1],
                p[2],
                Double.parseDouble(p[3])
        );

        if (p.length >= 5) {
            u.loadBudgetsFromString(p[4]);
        }

        return u;
    }
}