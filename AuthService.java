import java.util.*;

public class AuthService {

    static void register(Scanner sc) {

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        if (AppData.findUserByUsername(username) != null) {
            System.out.println("User already exists.");
            return;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        System.out.print("Set overall monthly spending limit: ");
        double limit = InputUtil.readDouble(sc);

        int userId = AppData.users.size() + 1;

        User user = new User(userId, username, password, limit);

        AppData.users.add(user);
        AppData.saveAll();

        System.out.println("Registration successful.");
    }

    static User login(Scanner sc) {

        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        User user = AppData.findUserByUsername(username);

        if (user == null) {
            System.out.println("User not found.");
            return null;
        }

        if (!user.password.equals(password)) {
            System.out.println("Wrong password.");
            return null;
        }

        System.out.println("Login successful.");
        return user;
    }

    static boolean deleteCurrentUser(Scanner sc) {

        if (AppData.currentUser == null) {
            return false;
        }

        System.out.print("Type YES to delete your account: ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Delete cancelled.");
            return false;
        }

        String username = AppData.currentUser.username;

        // remove user and related data
        AppData.users.removeIf(u -> u.username.equalsIgnoreCase(username));

        AppData.recurringExpenses.removeIf(r ->
                r.ownerUsername.equalsIgnoreCase(username));

        AppData.expenses.removeIf(e ->
                e.ownerUsername.equalsIgnoreCase(username) ||
                e.paidBy.equalsIgnoreCase(username));

        for (int i = 0; i < AppData.groups.size(); i++) {
            Group g = AppData.groups.get(i);
            g.members.removeIf(m -> m.equalsIgnoreCase(username));
        }

        AppData.groups.removeIf(g ->
                g.adminUsername.equalsIgnoreCase(username) ||
                g.members.isEmpty());

        AppData.settlements.removeIf(s ->
                s.fromUser.equalsIgnoreCase(username) ||
                s.toUser.equalsIgnoreCase(username));

        AppData.saveAll();

        System.out.println("User deleted successfully.");
        return true;
    }
}