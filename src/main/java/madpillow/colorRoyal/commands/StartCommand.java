package madpillow.colorRoyal.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GameManager;
import madpillow.colorRoyal.game.GameMap;

public class StartCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		if (args.length == 0) {
			return false;
		}
		Configuration configuration = ColorRoyal.getPlugin().getConfig();
		if (configuration.contains(args[0]) == false) {
			sender.sendMessage("指定されたMapが存在しません");
			return true;
		}

		ConfigurationSection section = configuration.getConfigurationSection(args[0]);
		Location location = section.getLocation("Location");
		int teleportDistance = section.getInt("TeleportDistance");
		GameMap gameMap = new GameMap(location, teleportDistance);
		gameManager.start(gameMap);

		return true;
	}
}
