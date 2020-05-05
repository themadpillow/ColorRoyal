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
	protected boolean actionSkill() {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		gameManager.getGamePlayerList().stream()
				.filter(entityPlayer -> gamePlayer.getNowTeam().getColor() != entityPlayer.getNowTeam().getColor())
				.forEach(entityPlayer -> entityPlayer.getPlayer()
						.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 5, 1)));

		return true;
	}
}
