package madpillow.colorRoyal.skills;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GamePlayer;

public class ChargeJump extends Skill {
	private int level = 0;
	private float add = SkillType.ChargeJump.getCharge(0.01F);

	public ChargeJump(GamePlayer gamePlayer) {
		super(gamePlayer);

		new BukkitRunnable() {
			public void run() {
				if (!gamePlayer.hasSkill(SkillType.ChargeJump)) {
					cancel();
					return;
				} else if (ColorRoyal.getPlugin().getGameManager().isGameing()) {
					runnable();
					cancel();
					return;
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 5L);
	}

	@Override
	protected boolean actionSkill() {
		if (level == 0) {
			return false;
		}

		gamePlayer.getPlayer().setVelocity(
				gamePlayer.getPlayer().getEyeLocation().getDirection().multiply(level).setY(1.0 + (0.4 * (level - 1))));
		gamePlayer.getPlayer().setExp(0F);
		level = 0;

		return true;
	}

	private void runnable() {
		new BukkitRunnable() {
			public void run() {
				if (!ColorRoyal.getPlugin().getGameManager().isGameing()) {
					cancel();
					return;
				}

				Player player = gamePlayer.getPlayer();
				if (!player.isOnline()) {
					return;
				}

				if (player.getItemInHand().getType() == SkillType.ChargeJump.getMaterial()) {
					float exp = player.getExp();
					if (player.isSneaking()) {
						exp += add;
						if (exp > 1) {
							exp = 1F;
						}

					}
					/*else {
						exp = player.getExp() - add;
						if (exp < 0) {
							exp = 0F;
						}

					}
					*/
					player.setExp(exp);

					if (exp > 0.99F && level != 3) {
						level = 3;
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
						Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
						}, 2L);
						Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
						}, 4L);
					} else if (exp > 0.66F && level < 2) {
						level = 2;
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
						Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
						}, 2L);
					} else if (exp > 0.33F && level < 1) {
						level = 1;
						player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
					} else if (exp <= 0.33F) {
						level = 0;
					}
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 5L);
	}
}
