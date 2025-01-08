package com.depot.pojo;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static Log instance;
    private StringBuffer logs;
    private static final String LOG_FILE = "warehouse_log.txt";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Log() {
        logs = new StringBuffer();
    }

    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public void addLog(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s\n", timestamp, message);
        logs.append(logEntry);

        // 实时写入文件
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(logEntry);
        } catch (IOException e) {
            System.err.println("写入日志文件失败: " + e.getMessage());
        }
    }

    public String getLog() {
        return logs.toString();
    }
}