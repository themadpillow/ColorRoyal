package madpillow.colorRoyal.skills;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import madpillow.colorRoyal.game.GamePlayer;

public class Speed extends Skill {
	public Speed(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected void actionSkill() {
		gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1));
	}

	@Override
	protected void setLoresInfo() {
		loresInfo.add("右クリックで使用");
		loresInfo.add("5秒間移動速度上昇");
		loresInfo.add("CT：" + coolTime + "秒");
	}
}
