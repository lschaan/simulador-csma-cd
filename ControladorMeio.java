import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ControladorMeio {
    private static ControladorMeio instancia;
    private List<String> meio = new ArrayList<>();
    private final List<Transmissor> transmissores = new ArrayList<>();
    private int proximoIdTransmissor = 1;
    private boolean estaSendoLimpo;
    private Log log;

    public static synchronized ControladorMeio getInstancia() {
        if (instancia == null) {
            instancia = new ControladorMeio();
        }
        return instancia;
    }

    public void iniciar() {
        this.log = new Log("MEIO");
        CompletableFuture.runAsync(this::monitorarConflitos);
    }

    public void ingressarAoMeio(Transmissor transmissor) {
        transmissor.setId(String.valueOf(proximoIdTransmissor++));
        transmissores.add(transmissor);
    }

    public void enviarMensagem(String mensagem) {
        meio.add(mensagem);
        log("Mensagem recebida! - " + meio);
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
            }

            Sleep.sleep(100);
        }
    }

    private boolean existeConflito() {
        return meio.size() > 1;
    }

    //Notifica os transmissores da existência de conflito no meio
    private void notificarConflito() {
        transmissores.forEach(Transmissor::onColisao);
    }

    private void limparMeio() {
        log("Iniciando limpeza do meio!");
        estaSendoLimpo = true;
        Sleep.sleep(2500);

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
