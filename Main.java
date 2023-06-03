public class Main {

    public static void main(String[] args) {
        ControladorMeio.getInstancia().iniciar();

        new Transmissor().start();
        new Transmissor().start();
        EstatisticasMeio.getInstancia().mostrarEstatisticasAtuais();
    }
}
