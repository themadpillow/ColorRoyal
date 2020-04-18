package madpillow.colorRoyal.skills;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.configUtils.SkillText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GamePlayer;
import madpillow.colorRoyal.game.GameTeamListManager;

public class SkillSelectInventory {
	GamePlayer gamePlayer;
	Inventory inventory;

	public SkillSelectInventory(GamePlayer gamePlayer) {
		this.gamePlayer = gamePlayer;
	}

	public void openInventory() {
		inventory = Bukkit.createInventory(null, 9, TextConfig.getSkillText(SkillText.SelectInventoryTitle,
				(String[]) gamePlayer.getSkillList().stream().map(skill -> skill.getName()).toArray()));
		for (Skills skills : Skills.values()) {
			ItemStack itemStack = skills.getSkill(gamePlayer).getItemStack();
			for (Skill skill : gamePlayer.getSkillList()) {
				if (skill.getItemStack().getType() == itemStack.getType()) {
					ItemMeta meta = itemStack.getItemMeta();
					meta.addEnchant(Enchantment.DIG_SPEED, 1, true);
					itemStack.setItemMeta(meta);
				}
			}

			inventory.addItem(itemStack);
		}

		gamePlayer.getPlayer().openInventory(inventory);
	}

	public static ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(TextConfig.getSkillText(SkillText.SelectItemName));
		meta.setLore(Arrays.asList("ゲーム開始前にスキルを2つ選んでください！"));
		itemStack.setItemMeta(meta);

		return itemStack;
	}

	public static void sendSelectInventoryItem(Player player) {
		GameTeamListManager gameTeamListManager = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager();
		if (gameTeamListManager.getGamePlayerAtList(player) == null) {
			gameTeamListManager.joinGame(player);
		}
		player.getInventory().addItem(SkillSelectInventory.getItemStack());
	}

	public static void checkHave2Skill(GamePlayer gamePlayer) {
		if (gamePlayer.getSkillList().size() == 0) {
			gamePlayer.getSkillList().add(Skills.Speed.getSkill(gamePlayer));
			gamePlayer.getSkillList().add(Skills.Invisiblity.getSkill(gamePlayer));
		} else if (gamePlayer.getSkillList().size() == 1) {
			if (gamePlayer.getSkillList().get(0).getEnumSkill() == Skills.Speed) {
				gamePlayer.getSkillList().add(Skills.Invisiblity.getSkill(gamePlayer));
			} else {
				gamePlayer.getSkillList().add(Skills.Speed.getSkill(gamePlayer));
			}
		}
	}

	public static void sendSkillItem(GamePlayer gamePlayer) {
		checkHave2Skill(gamePlayer);
		for (Skill skill : gamePlayer.getSkillList()) {
			gamePlayer.getPlayer().getInventory().addItem(skill.getItemStack());
		}
	}
}
