package madpillow.colorRoyal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scoreboard.Scoreboard;

public class GameEvent implements Listener {
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
			return;
		}

		if (board.getEntryTeam(e.getEntity().getName()) == board
				.getEntryTeam(e.getDamager().getName())) {
			e.setCancelled(true);
			return;
		}


	}
}
