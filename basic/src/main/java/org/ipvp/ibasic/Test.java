package org.ipvp.ibasic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Test {

    private static final String HASH_17 = "03f31164d234f10a3230611656332f1756e570a9";

    public static void main(String[] args) {
        String url = "http://resources.download.minecraft.net/" + HASH_17.substring(0, 2) + "/" + HASH_17;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            String row;
            while ((row = reader.readLine()) != null) {
                System.out.println(row);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
