package com.doctordark.base.cmd.old;

public class IDCommand { /**extends EssentialsCommand {

 public IDCommand() {
 super("id");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 if (!(sender instanceof Player)) {
 playerOnlyCommand(sender);
 return true;
 }

 Player player = (Player)sender;
 ItemStack stack = player.getItemInHand();

 if (stack == null || stack.getType() == Material.AIR) {
 sender.sendMessage(ChatColor.RED + "You must be holding an item!");
 return true;
 }

 @SuppressWarnings("deprecation")
 int id = stack.getType().getId();
 short data = stack.getDurability();

 String fullID = id + (data > 0 ? ":" + data : "");
 //String name = ItemDb.name(stack);
 //String aliases = ItemDb.names(stack);

 //sender.sendMessage(ChatColor.YELLOW + "You are holding " + ChatColor.WHITE + name + ChatColor.YELLOW + " with the ID " + fullID + ".");
 //sender.sendMessage(ChatColor.YELLOW + "Aliases: " + ChatColor.WHITE + aliases + ChatColor.WHITE + ".");
 return false;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return null;
 }
 */
}
