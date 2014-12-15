package com.doctordark.base.cmd.old;

public class WhoIsCommand {/**extends EssentialsCommand {

 public WhoIsCommand() {
 super("whois");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 if (args.length < 1) {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
 return true;
 }

 Player target = Bukkit.getServer().getPlayer(args[0]);

 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
 return true;
 }

 sender.sendMessage(ChatColor.DARK_AQUA + "[" + target.getDisplayName() + ChatColor.DARK_AQUA + "]");
 sender.sendMessage(ChatColor.AQUA + " Health: " + ChatColor.GRAY + target.getHealth() + "/" + target.getMaxHealth());
 sender.sendMessage(ChatColor.AQUA + " Hunger: " + ChatColor.GRAY + target.getFoodLevel() + "/" + 20);
 sender.sendMessage(ChatColor.AQUA + " IP Address: " + ChatColor.GRAY + target.getAddress().getHostString());
 sender.sendMessage(ChatColor.AQUA + " Game-mode: " + ChatColor.GRAY + target.getGameMode().name().toLowerCase());
 sender.sendMessage(ChatColor.AQUA + " Vanished: " + ChatColor.GRAY + toString(UserPreferences.isVanished(target.getUniqueId())));
 sender.sendMessage(ChatColor.AQUA + " Flight: " + ChatColor.GRAY + toString(target.getAllowFlight()));
 sender.sendMessage(ChatColor.AQUA + " Operator: " + ChatColor.GRAY + toString(target.isOp()));
 sender.sendMessage(ChatColor.AQUA + " Staff Chat: " + ChatColor.GRAY + toString(UserPreferences.isStaffChatting(target.getUniqueId())));
 return false;
 }

 private String toString(boolean trueFalse) {
 return BooleanUtils.toStringYesNo(trueFalse);
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return args.length == 1 ? null : new ArrayList<String>();
 }*/

}
