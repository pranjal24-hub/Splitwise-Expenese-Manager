public class Split {

    String username;
    double amount;

    public Split(String username, double amount) {
        this.username = username;
        this.amount = amount;
    }

    // convert split to string format
    public String toDataString() {
        return username + ":" + amount;
    }

    // create split object from string
    public static Split fromDataString(String s) {

        String[] p = s.split(":");

        return new Split(
                p[0],
                Double.parseDouble(p[1])
        );
    }
}