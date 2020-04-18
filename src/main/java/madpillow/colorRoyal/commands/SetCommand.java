package madpillow.colorRoyal.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import madpillow.colorRoyal.ColorRoyal;
import net.md_5.bungee.api.ChatColor;

public class SetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 2) {
			return false;
		} else if (!(sender instanceof Player)) {
			return false;
		}

		Configuration configuration = ColorRoyal.getPlugin().getConfig();
		ConfigurationSection section = configuration.getConfigurationSection(args[0]);
		if (section == null) {
			section = configuration.createSection(args[0]);
		}

		section.set("Location", ((Player) sender).getLocation());
		section.set("TeleportDistance", Integer.parseInt(args[1]));
		ColorRoyal.getPlugin().saveConfig();

		sender.sendMessage(ChatColor.GREEN + args[0] + ChatColor.YELLOW + "を登録しました(" + "テレポート範囲: " + args[1] + ")");

		return true;
	}

}
