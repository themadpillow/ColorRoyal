package madpillow.colorRoyal.skills;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.game.GamePlayer;

public class Invisiblity extends Skill {

	public Invisiblity(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected void actionSkill() {
		gamePlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 1));

		gamePlayer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
		new BukkitRunnable() {
			@Override
			public void run() {
				gamePlayer.sendArmor(gamePlayer.getNowTeam());
			}
		}.runTaskLater(ColorRoyal.getPlugin(), 20 * 5);
	}

	@Override
	protected void setLoresInfo() {
		loresInfo.add("右クリックで使用");
		loresInfo.add("5秒間透明化");
		loresInfo.add("CT：" + coolTime + "秒");
	}
}
