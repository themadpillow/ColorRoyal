package madpillow.colorRoyal.skills;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import madpillow.colorRoyal.game.GamePlayer;

public class HighJump extends Skill {

	public HighJump(GamePlayer gamePlayer) {
		super(gamePlayer);

		task();
	}

	@Override
	protected boolean actionSkill() {
		gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 5));

		return true;
	}

	private void task() {
		new BukkitRunnable() {
			@Override
			public void run() {
				// TODO 自動生成されたメソッド・スタブ

			}
		};
	}
}
