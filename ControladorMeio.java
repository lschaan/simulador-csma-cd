import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ControladorMeio {
    private static ControladorMeio instancia;
    private final Log log = new Log("MEIO");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private final EstatisticasMeio estatisticasMeio = new EstatisticasMeio();
    private final List<Transmissor> transmissores = new ArrayList<>();
    private List<String> meio = new ArrayList<>();
    private int proximoIdTransmissor = 1;
    private boolean estaSendoLimpo;


    public static synchronized ControladorMeio getInstancia() {
        if (instancia == null) {
            instancia = new ControladorMeio();
        }
        return instancia;
    }

    public void iniciar() {
        CompletableFuture.runAsync(this::monitorarConflitos);
    }

    public void ingressarAoMeio(Transmissor transmissor) {
        transmissor.setId(proximoIdTransmissor++);
        transmissores.add(transmissor);
    }

    public synchronized void enviarMensagem(String mensagem) {
        meio.add(mensagem);
        log("Mensagem recebida! - " + mensagem);
        CompletableFuture.runAsync(() -> removerMensagem(mensagem));
    }

    public boolean estaDisponivel() {
        return meio.isEmpty();
    }

    public boolean estaSendoLimpo() {
        return estaSendoLimpo;
    }

    /*
    Monitora infinitamente a existência de conflitos no meio a cada 100ms
    Caso exista, notifica os Transmissores e limpa o meio
     */
    private void monitorarConflitos() {
        while (true) {
            if (existeConflito()) {
                log("CONFLITO NO MEIO - " + meio);
                notificarConflito();
                limparMeio();
                estatisticasMeio.totalConflitos++;
                estatisticasMeio.ultimoConflito = new Date();
            }

            Sleep.sleep(100);
        }
    }

    private boolean existeConflito() {
        return meio.size() > 1;
    }

    //Notifica os transmissores da existência de conflito no meio
    private void notificarConflito() {
        transmissores.forEach(transmissor -> CompletableFuture.runAsync(transmissor::onColisao));
    }

    private void limparMeio() {
        log("Iniciando limpeza do meio!");
        estaSendoLimpo = true;
        Sleep.sleep(1500);

        meio = new ArrayList<>();
        estaSendoLimpo = false;
        log("Limpeza do meio encerrada!");
    }

    //Mensagem fica 4s no meio antes de ser "entregue"
    private void removerMensagem(String mensagem) {
        Sleep.sleep(4000);
        meio.remove(mensagem);
        log("Mensagem removida do meio! " + mensagem);
    }

    private void log(String message) {
        log.log(message);
    }
}
