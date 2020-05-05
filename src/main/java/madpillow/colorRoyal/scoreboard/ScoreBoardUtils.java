package madpillow.colorRoyal.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import madpillow.colorRoyal.configUtils.SideBarText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GamePlayer;

public class ScoreBoardUtils {
	public static Team createTeam(String teamName) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Team team = board.registerNewTeam(teamName);
		team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);

		return team;
	}

	public static void createSideBar(GamePlayer gamePlayer) {
		Scoreboard scoreboard = gamePlayer.getPlayer().getScoreboard();
		Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
		if (objective != null) {
			objective.unregister();
		}
		objective = scoreboard.registerNewObjective("ColorRoyal", "ColorRoyal",
				TextConfig.getSideBarText(SideBarText.Title),
				RenderType.INTEGER);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		objective.getScore("").setScore(7);
		objective
				.getScore(TextConfig.getSideBarText(SideBarText.ParentColor,
						gamePlayer.getParentTeam().getTeam().getName()))
				.setScore(6);
		objective.getScore(TextConfig.getSideBarText(SideBarText.NowColor, gamePlayer.getNowTeam().getTeam().getName()))
				.setScore(5);
		objective
				.getScore(TextConfig.getSideBarText(SideBarText.Point,
						String.valueOf(gamePlayer.getParentTeam().getPoint())))
				.setScore(4);
		objective.getScore("").setScore(3);
		objective.getScore(TextConfig.getSideBarText(SideBarText.SkillTitle)).setScore(2);
		objective.getScore(
				TextConfig.getSideBarText(SideBarText.Skill, gamePlayer.getSkillList().get(0).getName(), "0"))
				.setScore(1);
		objective.getScore(
				TextConfig.getSideBarText(SideBarText.Skill, gamePlayer.getSkillList().get(1).getName(), "0"))
				.setScore(0);
	}

	public static void createWinnerColorTeam(GamePlayer gamePlayer) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = scoreboard.getTeam("WinnerTeam");
		if (team != null) {
			team.unregister();
		}
		team = scoreboard.registerNewTeam("WinnerTeam");

		team.addEntry(gamePlayer.getPlayer().getName());
	}
}
