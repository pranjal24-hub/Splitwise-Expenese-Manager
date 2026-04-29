public class AutoSaveThread extends Thread {
    public void run() {
        while (true) {
            try {
                AppData.saveAll();
                Thread.sleep(10000);
            } catch (Exception e) {
                System.out.println("Auto-save error.");
            }
        }
    }
}