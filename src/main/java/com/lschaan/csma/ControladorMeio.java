package com.lschaan.csma;

import com.lschaan.utils.Log;
import com.lschaan.utils.Sleep;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ControladorMeio {
    private static ControladorMeio instancia;
    private final Log log = new Log("MEIO");
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
        CompletableFuture.runAsync(this::monitorarColisoes);
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
    Monitora infinitamente a existência de colisões no meio a cada 100ms
    Caso exista, notifica os Transmissores e limpa o meio
     */
    private void monitorarColisoes() {
        while (true) {
            if (existeColisao()) {
                log("COLISÃO NO MEIO - " + meio);
                notificarColisao();
                limparMeio();
            }

            Sleep.sleep(100);
        }
    }

    private boolean existeColisao() {
        return meio.size() > 1;
    }

    //Notifica os transmissores da existência de colisão no meio
    private void notificarColisao() {
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
