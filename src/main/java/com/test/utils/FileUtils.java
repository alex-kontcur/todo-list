package com.test.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * FileUtils - Utility class for File operations
 *
 * @author Kontcur Alex (bona)
 * @since 12.07.11
 */
@SuppressWarnings("CallToRuntimeExecWithNonConstantString")
public class FileUtils {

    private static final int BUFF = 256;
    private static final long DEAULT_WAIT_SECONDS = 3L;
    private static final String CMD_EXE = "cmd.exe";
    private static final String BASH = "bash";

    @SuppressWarnings ({"OverlyComplexAnonymousInnerClass", "ClassExplicitlyExtendsThread"})
    public static File createTempFile(String prefix, String fileName, String content) {
        final File tempFile;
        try {
            File directory = new File(System.getProperty("user.home"));
            String[] nameParts = fileName.split("\\.");
            String ext = nameParts[nameParts.length - 1];
            tempFile = File.createTempFile(prefix, "." + ext, directory);
        } catch (IOException ignored) {
            return null;
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            private void deleteFileOrDir(File file) {
                if (file.isDirectory()) {
                    for (File child : file.listFiles()) {
                        deleteFileOrDir(child);
                    }
                }
                file.delete();
            }

            @Override
            public void run() {
                deleteFileOrDir(tempFile);
            }
        });
        writeFile(tempFile, content.getBytes());
        return tempFile;
    }

    public static File writeFile(File file, byte[] bytes) {
        try {
            try (FileOutputStream fo = new FileOutputStream(file)) {
                fo.write(bytes);
            }
        } catch (IOException ignored) {
        }
        return file;
    }

    private static class BuffHolder {
        private final byte[] buff;
        private final int n;

        private BuffHolder(byte[] buff, int n) {
            this.buff = buff;
            this.n = n;
        }
    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nix") || os.contains("nux");
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    public static void runFileCmd(String command) {
        byte[] bytes = command.getBytes();
        byte[] enterBytes = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, enterBytes, 0, bytes.length);
        enterBytes[enterBytes.length - 1] = 10;

        ByteArrayInputStream is = new ByteArrayInputStream(enterBytes);
        try {
            List<BuffHolder> list = new ArrayList<>();
            while (is.available() > 0) {
                byte[] buf = new byte[BUFF];
                int n = is.read(buf, 0, buf.length);
                list.add(new BuffHolder(buf, n));
            }
            Process proc = Runtime.getRuntime().exec(isWindows() ? CMD_EXE : BASH);
            OutputStream os = proc.getOutputStream();
            for (BuffHolder h : list) {
                os.write(h.buff, 0, h.n);
                os.flush();
            }
            TimeUnit.SECONDS.sleep(DEAULT_WAIT_SECONDS);
            os.close();
            proc.destroy();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private FileUtils() {
    }

}
