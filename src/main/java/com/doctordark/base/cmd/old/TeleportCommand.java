package com.doctordark.base.cmd.old;

public class TeleportCommand {/**extends EssentialsCommand {

 public TeleportCommand() {
 super("teleport");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 /**if (args.length < 1 || args.length > 4) {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> [target|[x][y][z]]");
 return true;
 }

 Player player = null;
 if (args.length == 1 || args.length == 3) {
 if (sender instanceof Player) {
 player = (Player) sender;
 } else {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
 return true;
 }
 } else {
 player = Bukkit.getPlayerExact(args[0]);
 }

 if (player == null || (sender instanceof Player && !((Player)sender).canSee(player))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
 return true;
 }

 if (args.length < 3) {
 Player target = Bukkit.getPlayerExact(args[args.length - 1]);
 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[args.length - 1] + ChatColor.GOLD + "' not found!");
 return true;
 }
 player.teleport(target, TeleportCause.COMMAND);
 Command.broadcastCommandMessage(sender, "Teleported " + player.getDisplayName() + " to " + target.getDisplayName() + ".");
 return true;
 } else if (player.getWorld() != null) {
 Location playerLocation = player.getLocation();
 double x = getCoordinate(sender, playerLocation.getX(), args[args.length - 3]);
 double y = getCoordinate(sender, playerLocation.getY(), args[args.length - 2], 0, 0);
 double z = getCoordinate(sender, playerLocation.getZ(), args[args.length - 1]);

 if (x == MIN_COORD_MINUS_ONE || y == MIN_COORD_MINUS_ONE || z == MIN_COORD_MINUS_ONE) {
 sender.sendMessage("Please provide a valid location!");
 return true;
 }

 playerLocation.setX(x);
 playerLocation.setY(y);
 playerLocation.setZ(z);

 player.teleport(playerLocation, TeleportCause.COMMAND);
 Command.broadcastCommandMessage(sender, String.format("Teleported %s to %.2f, %.2f, %.2f", player.getDisplayName(), x, y, z));
 return true;
 }

 return false;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return args.length == 1 ? null : new ArrayList<String>();
 }
 */
}
