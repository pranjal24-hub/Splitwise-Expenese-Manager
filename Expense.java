import java.util.*;

public class Expense {

    int expenseId;
    int groupId;   // 0 means personal expense

    String ownerUsername;   // for personal expense
    String title;
    String paidBy;

    double totalAmount;

    String splitType;   // EQUAL / EXACT / PERCENTAGE / PERSONAL
    String category;
    String date;        // yyyy-mm-dd
    String note;

    ArrayList<Split> splits;

    public Expense(int expenseId, int groupId, String ownerUsername, String title,
                   String paidBy, double totalAmount, String splitType,
                   String category, String date, String note,
                   ArrayList<Split> splits) {

        this.expenseId = expenseId;
        this.groupId = groupId;
        this.ownerUsername = ownerUsername;
        this.title = title;
        this.paidBy = paidBy;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.category = category;
        this.date = date;
        this.note = note;
        this.splits = splits;
    }

    // convert splits list to string
    public String splitsToString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < splits.size(); i++) {

            sb.append(splits.get(i).toDataString());

            if (i != splits.size() - 1) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    // convert full expense to file format
    public String toFileString() {

        return expenseId + "," +
                groupId + "," +
                InputUtil.safe(ownerUsername) + "," +
                InputUtil.safe(title) + "," +
                InputUtil.safe(paidBy) + "," +
                totalAmount + "," +
                InputUtil.safe(splitType) + "," +
                InputUtil.safe(category) + "," +
                InputUtil.safe(date) + "," +
                InputUtil.safe(note) + "," +
                splitsToString();
    }

    // create expense object from file line
    public static Expense fromFileString(String line) {

        String[] p = line.split(",", 11);

        ArrayList<Split> splits = new ArrayList<>();

        if (p.length >= 11 && !p[10].trim().isEmpty()) {

            String[] arr = p[10].split(";");

            for (int i = 0; i < arr.length; i++) {

                String s = arr[i].trim();

                if (!s.isEmpty()) {
                    splits.add(Split.fromDataString(s));
                }
            }
        }

        return new Expense(
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                p[2],
                p[3],
                p[4],
                Double.parseDouble(p[5]),
                p[6],
                p[7],
                p[8],
                p[9],
                splits
        );
    }
}