package madpillow.colorRoyal.skills;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GameManager;
import madpillow.colorRoyal.game.GamePlayer;

public class Glowing extends Skill {

	public Glowing(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected void actionSkill() {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		gameManager.getGamePlayerList().stream()
				.filter(entityPlayer -> gamePlayer.getNowTeam().getColor() != entityPlayer.getNowTeam().getColor())
				.forEach(entityPlayer -> entityPlayer.getPlayer()
						.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 5, 1)));
	}

	@Override
	protected void setLoresInfo() {
		loresInfo.add("右クリックで使用");
		loresInfo.add("他チームのプレイヤーに5秒間発光を付与");
		loresInfo.add("CT：" + coolTime + "秒");
	}

}
