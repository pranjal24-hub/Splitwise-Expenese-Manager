public class RecurringExpense {

    int recurringId;
    String ownerUsername;

    String title;
    double amount;

    String category;
    String frequency;   // MONTHLY / WEEKLY

    String nextDueDate;
    String note;

    public RecurringExpense(int recurringId, String ownerUsername, String title,
                            double amount, String category, String frequency,
                            String nextDueDate, String note) {

        this.recurringId = recurringId;
        this.ownerUsername = ownerUsername;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
        this.note = note;
    }

    // convert recurring expense to file format
    public String toFileString() {

        return recurringId + "," +
                InputUtil.safe(ownerUsername) + "," +
                InputUtil.safe(title) + "," +
                amount + "," +
                InputUtil.safe(category) + "," +
                InputUtil.safe(frequency) + "," +
                InputUtil.safe(nextDueDate) + "," +
                InputUtil.safe(note);
    }

    // create object from file line
    public static RecurringExpense fromFileString(String line) {

        String[] p = line.split(",", 8);

        return new RecurringExpense(
                Integer.parseInt(p[0]),
                p[1],
                p[2],
                Double.parseDouble(p[3]),
                p[4],
                p[5],
                p[6],
                p[7]
        );
    }
}