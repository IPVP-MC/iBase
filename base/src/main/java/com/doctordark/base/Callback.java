package com.doctordark.base;

/**
 * Taken from open-source BungeeCord:
 * https://github.com/SpigotMC/BungeeCord
 * https://github.com/SpigotMC/BungeeCord/blob/master/api/src/main/java/net/md_5/bungee/api/Callback.java
 * <p>
 * Represents a method which may be called once a result has been computed
 * asynchronously.
 *
 * @param <V> the type of result
 */
public interface Callback<V> {

    /**
     * Called when the result is done.
     *
     * @param result the result of the computation
     * @param error  the error(s) that occurred, if any
     */
    void done(V result, Throwable error);
}