package madpillow.colorRoyal.skills;

import org.bukkit.Location;
import org.bukkit.Sound;
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
	protected void actionSkill() {
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
			gamePlayer.getPlayer().teleport(location);
			gamePlayer.getPlayer().playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
			location = null;
		}
	}

	@Override
	protected void setLoresInfo() {
		loresInfo.add("右クリックで使用");
		loresInfo.add("一度目の使用でテレポート先を設定");
		loresInfo.add("二度目の使用で設定先にテレポート");
		loresInfo.add("CT：" + coolTime + "秒");
	}
}
