package com.doctordark.base.cmd.old;

public class GamemodeCommand {/** extends EssentialsCommand {

 public GamemodeCommand() {
 super("gamemode");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 GameMode gamemode = null;
 Player target = null;

 if (args.length < 1) {
 if (!(sender instanceof Player)) {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <gamemode> [playerName]");
 return true;
 }

 target = (Player)sender;
 if (target.getGameMode() == GameMode.CREATIVE) {
 gamemode = GameMode.SURVIVAL;
 } else if (target.getGameMode() == GameMode.ADVENTURE) {
 gamemode = GameMode.CREATIVE;
 } else if (target.getGameMode() == GameMode.SURVIVAL) {
 gamemode = GameMode.ADVENTURE;
 }
 } else {
 gamemode =null;// Utils.getGameMode(args[0]);
 }

 if (gamemode == null) {
 sender.sendMessage(ChatColor.RED + "Gamemode '" + args[0] + "' not found!");
 return true;
 }

 if (args.length < 2) {
 if (sender instanceof Player) {
 target = (Player)sender;
 } else {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <gamemode> <playerName>");
 return true;
 }
 } else {
 target = Bukkit.getPlayer(args[1]);
 }

 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found!");
 return true;
 }

 String gamemodeName = (gamemode.name());

 if (target.getGameMode().equals(gamemode)) {
 sender.sendMessage(ChatColor.RED + "Gamemode of " + target.getName() + " is already " + gamemodeName + "!");
 return true;
 }

 target.setGameMode(gamemode);
 Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Gamemode of " + target.getDisplayName() + ChatColor.YELLOW + " set to " + gamemodeName + ".");
 return true;
 }

 @Override public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
 List<String> list = new ArrayList<String>();

 if (args.length == 1) {
 list = Arrays.asList("adventure", "creative", "survival");
 } else if (args.length == 2) {
 return null;
 }
 return list;

 //return Utils.getCompletions(args, list);
 }*/
}