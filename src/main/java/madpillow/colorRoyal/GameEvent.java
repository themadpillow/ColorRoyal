package madpillow.colorRoyal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GameEvent implements Listener {
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (!ColorRoyal.getPlugin().isGameing()) {
			return;
		}

		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
			return;
		}

		GamePlayer gameEntity = ColorRoyal.getPlugin().convertToGamePlayer((Player) e.getEntity());
		GamePlayer gameDamager = ColorRoyal.getPlugin().convertToGamePlayer((Player) e.getDamager());

		if (gameEntity == null || gameDamager == null) {
			return;
		}

		e.setDamage(0);
		gameEntity.damage(gameDamager);
	}
}
