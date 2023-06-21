package com.lschaan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Log {
    private final String transmissor;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final Map<String, String> cores = new HashMap<String, String>() {{
        put("1", "\u001B[32m");
        put("2", "\u001B[34m");
        put("3", "\u001B[33m");
        put("4", "\u001B[31m");
        put("5", "\u001B[35m");
    }};

    public Log(String transmissor) {
        this.transmissor = transmissor;
    }

    public void log(String mensagem) {
        System.out.printf(getCor() + "%-6s %-60s %s\u001B[0m \n", "[" + transmissor + "]", mensagem, simpleDateFormat.format(new Date()));
    }

    private String getCor() {
        return cores.getOrDefault(transmissor, "");
    }
}
