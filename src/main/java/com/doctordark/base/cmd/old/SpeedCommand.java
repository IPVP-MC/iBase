package com.doctordark.base.cmd.old;

public class SpeedCommand {/**extends EssentialsCommand {

 public SpeedCommand() {
 super("speed");
 }

 @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
 if (args.length < 1) {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <speed> [playerName] [fly|walk]");
 return true;
 }

 Player target = null;
 if (args.length < 2) {
 if (sender instanceof Player) {
 target = (Player)sender;
 } else {
 sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
 return true;
 }
 } else {
 target = Bukkit.getPlayer(args[1]);
 }

 if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
 sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found!");
 return true;
 }

 if (!Utils.isInteger(args[0])) {
 sender.sendMessage(ChatColor.RED + "Speed must be an integer!");
 return true;
 }

 Float speed = (float) Integer.parseInt(args[0]);

 if (speed <= 0 || speed > 10) {
 sender.sendMessage(ChatColor.RED + "Speed must be between 1 and 10!");
 return true;
 }

 // Adjust the speed to prevent an IllegalArgumentException.
 speed = speed / 10F;

 String speedString = speed.toString();

 boolean fly = target.isFlying();
 if (args.length >= 3) {
 if (args[2].equals("fly")) {
 fly = true;
 } else if (args[2].equals("walk")) {
 fly = false;
 }
 }

 if (fly) {
 if (target.getFlySpeed() == speed) {
 sender.sendMessage(ChatColor.RED + "Fly speed of " + target.getName() + " is already " + (speedString) + "!");
 return true;
 } else {
 target.setFlySpeed(speed);
 }
 } else {
 if (target.getWalkSpeed() == speed) {
 sender.sendMessage(ChatColor.RED + "Walk speed of " + target.getName() + " is already " + (speedString) + "!");
 return true;
 } else {
 target.setWalkSpeed(speed);
 }
 }

 Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set " + (fly ? "fly" : "walk") + " speed of " + target.getDisplayName() + ChatColor.YELLOW + " to " + speedString + ".");
 return false;
 }*/

}
