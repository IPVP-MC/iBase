package me.poisonex.plugins.ibasic.listeners;

import me.poisonex.plugins.ibasic.IBasic;
import me.poisonex.plugins.ibasic.commands.CmdText;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class SpecialListener implements Listener {
    private ArrayList<CmdText> cmdTexts = new ArrayList<>();

    public SpecialListener(IBasic plugin) {
        cmdTexts.add(new CmdText(plugin, "help"));
        cmdTexts.add(new CmdText(plugin, "tutorial"));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        for (CmdText cmdText : this.cmdTexts) {
            if (e.getMessage().startsWith("/" + cmdText.getName())) {
                cmdText.onCommand(p);

                e.setCancelled(true);
            }
        }
    }
}
