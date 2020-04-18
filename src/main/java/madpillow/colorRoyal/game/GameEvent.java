package madpillow.colorRoyal.game;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.PlayerUtils;
import madpillow.colorRoyal.configUtils.GameMessageText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.skills.Skill;
import madpillow.colorRoyal.skills.Skills;

public class GameEvent implements Listener {
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		if (!gameManager.isGameing()) {
			return;
		}

		if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
			return;
		}

		GameTeamListManager gameTeamListManager = gameManager.getGameTeamListManager();
		Optional<GamePlayer> gameEntity = gameTeamListManager.getGamePlayerAtList((Player) e.getEntity());
		Optional<GamePlayer> gameDamager = gameTeamListManager.getGamePlayerAtList((Player) e.getDamager());

		if (!gameEntity.isPresent() || !gameDamager.isPresent()) {
			return;
		}

		if (!gameDamager.get().isCanAttack()) {
			PlayerUtils.sendMessage(gameDamager.get().getPlayer(),
					TextConfig.getGameMessageText(GameMessageText.AttackCT));
			e.setCancelled(true);
			return;
		}

		if (!gameEntity.get().isCanDamage()) {
			PlayerUtils.sendMessage(gameDamager.get().getPlayer(),
					TextConfig.getGameMessageText(GameMessageText.DamageCTTarget));
			e.setCancelled(true);
			return;
		}
		if (!gameDamager.get().isCanDamage()) {
			PlayerUtils.sendMessage(gameDamager.get().getPlayer(),
					TextConfig.getGameMessageText(GameMessageText.DamageCTDamager));
			e.setCancelled(true);
			return;
		}

		if (gameDamager.get().getNowTeam().getColor() == gameEntity.get().getNowTeam().getColor()) {
			e.setCancelled(true);
			return;
		}

		e.setDamage(0);
		gameEntity.get().damage(gameDamager.get());
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		if (e.getWhoClicked().isOp()) {
			return;
		}

		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		if (gameManager.isGameing()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR
				|| e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getItem() == null || e.getItem().getType() == Material.AIR) {
				return;
			}

			GameTeamListManager gameteamListManager = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager();
			Optional<GamePlayer> gamePlayer = gameteamListManager.getGamePlayerAtList(e.getPlayer());
			if (!gamePlayer.isPresent()) {
				return;
			}

			boolean isSkill = false;
			for (Skills skills : Skills.values()) {
				if (skills.getMaterial() == e.getItem().getType()) {
					isSkill = true;
					break;
				}
			}
			if (isSkill) {
				for (Skill skill : gamePlayer.get().getSkillList()) {
					if (skill.getEnumSkill().getMaterial() == e.getItem().getType()) {
						skill.perform();
						return;
					}
				}

				//DEBUG
				PlayerUtils.sendMessage(e.getPlayer(), "DEBUG-ERROR: このスキルを保有していません");
			}
		}
	}
}
