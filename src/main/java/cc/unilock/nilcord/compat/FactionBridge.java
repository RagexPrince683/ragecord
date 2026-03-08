package cc.unilock.nilcord.compat;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.player.EntityPlayer;

public class FactionBridge {

    private static boolean xenofactionsLoaded = false;

    public static void init() {
        xenofactionsLoaded = Loader.isModLoaded("hfr");
    }

    /**
     * Returns true if the player's chat should be ignored by Nilcord
     * (faction or alliance channel).
     */
    public static boolean isFactionChat(EntityPlayer player) {

        if (!xenofactionsLoaded) return false;

        try {
            int mode = player.getEntityData().getInteger("clowderChat");

            // 0 = public
            // 1 = faction
            // 2 = alliance
            return mode == 1 || mode == 2;

        } catch (Exception e) {
            return false;
        }
    }
}
