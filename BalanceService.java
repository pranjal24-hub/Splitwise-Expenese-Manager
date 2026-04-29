import java.util.*;

public class BalanceService {

    static HashMap<String, Double> calculateNetBalancesForGroup(int groupId) {

        HashMap<String, Double> net = new HashMap<>();

        Group g = AppData.findGroupById(groupId);

        if (g == null) return net;

        // initialize all members with 0 balance
        for (int i = 0; i < g.members.size(); i++) {
            String m = g.members.get(i);
            net.put(m, 0.0);
        }

        // process expenses
        for (int i = 0; i < AppData.expenses.size(); i++) {

            Expense e = AppData.expenses.get(i);

            if (e.groupId == groupId) {

                net.put(e.paidBy,
                        net.getOrDefault(e.paidBy, 0.0) + e.totalAmount);

                for (int j = 0; j < e.splits.size(); j++) {

                    Split s = e.splits.get(j);

                    net.put(s.username,
                            net.getOrDefault(s.username, 0.0) - s.amount);
                }
            }
        }

        // process settlements
        for (int i = 0; i < AppData.settlements.size(); i++) {

            Settlement s = AppData.settlements.get(i);

            if (s.groupId == groupId) {

                net.put(s.fromUser,
                        net.getOrDefault(s.fromUser, 0.0) + s.amount);

                net.put(s.toUser,
                        net.getOrDefault(s.toUser, 0.0) - s.amount);
            }
        }

        return net;
    }

    static void showGroupBalances(Scanner sc) {

        GroupService.viewMyGroups();

        System.out.print("Enter group ID: ");
        int groupId = InputUtil.readInt(sc);

        Group g = AppData.findGroupById(groupId);

        if (g == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!g.members.contains(AppData.currentUser.username)) {
            System.out.println("You are not in this group.");
            return;
        }

        HashMap<String, Double> net = calculateNetBalancesForGroup(groupId);

        System.out.println("\n---- GROUP BALANCES ----");

        for (String user : net.keySet()) {

            double value = net.get(user);

            if (value > 0.01) {
                System.out.println(user + " should receive Rs. " +
                        String.format("%.2f", value));
            }
            else if (value < -0.01) {
                System.out.println(user + " should pay Rs. " +
                        String.format("%.2f", -value));
            }
            else {
                System.out.println(user + " is settled.");
            }
        }
    }

    static void showWhoOwesWhom(Scanner sc) {

        GroupService.viewMyGroups();

        System.out.print("Enter group ID: ");
        int groupId = InputUtil.readInt(sc);

        Group g = AppData.findGroupById(groupId);

        if (g == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!g.members.contains(AppData.currentUser.username)) {
            System.out.println("You are not in this group.");
            return;
        }

        HashMap<String, Double> net = calculateNetBalancesForGroup(groupId);

        ArrayList<String> debtors = new ArrayList<>();
        ArrayList<Double> debtorAmt = new ArrayList<>();

        ArrayList<String> creditors = new ArrayList<>();
        ArrayList<Double> creditorAmt = new ArrayList<>();

        for (String user : net.keySet()) {

            double amt = net.get(user);

            if (amt < -0.01) {
                debtors.add(user);
                debtorAmt.add(-amt);
            }
            else if (amt > 0.01) {
                creditors.add(user);
                creditorAmt.add(amt);
            }
        }

        System.out.println("\n---- WHO OWES WHOM ----");

        if (debtors.isEmpty() && creditors.isEmpty()) {
            System.out.println("All settled.");
            return;
        }

        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {

            double x = Math.min(debtorAmt.get(i), creditorAmt.get(j));

            System.out.println(
                    debtors.get(i) + " owes " +
                    creditors.get(j) + " Rs. " +
                    String.format("%.2f", x)
            );

            debtorAmt.set(i, debtorAmt.get(i) - x);
            creditorAmt.set(j, creditorAmt.get(j) - x);

            if (debtorAmt.get(i) < 0.01) i++;
            if (creditorAmt.get(j) < 0.01) j++;
        }
    }

    static void settleUp(Scanner sc) {

        GroupService.viewMyGroups();

        System.out.print("Enter group ID: ");
        int groupId = InputUtil.readInt(sc);

        Group g = AppData.findGroupById(groupId);

        if (g == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!g.members.contains(AppData.currentUser.username)) {
            System.out.println("You are not in this group.");
            return;
        }

        System.out.print("Enter from user: ");
        String fromUser = sc.nextLine().trim();

        System.out.print("Enter to user: ");
        String toUser = sc.nextLine().trim();

        if (!g.members.contains(fromUser) || !g.members.contains(toUser)) {
            System.out.println("Both users must be group members.");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = InputUtil.readDouble(sc);

        System.out.print("Enter date (yyyy-mm-dd): ");
        String date = sc.nextLine().trim();

        System.out.print("Enter mode (Cash/UPI/Bank): ");
        String mode = sc.nextLine().trim();

        Settlement s = new Settlement(
                AppData.settlements.size() + 1,
                groupId,
                fromUser,
                toUser,
                amount,
                date,
                mode
        );

        AppData.settlements.add(s);
        AppData.saveAll();

        System.out.println("Settlement added successfully.");
    }

    static void showSettlementHistory(Scanner sc) {

        System.out.println("\n1. View all my settlement history");
        System.out.println("2. View group settlement history");
        System.out.print("Enter choice: ");

        int ch = InputUtil.readInt(sc);

        if (ch == 1) {

            boolean found = false;

            for (int i = 0; i < AppData.settlements.size(); i++) {

                Settlement s = AppData.settlements.get(i);

                if (s.fromUser.equalsIgnoreCase(AppData.currentUser.username) ||
                    s.toUser.equalsIgnoreCase(AppData.currentUser.username)) {

                    printSettlement(s);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No settlement history found.");
            }
        }
        else if (ch == 2) {

            GroupService.viewMyGroups();

            System.out.print("Enter group ID: ");
            int groupId = InputUtil.readInt(sc);

            Group g = AppData.findGroupById(groupId);

            if (g == null) {
                System.out.println("Group not found.");
                return;
            }

            if (!g.members.contains(AppData.currentUser.username)) {
                System.out.println("You are not in this group.");
                return;
            }

            boolean found = false;

            for (int i = 0; i < AppData.settlements.size(); i++) {

                Settlement s = AppData.settlements.get(i);

                if (s.groupId == groupId) {
                    printSettlement(s);
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No settlement history found for this group.");
            }
        }
        else {
            System.out.println("Invalid choice.");
        }
    }

    static void printSettlement(Settlement s) {

        System.out.println("Settlement ID: " + s.settlementId);
        System.out.println("Group ID: " + s.groupId);
        System.out.println("From: " + s.fromUser);
        System.out.println("To: " + s.toUser);
        System.out.println("Amount: " + s.amount);
        System.out.println("Date: " + s.date);
        System.out.println("Mode: " + s.mode);
        System.out.println("--------------------------");
    }
}