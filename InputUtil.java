import java.util.*;

public class InputUtil {

    public static int readInt(Scanner sc) {

        while (true) {
            try {
                String s = sc.nextLine().trim();
                int value = Integer.parseInt(s);
                return value;
            }
            catch (Exception e) {
                System.out.print("Enter valid integer: ");
            }
        }
    }

    public static double readDouble(Scanner sc) {

        while (true) {
            try {
                String s = sc.nextLine().trim();
                double value = Double.parseDouble(s);
                return value;
            }
            catch (Exception e) {
                System.out.print("Enter valid number: ");
            }
        }
    }

    public static String safe(String s) {

        if (s == null) {
            return "";
        }

        s = s.replace(",", " ");
        s = s.replace("|", " ");
        s = s.replace(";", " ");

        return s.trim();
    }
}