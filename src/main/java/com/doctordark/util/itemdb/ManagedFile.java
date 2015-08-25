package com.doctordark.util.itemdb;

import com.doctordark.base.BasePlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class ManagedFile {

    private static final int BUFFER_SIZE = 1024 * 8;
    private final transient File file;

    public ManagedFile(final String filename, final JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), filename);
        if (!file.exists()) {
            try {
                copyResourceAscii('/' + filename, file);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "items.csv has not been loaded", ex);
            }
        }
    }

    public File getFile() {
        return file;
    }

    public static void copyResourceAscii(final String resourceName, final File file) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(ManagedFile.class.getResourceAsStream(resourceName), StandardCharsets.UTF_8)) {
            final MessageDigest digest = getDigest();
            try (DigestOutputStream digestStream = new DigestOutputStream(new FileOutputStream(file), digest)) {
                try (OutputStreamWriter writer = new OutputStreamWriter(digestStream)) {
                    final char[] buffer = new char[BUFFER_SIZE];
                    do {
                        final int length = reader.read(buffer);
                        if (length >= 0) {
                            writer.write(buffer, 0, length);
                        } else {
                            break;
                        }
                    }

                    while (true);
                    writer.write("\n");
                    writer.flush();
                    final BigInteger hashInt = new BigInteger(1, digest.digest());
                    digestStream.on(false);
                    digestStream.write('#');
                    digestStream.write(hashInt.toString(16).getBytes());
                }
            }
        }
    }

    public static MessageDigest getDigest() throws IOException {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new IOException(ex);
        }
    }

    public List<String> getLines() {
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                final List<String> lines = new ArrayList<>();
                do {
                    final String line = reader.readLine();
                    if (line == null) {
                        break;
                    } else {
                        lines.add(line);
                    }
                } while (true);
                return lines;
            }
        } catch (IOException ex) {
            BasePlugin.getPlugin().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
}