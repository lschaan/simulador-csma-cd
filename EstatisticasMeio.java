public class EstatisticasMeio {

    private int mensagensTransmitidasComSucesso;
    private int conflitos;
    private boolean estaSendoLimpo;
    private final ControladorMeio controladorMeio = ControladorMeio.getInstancia();

    private static EstatisticasMeio instancia;

    public static EstatisticasMeio getInstancia() {
        if (instancia == null) {
            instancia = new EstatisticasMeio();
        }
        return instancia;
    }


    public void mostrarEstatisticasAtuais() {
        while (true) {
            System.out.println(controladorMeio.estaDisponivel());
            Sleep.sleep(500);
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        }
    }
}
