package madpillow.colorRoyal.skills;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.PlayerUtils;
import madpillow.colorRoyal.game.GamePlayer;

public class Teleport extends Skill {
	private Location location = null;

	public Teleport(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected boolean actionSkill() {
		if (location == null) {
			location = gamePlayer.getPlayer().getLocation();
			PlayerUtils.sendMessage(gamePlayer.getPlayer(), "テレポート先を設定しました");

			new BukkitRunnable() {
				double offset = 0;

				@Override
				public void run() {
					if (location == null) {
						cancel();
						return;
					}
					PlayerUtils.createHelix(gamePlayer.getPlayer(), location, offset);
					offset += 0.2f;
					if (offset > 6) {
						offset = 0;
					}
				}
			}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 1L);
		} else {
			Location beforeLoc = gamePlayer.getPlayer().getLocation().clone();
			gamePlayer.getPlayer().teleport(location);
			gamePlayer.getPlayer().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(beforeLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			}
			location = null;
		}

		return true;
	}
}
