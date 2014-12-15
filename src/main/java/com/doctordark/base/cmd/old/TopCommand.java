package com.doctordark.base.cmd.old;

public class TopCommand {/** extends EssentialsCommand {

 public TopCommand() {
 super("top");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 if (!(sender instanceof Player)) {
 playerOnlyCommand(sender);
 return true;
 }

 Player player = (Player)sender;
 Location location = player.getLocation();
 World world = location.getWorld();
 int maxHeight = world.getMaxHeight();

 Block highestBlock = null;
 for (int y = maxHeight; y > 0; y--) {
 Block block = world.getBlockAt(location.getBlockX(), y, location.getBlockZ());
 if (block == null || block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
 continue;
 }
 highestBlock = block;
 break;
 }

 if (highestBlock == null || highestBlock.getY() + 1 == location.getBlockY()) {
 player.sendMessage(ChatColor.RED + "No highest block was found!");
 return true;
 }

 player.teleport(highestBlock.getLocation().add(0, 2, 0), TeleportCause.COMMAND);
 return true;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return Collections.emptyList();
 }*/

}