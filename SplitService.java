import java.util.*;

public class SplitService {

    static ArrayList<Split> equalSplit(ArrayList<String> members, double totalAmount) {

        ArrayList<Split> list = new ArrayList<>();

        double perHead = totalAmount / members.size();

        for (int i = 0; i < members.size(); i++) {
            String m = members.get(i);
            list.add(new Split(m, perHead));
        }

        return list;
    }

    static ArrayList<Split> exactSplit(ArrayList<String> members, Scanner sc, double totalAmount) {

        ArrayList<Split> list = new ArrayList<>();
        double sum = 0.0;

        for (int i = 0; i < members.size(); i++) {
            String m = members.get(i);

            System.out.print("Enter exact amount for " + m + ": ");
            double amt = InputUtil.readDouble(sc);

            list.add(new Split(m, amt));
            sum += amt;
        }

        if (Math.abs(sum - totalAmount) > 0.01) {
            System.out.println("Exact split total does not match expense total.");
            return null;
        }

        return list;
    }

    static ArrayList<Split> percentageSplit(ArrayList<String> members, Scanner sc, double totalAmount) {

        ArrayList<Split> list = new ArrayList<>();
        double totalPercent = 0.0;

        for (int i = 0; i < members.size(); i++) {
            String m = members.get(i);

            System.out.print("Enter percentage for " + m + ": ");
            double per = InputUtil.readDouble(sc);

            totalPercent += per;
            list.add(new Split(m, totalAmount * per / 100.0));
        }

        if (Math.abs(totalPercent - 100.0) > 0.01) {
            System.out.println("Percentages must sum to 100.");
            return null;
        }

        return list;
    }
}