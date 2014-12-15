package com.doctordark.base.cmd.old;

public class ItemCommand {/** extends EssentialsCommand {

 public ItemCommand() {
 super("item");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 if (!(sender instanceof Player)) {
 playerOnlyCommand(sender);
 return true;
 }

 Player player = (Player)sender;

 if (args.length < 1) {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <name> <quantity>");
 return true;
 }

 if (args.length >= 2 && !Utils.isInteger(args[1])) {
 sender.sendMessage(ChatColor.RED + "NumberFormatException: Quantity must be an integer!");
 return true;
 }

 ItemStack stack = ItemDb.get(args[0]);

 if (stack == null) {
 sender.sendMessage(ChatColor.RED + "Item '" + args[0] + "' not found!");
 return true;
 }

 String name = ItemDb.name(stack);
 Integer amount = args.length >= 2 ? Integer.parseInt(args[1]) : stack.getMaxStackSize();
 stack.setAmount(amount);

 // Add the items to the inventory, dropping any excess.
 Map<Integer, ItemStack> excess = player.getInventory().addItem(stack);
 for (ItemStack entry : excess.values()) {
 player.getWorld().dropItemNaturally(player.getLocation(), entry);
 }

 Command.broadcastCommandMessage(sender, "Spawned " + amount + " of " + name + ".");
 return false;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 return args.length == 1 ? null : new ArrayList<String>();
 }
 */
}
