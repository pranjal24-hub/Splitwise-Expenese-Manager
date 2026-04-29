import java.util.*;

public class Group {

    int groupId;
    String groupName;
    String adminUsername;

    ArrayList<String> members;

    public Group(int groupId, String groupName, String adminUsername, ArrayList<String> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.adminUsername = adminUsername;
        this.members = members;
    }

    // convert group to string for saving in file
    public String toFileString() {

        String memberString = String.join(";", members);

        return groupId + "," +
                InputUtil.safe(groupName) + "," +
                InputUtil.safe(adminUsername) + "," +
                memberString;
    }

    // create group object from file line
    public static Group fromFileString(String line) {

        String[] p = line.split(",", 4);

        ArrayList<String> members = new ArrayList<>();

        if (p.length >= 4 && !p[3].trim().isEmpty()) {

            String[] arr = p[3].split(";");

            for (int i = 0; i < arr.length; i++) {

                String s = arr[i].trim();

                if (!s.isEmpty()) {
                    members.add(s);
                }
            }
        }

        return new Group(
                Integer.parseInt(p[0]),
                p[1],
                p[2],
                members
        );
    }
}