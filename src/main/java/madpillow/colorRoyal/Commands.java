package madpillow.colorRoyal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("start")) {
			ColorRoyal.getPlugin().start();
		} else if(command.getName().equalsIgnoreCase("stop")) {
			ColorRoyal.getPlugin().stop();
		}

		return true;
	}
}
