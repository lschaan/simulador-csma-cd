import java.util.Random;

public class Transmissor extends Thread {
    private String id;
    private ControladorMeio controladorMeio;
    private volatile boolean backoffOcorrendo;
    private Integer proximoNBackoff;
    private Log log;

    @Override
    public void run() {
        this.controladorMeio = ControladorMeio.getInstancia();
        controladorMeio.ingressarAoMeio(this);
        log = new Log(id);
        enviarMensagensAleatoriamente();
    }

    public void onColisao() {
        log("Notificado da existência de uma colisão no meio.");
        iniciarBackoff();
    }

    private void enviarMensagensAleatoriamente() {
        log("Enviando mensagens aleatoriamente!");
        while (true) {
            int segundosAteProximaMensagem = new Random().nextInt(10) + 1; //Entre 1 e 10 segundos
            log("Aguardando " + segundosAteProximaMensagem + " segundos até o próximo envio.");
            Sleep.sleep(segundosAteProximaMensagem * 1000);

            while (backoffOcorrendo) {
                log("Ia enviar mensagem, mas backoff está ocorrendo!");
                Thread.onSpinWait();
            }
            enviarMensagem();
        }
    }

    private void enviarMensagem() {
        log("Iniciando sensing... ");

        long tempoInicioSensingMs = System.currentTimeMillis();
        long duracaoSensingMs = 3500;
        boolean ocorreuProblema = false;

        //Realiza sensing, até o tempo finalizar OU houver alguma mensagem no meio
        while (System.currentTimeMillis() - tempoInicioSensingMs < duracaoSensingMs) {
            //Caso encontre colisão, encerra o sensing. Inicia o backoff
            if (controladorMeio.estaSendoLimpo()) {
                ocorreuProblema = true;
            }

            //Caso exista mensagem no meio, reinicia o sensing
            if (!controladorMeio.estaDisponivel()) {
                log("Meio não está disponível! Reiniciando sensing...");
                tempoInicioSensingMs = System.currentTimeMillis();
            }
            Sleep.sleep(750);//750ms para diminuir o impacto computacional do busy-waiting
        }

        if (ocorreuProblema) {
            log("Problema encontrado no meio! Sensing encerrado antecipadamente... ");
            return;
        }

        log("Sensing encerrado! Enviando nova mensagem... ");
        controladorMeio.enviarMensagem(getMensagem());
    }

    private void iniciarBackoff() {
        if (proximoNBackoff == null || proximoNBackoff > N_MINIMO_BACKOFF) {
            proximoNBackoff = new Random().nextInt(N_MAXIMO_BACKOFF) + N_MAXIMO_BACKOFF;
        }

        double tempoBackoffMs = Math.pow(2, proximoNBackoff) * 1000;
        log("Iniciando backoff! n=" + proximoNBackoff + ", tempo=" + tempoBackoffMs);
        Sleep.sleep((long) tempoBackoffMs);

        log("Backoff encerrado!");
        proximoNBackoff++;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String getMensagem() {
        return "[id=" + id + "]";
    }

    private static final int N_MINIMO_BACKOFF = 1;
    private static final int N_MAXIMO_BACKOFF = 5;

    private void log(String message) {
        log.log(message);
    }
}
