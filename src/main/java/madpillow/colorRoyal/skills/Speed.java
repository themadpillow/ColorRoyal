package madpillow.colorRoyal.skills;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import madpillow.colorRoyal.game.GamePlayer;

public class Speed extends Skill {
	public Speed(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected boolean actionSkill() {
		gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1));

		return true;
	}
}
