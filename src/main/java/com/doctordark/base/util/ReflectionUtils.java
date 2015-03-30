package com.doctordark.base.util;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReflectionUtils {
    private static final String PACKAGE_PREFIX = "org/bukkit/craftbukkit/v";

    public static String getVersion() {
        return version;
    }

    public static void putInPrivateStaticMap(Class clazz, String fieldName, Object key, Object value)
            throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        Map map = (Map) field.get(null);
        map.put(key, value);

        field.set(null, map);
    }

    public static String getCraftPlayerClasspath() {
        return "org.bukkit.craftbukkit." + getVersion() + ".entity.CraftPlayer";
    }

    public static String getPlayerConnectionClasspath() {
        return "net.minecraft.server." + getVersion() + ".PlayerConnection";
    }

    public static String getNMSPlayerClasspath() {
        return "net.minecraft.server." + getVersion() + ".EntityPlayer";
    }

    public static String getPacketClasspath() {
        return "net.minecraft.server." + getVersion() + ".Packet";
    }

    public static String getPacketTeamClasspath() {
        return "net.minecraft.server." + getVersion() + ".PacketPlayOutScoreboardTeam";
    }

    public static String getPlayerListClasspath() {
        return "net.minecraft.server." + getVersion() + ".PlayerList";
    }

    private static String version = "";

    static {
        try {
            File file = new File(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().replace("\\", "/");
                if (name.startsWith("org/bukkit/craftbukkit/v")) {
                    String ver = "";
                    for (int t = "org/bukkit/craftbukkit/v".length(); t < name.length(); t++) {
                        char c = name.charAt(t);
                        if (c == '/') {
                            break;
                        }
                        ver = ver + c;
                    }
                    version = "v" + ver;
                    break;
                }
            }
            zis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
