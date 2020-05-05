package madpillow.colorRoyal.skills;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GamePlayer;
import madpillow.colorRoyal.game.GameTeamListManager;

public class Blindness extends Skill {

	public Blindness(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected boolean actionSkill() {
		GameTeamListManager gameTeamListManager = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager();
		Player player = gamePlayer.getPlayer();

		player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10).stream()
				.filter(entity -> entity instanceof Player)
				.map(entity -> gameTeamListManager.getGamePlayerAtList((Player) entity))
				.filter(Optional::isPresent)
				.filter(entityPlayer -> gamePlayer.getNowTeam().getColor() != entityPlayer.get().getNowTeam()
						.getColor())
				.forEach(entityPlayer -> entityPlayer.get().getPlayer()
						.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 3, 1)));

		return true;
	}
}
