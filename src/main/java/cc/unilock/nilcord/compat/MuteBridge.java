package cc.unilock.nilcord.compat;

import cpw.mods.fml.common.Loader;

import java.lang.reflect.Method;
import java.util.UUID;

public class MuteBridge {

    private static boolean xenofactionsLoaded = false;
    private static Method isMutedMethod = null;

    public static void init() {
        try {
            xenofactionsLoaded = Loader.isModLoaded("hfr"); // FIXED

            if (!xenofactionsLoaded) return;

            Class<?> muteManagerClass =
                Class.forName("com.hfr.command.MuteManager");

            isMutedMethod =
                muteManagerClass.getMethod("isMuted", UUID.class);

            System.out.println("Xeno loaded: " + xenofactionsLoaded);
            System.out.println("Method found: " + (isMutedMethod != null));

        } catch (Exception e) {
            xenofactionsLoaded = false;
        }
    }

    public static boolean isPlayerMuted(UUID uuid) {

        if (!xenofactionsLoaded || isMutedMethod == null)
            return false;

        try {
            System.out.println("Checking mute for UUID: " + uuid);
            return (boolean) isMutedMethod.invoke(null, uuid);
        } catch (Exception e) {
            return false;
        }
    }
}
