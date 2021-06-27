package raff.treefeller.Utility;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import raff.treefeller.TreeFeller;


public class TogglePlugin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("info")) {
                sender.sendMessage(ChatColor.GOLD + "TreeFeller" + ChatColor.WHITE + " > Plugin by Raff [" + ChatColor.GREEN + "https://raffsimms.com" + ChatColor.WHITE + "]");
            }
            return false;
        }
        if (sender instanceof Player) {
            Player _player = ((Player) sender).getPlayer();
            assert _player != null;
            if (!TreeFeller.enabledPlayers.contains(_player.getUniqueId())) {
                TreeFeller.enabledPlayers.add(_player.getUniqueId());
                _player.sendMessage(ChatColor.GOLD + "TreeFeller" + ChatColor.WHITE + " > Has been " + ChatColor.GREEN + "ENABLED");
            } else {
                TreeFeller.enabledPlayers.remove(_player.getUniqueId());
                _player.sendMessage(ChatColor.GOLD + "TreeFeller" + ChatColor.WHITE + " > Has been" + ChatColor.RED + "DISABLED");
            }
        }
        return false;
    }
}
