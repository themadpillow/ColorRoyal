package madpillow.colorRoyal.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import lombok.Getter;
import lombok.Setter;
import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.PlayerUtils;
import madpillow.colorRoyal.configUtils.GameMessageText;
import madpillow.colorRoyal.configUtils.SideBarText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.skills.Skill;
import madpillow.colorRoyal.skills.SkillType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class GamePlayer {
	@Getter
	@Setter
	private Player player;
	@Getter
	private GameTeam parentTeam;
	@Getter
	@Setter
	private GameTeam nowTeam;
	@Getter
	private boolean canDamage = true;
	@Getter
	@Setter
	private boolean canAttack = true;
	private int attackCountOrigin;
	@Getter
	private int attackCount;
	@Getter
	private List<Skill> skillList = new ArrayList<>();
	@Getter
	private boolean isNowParentTeam = true;

	public GamePlayer(Player player, GameTeam parentTeam) {
		this.player = player;
		this.parentTeam = parentTeam;
		this.nowTeam = parentTeam;

		FileConfiguration configuration = ColorRoyal.getPlugin().getConfig();
		if (!configuration.contains("AttackCount")) {
			configuration.set("AttackCount", 3);
			ColorRoyal.getPlugin().saveConfig();
			configuration = ColorRoyal.getPlugin().getConfig();
		}
		this.attackCountOrigin = configuration.getInt("AttackCount");
		this.attackCount = attackCountOrigin;

		player.setScoreboard(parentTeam.getTeam().getScoreboard());
		parentTeam.getTeam().addEntry(player.getName());
	}

	public void remainAttackCount() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!ColorRoyal.getPlugin().getGameManager().isGameing()) {
					cancel();
					return;
				}
				if (!isNowParentTeam) {
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							TextComponent.fromLegacyText(
									TextConfig.getGameMessageText(GameMessageText.RemainingAttackCount,
											String.valueOf(attackCount))));
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 20L);
	}

	public void changeTeam(GameTeam newTeam) {
		Scoreboard scoreboard = player.getScoreboard();
		scoreboard.resetScores(TextConfig.getSideBarText(SideBarText.NowColor, nowTeam.getTeam().getName()));

		nowTeam = newTeam;

		if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
			scoreboard.getObjective(DisplaySlot.SIDEBAR)
					.getScore(TextConfig.getSideBarText(
							SideBarText.NowColor,
							newTeam.getTeam().getName()))
					.setScore(5);
		}

		if (newTeam.getColor() == parentTeam.getColor()) {
			this.isNowParentTeam = true;
			this.attackCount = this.attackCountOrigin;

			Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
				PlayerUtils.teleportPlayer(player, ColorRoyal.getPlugin().getGameManager().getGameMap());
			}, 0L);
			PlayerUtils.sendMessage(player, TextConfig.getGameMessageText(GameMessageText.ReturnParentColor));
		} else {
			this.isNowParentTeam = false;
		}
	}

	public void attackCoolDown() {
		setCanAttack(false);
		new BukkitRunnable() {
			@Override
			public void run() {
				setCanAttack(true);
			}
		}.runTaskLater(ColorRoyal.getPlugin(), 3 * 20L);
	}

	public void damage(GamePlayer damager) {
		damager.attackCoolDown();
		this.changeTeam(damager.getNowTeam());
		this.changeArmorColor(nowTeam);
		this.invincible();

		if(damager.isNowParentTeam) {
			damager.getParentTeam().addPoint(2);
		} else {
			damager.getParentTeam().addPoint();
			damager.getNowTeam().addPoint();
		}
		damager.getPlayer().playSound(damager.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		PlayerUtils.sendMessage(player, damager.getNowTeam().getTeam().getName() + "に染められました！");
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);

		if (damager.getNowTeam().getColor() != damager.getParentTeam().getColor()) {
			damager.reduceAttackCount();
		}

		Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
			GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
			if (gameManager.getGamePlayerList().stream()
					.filter(gamePlayer -> gamePlayer.player.isOnline())
					.allMatch(gamePlayer -> gamePlayer.nowTeam == damager.nowTeam)) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					PlayerUtils.sendMessage(player, TextConfig.getGameMessageText(GameMessageText.AllColoredChat));
				}
				gameManager.stop();
			}
		}, 0L);
	}

	private void changeArmorColor(GameTeam team) {
		if (player.getInventory().getHelmet() == null) {
			return;
		}

		ItemStack helmet = player.getInventory().getHelmet();
		LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
		meta.setColor(Color.fromRGB(team.getColor()));
		for (ItemStack armor : player.getInventory().getArmorContents()) {
			armor.setItemMeta(meta);
		}

		player.updateInventory();
	}

	public void sendArmor(GameTeam gameTeam) {
		player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));

		changeArmorColor(gameTeam);
	}

	public void invincible() {
		canDamage = false;
		sendDiamondArmor();

		new BukkitRunnable() {
			@Override
			public void run() {
				canDamage = true;
				sendArmor(nowTeam);
			}
		}.runTaskLater(ColorRoyal.getPlugin(), 5 * 20L);
	}

	private void sendDiamondArmor() {
		player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
		player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
	}

	private void reduceAttackCount() {
		this.attackCount--;
		if (attackCount == 0) {
			this.attackCount = attackCountOrigin;
			changeTeam(parentTeam);
			parentTeam.addPoint(3);
			sendArmor(nowTeam);
		}
	}

	public boolean hasSkill(SkillType skillType) {
		for (Skill skill : skillList) {
			if (skill.getEnumSkill() == skillType) {
				return true;
			}
		}

		return false;
	}
}
