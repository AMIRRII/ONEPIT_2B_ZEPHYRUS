package bhrms;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseBackup {
    
    private static final String DATABASE_NAME = "bhrms_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String DB_HOST = "localhost";
    private static final int DB_PORT = 3306;
    
    public boolean createBackup(String backupFilePath) {
    System.out.println("=== Starting Backup Process ===");
    System.out.println("Target: " + backupFilePath);
    
    // First try using mysqldump (much faster)
    System.out.println("\nTrying mysqldump backup...");
    if (createBackupWithMysqldump(backupFilePath)) {
        System.out.println("✓ mysqldump backup successful");
        return true;
    }
    
    System.out.println("\nTrying Java backup...");
    if (createBackupWithJava(backupFilePath)) {
        System.out.println("✓ Java backup successful");
        return true;
    }
    
    System.out.println("\nBoth methods failed, trying simple backup...");
    if (createSimpleBackup(backupFilePath)) {
        System.out.println("✓ Simple backup successful");
        return true;
    }
    
    System.out.println("✗ All backup methods failed!");
    return false;
}

        // Fast backup using mysqldump
    private boolean createBackupWithMysqldump(String backupFilePath) {
    System.out.println("Attempting mysqldump backup to: " + backupFilePath);
    
    try {
        String mysqldumpPath = findMysqldumpPath();
        if (mysqldumpPath == null) {
            System.out.println("ERROR: mysqldump not found!");
            return false;
        }
        
        System.out.println("Found mysqldump at: " + mysqldumpPath);
        
        // Check if we can execute it
        File mysqldumpFile = new File(mysqldumpPath);
        if (!mysqldumpFile.canExecute()) {
            System.out.println("ERROR: Cannot execute mysqldump at: " + mysqldumpPath);
            return false;
        }
        
        // Build command
        List<String> command = new ArrayList<>();
        command.add(mysqldumpPath);
        command.add("--user=" + DB_USER);
        if (!DB_PASSWORD.isEmpty()) {
            command.add("--password=" + DB_PASSWORD);
        }
        command.add("--host=" + DB_HOST);
        command.add("--port=" + String.valueOf(DB_PORT));
        command.add("--single-transaction");
        command.add("--quick");
        command.add("--lock-tables=false");
        command.add(DATABASE_NAME);
        
        System.out.println("Command: " + String.join(" ", command));
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        
        // Create backup file
        File backupFile = new File(backupFilePath);
        processBuilder.redirectOutput(backupFile);
        processBuilder.redirectErrorStream(true);
        
        System.out.println("Starting mysqldump process...");
        Process process = processBuilder.start();
        
        // Read error/output stream
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("mysqldump> " + line);
            }
        }
        
        int exitCode = process.waitFor();
        System.out.println("mysqldump exit code: " + exitCode);
        
        if (exitCode == 0) {
            if (backupFile.exists()) {
                long fileSize = backupFile.length();
                System.out.println("Backup created successfully!");
                System.out.println("File size: " + fileSize + " bytes");
                return fileSize > 0;
            } else {
                System.out.println("ERROR: Backup file was not created!");
                return false;
            }
        } else {
            System.out.println("ERROR: mysqldump failed!");
            System.out.println("Output:\n" + output.toString());
            
            // Try to delete the possibly corrupted backup file
            if (backupFile.exists()) {
                backupFile.delete();
            }
            return false;
        }
        
    } catch (Exception e) {
        System.err.println("EXCEPTION in mysqldump: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
    
    // Find mysqldump executable
    private String findMysqldumpPath() {
    System.out.println("Looking for mysqldump...");
    
    // Common paths for mysqldump
    String[] possiblePaths = {
        "mysqldump", // If it's in PATH
        "C:\\xampp\\mysql\\bin\\mysqldump.exe", // XAMPP on Windows
        "C:\\xampp\\mysql\\bin\\mysqldump", // XAMPP without .exe
        "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe",
        "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysqldump.exe",
        "C:\\Program Files\\MariaDB 10.\\bin\\mysqldump.exe",
        "/usr/bin/mysqldump", // Linux
        "/usr/local/mysql/bin/mysqldump", // macOS
        "/opt/local/lib/mysql8/bin/mysqldump",
        "/usr/local/bin/mysqldump"
    };
    
    for (String path : possiblePaths) {
        File file = new File(path);
        System.out.println("  Checking: " + path);
        
        if (file.exists()) {
            System.out.println("    ✓ Exists");
            if (file.canExecute()) {
                System.out.println("    ✓ Executable");
                return path;
            } else {
                System.out.println("    ✗ Not executable");
            }
        } else {
            System.out.println("    ✗ Not found");
        }
    }
    
    // Try to find it in PATH using command line
    System.out.println("Checking PATH...");
    try {
        String os = System.getProperty("os.name").toLowerCase();
        Process process;
        
        if (os.contains("win")) {
            process = new ProcessBuilder("where", "mysqldump").start();
        } else {
            process = new ProcessBuilder("which", "mysqldump").start();
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String path = reader.readLine();
        process.waitFor();
        
        if (path != null && !path.trim().isEmpty()) {
            System.out.println("  Found in PATH: " + path);
            return path.trim();
        }
    } catch (Exception e) {
        System.out.println("  Error checking PATH: " + e.getMessage());
    }
    
    System.out.println("mysqldump not found in any common location");
    return null;
}

    // Add this method to DatabaseBackup class
public boolean createSimpleBackup(String backupFilePath) {
    System.out.println("Creating simple backup...");
    
    try (Connection conn = DatabaseConnection.getConnection();
         BufferedWriter writer = new BufferedWriter(new FileWriter(backupFilePath))) {
        
        // Just create a basic backup with metadata
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writer.write("-- Simple BHRMS Backup");
        writer.newLine();
        writer.write("-- Created: " + sdf.format(new Date()));
        writer.newLine();
        writer.write("-- This is a metadata-only backup");
        writer.newLine();
        writer.write("-- For full backup, install MySQL and ensure mysqldump is available");
        writer.newLine();
        writer.newLine();
        
        // Get some basic info
        writer.write("-- Database Information:");
        writer.newLine();
        
        // Count patients
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM patients WHERE is_active = TRUE")) {
            if (rs.next()) {
                writer.write("-- Active Patients: " + rs.getInt(1));
                writer.newLine();
            }
        }
        
        // Count medical records
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM medical_records")) {
            if (rs.next()) {
                writer.write("-- Medical Records: " + rs.getInt(1));
                writer.newLine();
            }
        }
        
        // Count users
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE is_active = TRUE")) {
            if (rs.next()) {
                writer.write("-- Active Users: " + rs.getInt(1));
                writer.newLine();
            }
        }
        
        writer.newLine();
        writer.write("-- Note: This is a metadata-only backup.");
        writer.newLine();
        writer.write("-- To enable full backups, please ensure MySQL is properly installed");
        writer.newLine();
        writer.write("-- and mysqldump is available in your system PATH.");
        writer.newLine();
        
        writer.flush();
        System.out.println("Simple backup created: " + backupFilePath);
        return true;
        
    } catch (Exception e) {
        System.err.println("Error creating simple backup: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
    
    // Original Java backup method (slower - kept as fallback)
    private boolean createBackupWithJava(String backupFilePath) {
        Connection conn = null;
        BufferedWriter writer = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            writer = new BufferedWriter(new FileWriter(backupFilePath));
            
            // Write backup header
            writeBackupHeader(writer);
            
            // Get all tables
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                backupTable(conn, writer, tableName);
            }
            
            writeBackupFooter(writer);
            writer.flush();
            
            System.out.println("Java backup completed: " + backupFilePath);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating Java backup: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) writer.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void backupTable(Connection conn, BufferedWriter writer, String tableName) 
            throws SQLException, IOException {
        
        writer.write("\n-- Table: " + tableName);
        writer.newLine();
        writer.write("DROP TABLE IF EXISTS `" + tableName + "`;");
        writer.newLine();
        
        // Get create table statement
        String createSQL = getCreateTableSQL(conn, tableName);
        writer.write(createSQL + ";");
        writer.newLine();
        writer.newLine();
        
        // Get table data
        String selectSQL = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            int rowCount = 0;
            while (rs.next()) {
                if (rowCount == 0) {
                    writer.write("INSERT INTO `" + tableName + "` VALUES ");
                } else {
                    writer.write(",");
                    writer.newLine();
                    writer.write("  ");
                }
                
                writer.write("(");
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) writer.write(", ");
                    
                    Object value = rs.getObject(i);
                    if (value == null) {
                        writer.write("NULL");
                    } else {
                        String stringValue = value.toString();
                        stringValue = stringValue.replace("\\", "\\\\")
                                               .replace("'", "''");
                        
                        if (meta.getColumnType(i) == Types.VARCHAR ||
                            meta.getColumnType(i) == Types.CHAR ||
                            meta.getColumnType(i) == Types.LONGVARCHAR ||
                            meta.getColumnType(i) == Types.DATE ||
                            meta.getColumnType(i) == Types.TIMESTAMP) {
                            writer.write("'");
                            writer.write(stringValue);
                            writer.write("'");
                        } else {
                            writer.write(stringValue);
                        }
                    }
                }
                writer.write(")");
                rowCount++;
                
                // Batch every 100 rows for readability
                if (rowCount % 100 == 0) {
                    writer.write(";");
                    writer.newLine();
                    writer.write("INSERT INTO `" + tableName + "` VALUES ");
                }
            }
            
            if (rowCount > 0) {
                writer.write(";");
                writer.newLine();
                writer.write("-- " + rowCount + " rows inserted into " + tableName);
                writer.newLine();
            }
            
        } catch (SQLException e) {
            writer.write("-- Error backing up table " + tableName + ": " + e.getMessage());
            writer.newLine();
            throw e;
        }
    }
    
    private String getCreateTableSQL(Connection conn, String tableName) throws SQLException {
        String sql = "SHOW CREATE TABLE " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString(2);
            }
        }
        return "";
    }
    
    private void writeBackupHeader(BufferedWriter writer) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writer.write("-- BHRMS Database Backup");
        writer.newLine();
        writer.write("-- Generated: " + sdf.format(new Date()));
        writer.newLine();
        writer.write("-- Database: " + DATABASE_NAME);
        writer.newLine();
        writer.write("-- Backup Method: " + (findMysqldumpPath() != null ? "mysqldump" : "Java"));
        writer.newLine();
        writer.write("SET FOREIGN_KEY_CHECKS=0;");
        writer.newLine();
        writer.newLine();
    }
    
    private void writeBackupFooter(BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.write("SET FOREIGN_KEY_CHECKS=1;");
        writer.newLine();
        writer.newLine();
        writer.write("-- Backup completed successfully");
        writer.newLine();
    }
    
    // Inner class for backup statistics
    public static class BackupStats {
        private long fileSize;
        private Date backupDate;
        private String filePath;
        private int lineCount;
        private int insertCount;
        
        public BackupStats() {
            // Default constructor
        }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        
        public Date getBackupDate() { return backupDate; }
        public void setBackupDate(Date backupDate) { this.backupDate = backupDate; }
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public int getLineCount() { return lineCount; }
        public void setLineCount(int lineCount) { this.lineCount = lineCount; }
        
        public int getInsertCount() { return insertCount; }
        public void setInsertCount(int insertCount) { this.insertCount = insertCount; }
        
        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "Backup Statistics:\n" +
                   "  File: " + (filePath != null ? new File(filePath).getName() : "Unknown") + "\n" +
                   "  Size: " + formatSize(fileSize) + "\n" +
                   "  Date: " + (backupDate != null ? sdf.format(backupDate) : "Unknown") + "\n" +
                   "  Lines: " + lineCount + "\n" +
                   "  Inserts: " + insertCount;
        }
        
        private String formatSize(long size) {
            if (size < 1024) return size + " B";
            else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
            else return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }
    
    // Method to get backup statistics
    public BackupStats getBackupStats(String backupFilePath) {
        BackupStats stats = new BackupStats();
        File backupFile = new File(backupFilePath);
        
        if (backupFile.exists()) {
            stats.setFileSize(backupFile.length());
            stats.setBackupDate(new Date(backupFile.lastModified()));
            stats.setFilePath(backupFile.getAbsolutePath());
            
            // Count lines (approximate row count)
            try (BufferedReader reader = new BufferedReader(new FileReader(backupFile))) {
                int lineCount = 0;
                int insertCount = 0;
                String line;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    if (line.startsWith("INSERT INTO")) {
                        insertCount++;
                    }
                }
                stats.setLineCount(lineCount);
                stats.setInsertCount(insertCount);
            } catch (IOException e) {
                // Ignore errors in stats
                System.err.println("Error reading backup file for stats: " + e.getMessage());
            }
        }
        
        return stats;
    }
}