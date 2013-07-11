package com.mangoshine.doc.logging;

import com.mangoshine.doc.Config;

public class Logger {
    static public void log(String msg) {
        if (!Config.INSTANCE.isSilent()) {
            System.out.println(msg);
        }
    }
}