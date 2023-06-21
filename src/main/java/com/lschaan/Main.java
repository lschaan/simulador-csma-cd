package com.lschaan;

import com.lschaan.csma.ControladorMeio;
import com.lschaan.csma.Transmissor;

public class Main {

    public static void main(String[] args) {
        ControladorMeio.getInstancia().iniciar();

        new Transmissor().start();
        new Transmissor().start();
    }
}
