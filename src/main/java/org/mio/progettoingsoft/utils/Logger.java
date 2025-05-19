package org.mio.progettoingsoft.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    public enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    private static Level minLevel = Level.DEBUG;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ANSI colors
    private static final String RESET  = "\u001B[0m";
    private static final String GRAY   = "\u001B[90m";
    private static final String BLUE   = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED    = "\u001B[31m";

    // -------- Static configuration --------
    public static void setMinLevel(Level level) {
        minLevel = level;
    }

    // -------- Static logging methods --------
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warning(String message) {
        log(Level.WARNING, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    private static void log(Level level, String message) {
        if (level.ordinal() < minLevel.ordinal()) return;

        String timestamp = LocalDateTime.now().format(formatter);
        String thread = Thread.currentThread().getName();
        String caller = getCallerClassName();
        String color = getColor(level);

        String logLine = String.format("%s[%s] [%s] [%s] [%s] %s%s",
                color, timestamp, thread, level, caller, message, RESET);

        if (level == Level.ERROR) {
            System.err.println(logLine);
        } else {
            System.out.println(logLine);
        }
    }

    private static String getCallerClassName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement el : stack) {
            if (!el.getClassName().equals(Logger.class.getName()) && !el.getClassName().startsWith("java.lang")) {
                String fullClassName = el.getClassName();
                int lastDot = fullClassName.lastIndexOf('.');
                return lastDot == -1 ? fullClassName : fullClassName.substring(lastDot + 1);
            }
        }
        return "UnknownClass";
    }

    private static String getColor(Level level) {
        return switch (level) {
            case DEBUG -> GRAY;
            case INFO -> BLUE;
            case WARNING -> YELLOW;
            case ERROR -> RED;
        };
    }
}

