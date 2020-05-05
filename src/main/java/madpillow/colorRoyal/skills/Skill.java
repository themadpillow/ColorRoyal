package madpillow.colorRoyal.skills;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
	protected SkillType enumSkill;
	@Getter
	protected String name;
	protected boolean isActive = true;
	protected GamePlayer gamePlayer;

	public Skill(GamePlayer gamePlayer) {
		for (SkillType skills : SkillType.values()) {
			if (skills.toString().equalsIgnoreCase(this.getClass().getSimpleName())) {
				enumSkill = skills;
			}
		}
		if (enumSkill == null) {
			PlayerUtils.broadcastMessage("ERROR: 不明なSkill(" + name + ")");
		}
		this.name = enumSkill.getSkillName();

		this.gamePlayer = gamePlayer;
	}

	public boolean perform() {
		if (!isActive) {
			notActiveMessage();
			return false;
		} else {
			Bukkit.getScheduler().runTaskLater(ColorRoyal.getPlugin(), () -> {
				if (actionSkill()) {
					gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENTITY_TNT_PRIMED, 1,
							2);
					PlayerUtils.sendMessage(gamePlayer.getPlayer(),
							TextConfig.getGameMessageText(GameMessageText.PerformSkill, name));
					coolDown();
				}
			}, 0L);

			return true;
		}
	}

	protected abstract boolean actionSkill();

	protected void notActiveMessage() {
		PlayerUtils.sendMessage(gamePlayer.getPlayer(), TextConfig.getGameMessageText(GameMessageText.SkillCT));
	}

	protected void coolDown() {
		isActive = false;

		int scorePos = gamePlayer.getSkillList().get(0) == this ? 1 : 0;
		Scoreboard scoreboard = gamePlayer.getPlayer().getScoreboard();
		scoreboard.resetScores(TextConfig.getSideBarText(SideBarText.Skill, name, "0"));
		Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		objective.getScore(TextConfig.getSideBarText(SideBarText.Skill, name, String.valueOf(enumSkill.getCoolTime())))
				.setScore(scorePos);

		coolDownTask(scoreboard, scorePos);
	}

	private void coolDownTask(Scoreboard scoreboard, int scorePos) {
		new BukkitRunnable() {
			int time = enumSkill.getCoolTime();
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
