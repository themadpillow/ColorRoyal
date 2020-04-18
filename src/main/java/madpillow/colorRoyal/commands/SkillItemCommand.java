package madpillow.colorRoyal.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GameManager;
import madpillow.colorRoyal.skills.SkillSelectInventory;

public class SkillItemCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		if (gameManager.isGameing()) {
			return false;
		}

		if (args.length == 0) {
			if (sender instanceof Player) {
				SkillSelectInventory.sendSelectInventoryItem((Player) sender);
			}
		} else {
			List<Entity> commandEntities = Bukkit.selectEntities(sender, args[0]);
			for (Entity entity : commandEntities) {
				if (entity instanceof Player) {
					SkillSelectInventory.sendSelectInventoryItem((Player) entity);
				}
			}
		}
		return true;
	}
}
