package com.doctordark.base.cmd.old;

public class VisibleCommand {/**extends EssentialsCommand {

 public VisibleCommand() {
 super("visible");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 Player target = null;
 if (args.length < 1) {
 if (sender instanceof Player) {
 target = (Player)sender;
 } else {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
 return true;
 }
 } else {
 target = Bukkit.getPlayer(args[0]);
 }

 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
 return true;
 }

 boolean vanished = !UserPreferences.isVanished(target.getUniqueId());
 if (args.length >= 2) {
 vanished = Boolean.parseBoolean(args[1]);
 }

 UserPreferences.setVanished(target.getUniqueId(), vanished);
 Command.broadcastCommandMessage(sender, "Visible mode of " + target.getDisplayName() + " set to " + vanished + ".");
 return false;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return args.length == 1 ? null : new ArrayList<String>();
 }*/
}
