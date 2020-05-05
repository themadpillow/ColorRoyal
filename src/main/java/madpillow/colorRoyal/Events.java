package madpillow.colorRoyal;

import java.util.Optional;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import madpillow.colorRoyal.configUtils.SkillText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GameManager;
import madpillow.colorRoyal.game.GamePlayer;
import madpillow.colorRoyal.game.GameTeamListManager;
import madpillow.colorRoyal.scoreboard.ScoreBoardUtils;
import madpillow.colorRoyal.skills.Skill;
import madpillow.colorRoyal.skills.SkillSelectInventory;
import madpillow.colorRoyal.skills.SkillType;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getItem() != null
					&& e.getItem().getType() == Material.NETHER_STAR) {
				GameTeamListManager gameteamListManager = ColorRoyal.getPlugin().getGameManager()
						.getGameTeamListManager();
				GamePlayer gamePlayer = gameteamListManager.getGamePlayerAtList(e.getPlayer())
						.orElseGet(() -> gameteamListManager.joinGame(e.getPlayer()));

				SkillSelectInventory skillSelectInventory = new SkillSelectInventory(gamePlayer);
				skillSelectInventory.openInventory();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (ColorRoyal.getPlugin().getGameManager().isGameing()) {
			e.setDamage(0.1);
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player
				&& e.getDamager() instanceof Player) {
			if (!ColorRoyal.getPlugin().getGameManager().isGameing()) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		GameTeamListManager gameteamListManager = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager();
		Optional<GamePlayer> gamePlayer = gameteamListManager.getGamePlayerAtList((Player) e.getWhoClicked());
		if (!gamePlayer.isPresent()) {
			return;
		}

		if (e.getView().getTitle()
				.startsWith(TextConfig.getSkillText(SkillText.SelectInventoryTitle).substring(0, 5))) {
			e.setCancelled(true);

			if (e.getCurrentItem().getItemMeta().hasEnchants()) {
				for (Skill skill : gamePlayer.get().getSkillList()) {
					if (skill.getEnumSkill().getMaterial() == e.getCurrentItem().getType()) {
						gamePlayer.get().getSkillList().remove(skill);
						gamePlayer.get().getPlayer().setPlayerListName(gamePlayer.get().getPlayer().getName());
						break;
					}
				}
			} else {
				if (gamePlayer.get().getSkillList().size() == 2) {
					PlayerUtils.sendMessage(gamePlayer.get().getPlayer(), "3つ以上のSKILLは選択できません。どちらかを解除して選択してください。");
					return;
				} else {
					for (SkillType skills : SkillType.values()) {
						if (skills.getMaterial() == e.getCurrentItem().getType()) {
							gamePlayer.get().getSkillList().add(skills.getSkill(gamePlayer.get()));

							if (gamePlayer.get().getSkillList().size() == 2) {
								gamePlayer.get().getPlayer()
										.setPlayerListName(ChatColor.GREEN + "✔" + ChatColor.RESET
												+ gamePlayer.get().getPlayer().getName());
							} else {
								gamePlayer.get().getPlayer().setPlayerListName(gamePlayer.get().getPlayer().getName());
							}

							break;
						}
					}
				}
			}

			e.getWhoClicked().closeInventory();
			new SkillSelectInventory(gamePlayer.get()).openInventory();
			gamePlayer.get().getPlayer().playSound(gamePlayer.get().getPlayer().getLocation(),
					Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
					1, 2);
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		GameTeamListManager gameteamListManager = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager();
		if (!gameManager.isGameing()) {
			if (!e.getPlayer().getInventory().contains(Material.NETHER_STAR)) {
				e.getPlayer().getInventory().addItem(SkillSelectInventory.getItemStack());
			}
			Optional<GamePlayer> gamePlayer = null;
			if (!(gamePlayer = gameteamListManager.getHardGamePlayerAtList(e.getPlayer())).isPresent()) {
				gameteamListManager.joinGame(e.getPlayer());
			} else {
				gamePlayer.get().setPlayer(e.getPlayer());
			}
		} else {
			Optional<GamePlayer> gamePlayer = null;
			if ((gamePlayer = gameteamListManager.getHardGamePlayerAtList(e.getPlayer())).isPresent()) {
				gamePlayer.get().setPlayer(e.getPlayer());
				e.getPlayer().setScoreboard(gamePlayer.get().getParentTeam().getTeam().getScoreboard());
				gamePlayer.get().getParentTeam().getTeam().addEntry(e.getPlayer().getName());
				ScoreBoardUtils.createSideBar(gamePlayer.get());
			} else {
				e.getPlayer().setGameMode(GameMode.SPECTATOR);
				PlayerUtils.sendMessage(e.getPlayer(), "試合中のため観戦モードになりました");
			}
		}
	}

	@EventHandler
	public void onItemDropEvent(PlayerDropItemEvent e) {
		if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
		}
	}
}
