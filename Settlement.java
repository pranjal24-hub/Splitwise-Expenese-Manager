public class Settlement {

    int settlementId;
    int groupId;

    String fromUser;
    String toUser;

    double amount;

    String date;
    String mode;

    public Settlement(int settlementId, int groupId, String fromUser,
                      String toUser, double amount, String date, String mode) {

        this.settlementId = settlementId;
        this.groupId = groupId;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
        this.date = date;
        this.mode = mode;
    }

    // convert settlement to file format
    public String toFileString() {

        return settlementId + "," +
                groupId + "," +
                InputUtil.safe(fromUser) + "," +
                InputUtil.safe(toUser) + "," +
                amount + "," +
                InputUtil.safe(date) + "," +
                InputUtil.safe(mode);
    }

    // create object from file line
    public static Settlement fromFileString(String line) {

        String[] p = line.split(",", 7);

        return new Settlement(
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                p[2],
                p[3],
                Double.parseDouble(p[4]),
                p[5],
                p[6]
        );
    }
}