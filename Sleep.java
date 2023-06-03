public class Sleep {

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException interruptedException) {
            System.out.println("ERRO - " + interruptedException);
        }
    }
}
