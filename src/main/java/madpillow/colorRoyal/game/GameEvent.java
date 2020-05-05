package madpillow.colorRoyal.game;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.PlayerUtils;
import madpillow.colorRoyal.configUtils.GameMessageText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.skills.Decoy;
import madpillow.colorRoyal.skills.Skill;
import madpillow.colorRoyal.skills.SkillType;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;

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
			for (SkillType skills : SkillType.values()) {
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

	@EventHandler
	public void onDamageNPC(NPCDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}

		GameTeamListManager gameteamListManager = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager();
		Optional<GamePlayer> damager = gameteamListManager.getGamePlayerAtList((Player) e.getDamager());
		if (!damager.isPresent()) {
			return;
		}

		String name = e.getNPC().getName();
		if (damager.get().getPlayer().getName().equalsIgnoreCase(name)) {
			return;
		}

		damager.get().getPlayer().playSound(
				damager.get().getPlayer().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2F);
		e.getNPC().despawn();

		Player player = Bukkit.getPlayer(name);
		if (player == null) {
			return;
		}

		Optional<GamePlayer> gamePlayer = ColorRoyal.getPlugin().getGameManager().getGameTeamListManager()
				.getGamePlayerAtList(player);
		PlayerUtils.sendMessage(player, TextConfig.getGameMessageText(GameMessageText.DestroyDecoy));
		player.getInventory().addItem(new ItemStack(Material.COMPASS));
		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.3F, 1F);

		Decoy decoy = null;
		for (Skill skill : gamePlayer.get().getSkillList()) {
			if (skill instanceof Decoy) {
				decoy = (Decoy) skill;
				break;
			}
		}
		final Decoy decoySkill = decoy;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!ColorRoyal.getPlugin().getGameManager().isGameing()) {
					cancel();
					return;
				}
				if (decoySkill.isNull()) {
					cancel();
					return;
				}
				player.setCompassTarget(damager.get().getPlayer().getLocation());
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 0L, 20L);
		new BukkitRunnable() {
			@Override
			public void run() {
				player.getInventory().remove(Material.COMPASS);
				decoySkill.destroy();
			}
		}.runTaskLater(ColorRoyal.getPlugin(), SkillType.Decoy.getCharge(20) * 20L);
	}
}
