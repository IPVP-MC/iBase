package com.doctordark.base.cmd.old;

public class AmIVisCommand {/** extends EssentialsCommand {

 public AmIVisCommand() {
 super("amivis");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 if (!(sender instanceof Player)) {
 playerOnlyCommand(sender);
 return true;
 }

 Player player = (Player)sender;

 if (args.length < 1) {
 boolean visible = UserPreferences.isVanished(player.getUniqueId());
 sender.sendMessage(ChatColor.YELLOW + "You are " + (visible ? ChatColor.GREEN + "in" : ChatColor.RED + "not in") + ChatColor.YELLOW + " vanish mode.");
 return true;
 }

 Player target = Bukkit.getServer().getPlayer(args[0]);

 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
 return true;
 }

 boolean visible = target.canSee(player);
 sender.sendMessage(target.getDisplayName() + " " + (visible ? ChatColor.GREEN + "can" : ChatColor.RED + "cannot") + ChatColor.YELLOW + " see you.");
 return true;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return args.length == 1 ? null : new ArrayList<String>();
 }
 */
}