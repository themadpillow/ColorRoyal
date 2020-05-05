package madpillow.colorRoyal.skills;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GamePlayer;

public class Invisiblity extends Skill {

	public Invisiblity(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected boolean actionSkill() {
		gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 1));
		gamePlayer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
		Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
			gamePlayer.sendArmor(gamePlayer.getNowTeam());
		}, 5 * 20L);

		return true;
	}
}
