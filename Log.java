import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class Log {
    private final String transmissor;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    public Log(String transmissor) {
        this.transmissor = transmissor;
    }

    public void log(String mensagem) {
//        System.out.printf("%-5s %-60s %s\n", transmissor, mensagem, simpleDateFormat.format(new Date()));
    }

    public void erro(Exception exception) {
        System.out.println("ERRO - [" + transmissor + "] - " + exception);
    }
}
