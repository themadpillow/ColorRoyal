package madpillow.colorRoyal.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import lombok.Getter;
import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.PlayerUtils;
import madpillow.colorRoyal.configUtils.GameMessageText;
import madpillow.colorRoyal.configUtils.SideBarText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GameManager;
import madpillow.colorRoyal.game.GamePlayer;

public abstract class Skill {
	@Getter
	protected Skills enumSkill;
	@Getter
	protected ItemStack itemStack;
	@Getter
	protected String name;
	@Getter
	protected int coolTime;
	protected boolean isActive = true;
	@Getter
	protected List<String> loresInfo;
	protected GamePlayer gamePlayer;

	public Skill(GamePlayer gamePlayer) {
		for (Skills skills : Skills.values()) {
			if (skills.toString().equalsIgnoreCase(this.getClass().getSimpleName())) {
				enumSkill = skills;
			}
		}
		if (enumSkill == null) {
			PlayerUtils.broadcastMessage("ERROR: 不明なSkill(" + name + ")");
		}
		this.name = enumSkill.getSkillName();

		setCoolTime();
		this.gamePlayer = gamePlayer;
		loresInfo = new ArrayList<>();
		setLoresInfo();
		initItemStack();
	}

	protected void initItemStack() {
		itemStack = new ItemStack(enumSkill.getMaterial());
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(loresInfo);

		itemStack.setItemMeta(meta);
	}

	public void setCoolTime() {
		FileConfiguration configuration = ColorRoyal.getPlugin().getConfig();
		if (!configuration.contains(name + ".CT")) {
			configuration.set(name + ".CT", 30);
			ColorRoyal.getPlugin().saveConfig();
		}

		coolTime = configuration.getInt(name + ".CT", 30);
	}

	public boolean perform() {
		if (!isActive) {
			notActiveMessage();
			return false;
		} else {
			gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 2);
			PlayerUtils.sendMessage(gamePlayer.getPlayer(),
					TextConfig.getGameMessageText(GameMessageText.PerformSkill, name));

			Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
				actionSkill();
				coolDown();
			}, 0L);

			return true;
		}
	}

	protected abstract void actionSkill();

	protected void notActiveMessage() {
		PlayerUtils.sendMessage(gamePlayer.getPlayer(), TextConfig.getGameMessageText(GameMessageText.SkillCT));
	}

	protected abstract void setLoresInfo();

	protected void coolDown() {
		isActive = false;

		int scorePos = gamePlayer.getSkillList().get(0) == this ? 1 : 0;
		Scoreboard scoreboard = gamePlayer.getPlayer().getScoreboard();
		scoreboard.resetScores(TextConfig.getSideBarText(SideBarText.Skill, name, "0"));
		Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		objective.getScore(TextConfig.getSideBarText(SideBarText.Skill, name, String.valueOf(coolTime)))
				.setScore(scorePos);

		coolDownTask(scoreboard, scorePos);
	}

	private void coolDownTask(Scoreboard scoreboard, int scorePos) {
		new BukkitRunnable() {
			int time = coolTime;
			Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
			GameManager gameManager = ColorRoyal.getPlugin().getGameManager();

			@Override
			public void run() {
				if (!gameManager.isGameing()) {
					cancel();
					return;
				}

				scoreboard.resetScores(TextConfig.getSideBarText(SideBarText.Skill, name, String.valueOf(time)));
				time--;
				objective.getScore(TextConfig.getSideBarText(SideBarText.Skill, name, String.valueOf(time)))
						.setScore(scorePos);

				if (time == 0) {
					isActive = true;
					cancel();
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 20L);
	}
}
