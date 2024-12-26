package com.depot.pojo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Log {
    private static Log instance;
    private List<String> logs;
    private static final String LOG_FILE = "warehouse_log.txt";
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private Log() {
        logs = new ArrayList<>();
    }
    
    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }
    
    public void addLog(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s", timestamp, message);
        logs.add(logEntry);
        
        // 实时写入文件
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(logEntry + "\n");
        } catch (IOException e) {
            System.err.println("写入日志文件失败: " + e.getMessage());
        }
    }
    
    public String getLog() {
        StringBuilder sb = new StringBuilder();
        for (String log : logs) {
            sb.append(log).append("\n");
        }
        return sb.toString();
    }
    
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            for (String log : logs) {
                writer.println(log);
            }
        } catch (IOException e) {
            System.err.println("保存日志文件失败: " + e.getMessage());
        }
    }
    
    public void clear() {
        logs.clear();
        try {
            new FileWriter(LOG_FILE).close();
        } catch (IOException e) {
            System.err.println("清除日志文件失败: " + e.getMessage());
        }
    }
} 