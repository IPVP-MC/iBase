package com.doctordark.base.cmd.old;

public class StaffchatCommand {/**extends EssentialsCommand {

 public StaffchatCommand() {
 super("staffchat");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 Player target = null;
 if (args.length < 1) {
 if (sender instanceof Player) {
 target = (Player)sender;
 } else {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName> [true|false]");
 return true;
 }
 } else {
 target = Bukkit.getPlayer(args[0]);
 }

 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
 return true;
 }

 boolean staffchat = !UserPreferences.isStaffChatting(target.getUniqueId());
 if (args.length >= 2) {
 staffchat = Boolean.parseBoolean(args[1]);
 }

 UserPreferences.setStaffChatting(target.getUniqueId(), staffchat);
 Command.broadcastCommandMessage(sender, ChatColor.LIGHT_PURPLE + "Staff chat mode of " + target.getDisplayName() + ChatColor.LIGHT_PURPLE + " set to " + staffchat + ".");
 return true;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return args.length == 1 ? null : new ArrayList<String>();
 }
 */
}
