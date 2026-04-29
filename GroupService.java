import java.util.*;

public class GroupService {

    static void createGroup(Scanner sc) {

        System.out.print("Enter group name: ");
        String groupName = sc.nextLine().trim();

        if (groupName.isEmpty()) {
            System.out.println("Group name cannot be empty.");
            return;
        }

        ArrayList<String> members = new ArrayList<>();

        // add current user as first member
        members.add(AppData.currentUser.username);

        System.out.print("How many members do you want to add now (excluding yourself)? ");
        int n = InputUtil.readInt(sc);

        for (int i = 0; i < n; i++) {

            System.out.print("Enter username of member " + (i + 1) + ": ");
            String uname = sc.nextLine().trim();

            User u = AppData.findUserByUsername(uname);

            if (u != null && !members.contains(u.username)) {
                members.add(u.username);
            } else {
                System.out.println("User not found or already added: " + uname);
            }
        }

        int groupId = AppData.groups.size() + 1;

        Group g = new Group(
                groupId,
                groupName,
                AppData.currentUser.username,
                members
        );

        AppData.groups.add(g);
        AppData.saveAll();

        System.out.println("Group created successfully. Group ID = " + groupId);
    }

    static void addMembersToGroup(Scanner sc) {

        viewMyGroups();

        System.out.print("Enter group ID: ");
        int groupId = InputUtil.readInt(sc);

        Group g = AppData.findGroupById(groupId);

        if (g == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!g.adminUsername.equalsIgnoreCase(AppData.currentUser.username)) {
            System.out.println("Only admin can add members.");
            return;
        }

        System.out.print("How many members to add? ");
        int n = InputUtil.readInt(sc);

        for (int i = 0; i < n; i++) {

            System.out.print("Enter username: ");
            String uname = sc.nextLine().trim();

            User u = AppData.findUserByUsername(uname);

            if (u == null) {
                System.out.println("User not found.");
            }
            else if (g.members.contains(u.username)) {
                System.out.println("User already in group.");
            }
            else {
                g.members.add(u.username);
                System.out.println("Added: " + u.username);
            }
        }

        AppData.saveAll();
    }

    static void viewMyGroups() {

        System.out.println("\n---- MY GROUPS ----");

        boolean found = false;

        for (int i = 0; i < AppData.groups.size(); i++) {

            Group g = AppData.groups.get(i);

            if (g.members.contains(AppData.currentUser.username)) {

                found = true;

                System.out.println("Group ID: " + g.groupId);
                System.out.println("Group Name: " + g.groupName);
                System.out.println("Admin: " + g.adminUsername);
                System.out.println("Members: " + g.members);
                System.out.println("----------------------");
            }
        }

        if (!found) {
            System.out.println("No groups found.");
        }
    }

    static void deleteGroup(Scanner sc) {

        viewMyGroups();

        System.out.print("Enter group ID to delete: ");
        int groupId = InputUtil.readInt(sc);

        Group g = AppData.findGroupById(groupId);

        if (g == null) {
            System.out.println("Group not found.");
            return;
        }

        if (!g.adminUsername.equalsIgnoreCase(AppData.currentUser.username)) {
            System.out.println("Only admin can delete group.");
            return;
        }

        AppData.groups.remove(g);

        AppData.expenses.removeIf(e -> e.groupId == groupId);
        AppData.settlements.removeIf(s -> s.groupId == groupId);

        AppData.saveAll();

        System.out.println("Group deleted successfully.");
    }
}