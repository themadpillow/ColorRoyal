package madpillow.colorRoyal;

import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import lombok.Getter;
import lombok.Setter;

public class GamePlayer {
	@Getter
	private Player player;
	@Getter
	private GameTeam parentTeam;
	@Getter
	@Setter
	private GameTeam nowTeam;
	@Getter
	private int attackCount = 3;

	public GamePlayer(Player player, GameTeam parentTeam) {
		this.player = player;
		this.parentTeam = parentTeam;
		this.nowTeam = parentTeam;
	}

	public void resetTeam() {
		nowTeam.getTeam().removeEntry(player.getName());
		parentTeam.getTeam().addEntry(player.getName());
		nowTeam = parentTeam;

		changeArmorColor(parentTeam);
	}

	public void changeArmorColor(GameTeam team) {
		ItemStack helmet = player.getInventory().getHelmet();
		LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
		meta.setColor(Color.fromRGB(team.getColor().getRed(), team.getColor().getGreen(),
				team.getColor().getBlue()));
		helmet.setItemMeta(meta);
	}

	public void reduceAttackCount() {
		attackCount--;
		if (attackCount == 0) {
			attackCount = 3;

			resetTeam();
		}
	}
}
