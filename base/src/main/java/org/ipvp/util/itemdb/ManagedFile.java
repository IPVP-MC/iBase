package org.ipvp.util.itemdb;

import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.base.BasePlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                try (OutputStreamWriter writer = new OutputStreamWriter(digestStream, StandardCharsets.UTF_8)) {
                    final char[] buffer = new char[BUFFER_SIZE];
                    int length;
                    while ((length = reader.read(buffer)) >= 0) {
                        writer.write(buffer, 0, length);
                    }

                    writer.write("\n");
                    writer.flush();

                    digestStream.on(false);
                    digestStream.write('#');
                    digestStream.write(new BigInteger(1, digest.digest()).toString(16).getBytes(StandardCharsets.UTF_8));
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
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(file.getPath()), StandardCharsets.UTF_8)) {
                final List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                return lines;
            }
        } catch (IOException ex) {
            BasePlugin.getPlugin().getLogger().log(Level.SEVERE, ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }
}