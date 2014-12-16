package com.doctordark.base.cmd.module.essential;

import com.doctordark.base.cmd.BaseCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Command used to check the server lag.
 */
public class LagCommand extends BaseCommand {

    private static final double MAXIMUM_TPS = 20.0D;

    public LagCommand() {
        super("lag", "Checks the lag of the server.", "base.command.lag");
        this.setAliases(new String[]{});
        this.setUsage("/(command)");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        double tps = 20; //Math.min(((CraftServer) Bukkit.getServer()).getServer().recentTps[0], 20);
        double lag = (double) Math.round((1.0D - tps / MAXIMUM_TPS) * 100.0D);

        ChatColor color;
        if (tps >= 18.0) {
            color = ChatColor.GREEN;
        } else if (tps >= 15.0) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.RED;
        }

        sender.sendMessage(color + "Server TPS is currently at " + format(tps) + ".");
        sender.sendMessage(color + "Server lag is currently at " + format(lag) + "%");
        return true;
    }

    /**
     * Formats a double into a nice string.
     *
     * @param value the double to format
     * @return the formatted string
     */
    private String format(Double value) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(0);
        decimalFormat.setGroupingUsed(false);
        BigDecimal bigDecimal = new BigDecimal(value.toString());
        return decimalFormat.format(bigDecimal);
    }
}
