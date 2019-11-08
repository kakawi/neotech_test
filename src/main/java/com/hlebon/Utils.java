package com.hlebon;

public class Utils {
    public static void delay(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}
