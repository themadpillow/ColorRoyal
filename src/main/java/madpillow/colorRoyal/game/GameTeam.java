package madpillow.colorRoyal.game;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import madpillow.colorRoyal.configUtils.SideBarText;
import madpillow.colorRoyal.configUtils.TextConfig;

public class GameTeam {
	@Getter
	private Team team;
	@Getter
	private int color;
	@Getter
	private int point = 0;

	public GameTeam(Team team, int color) {
		this.team = team;
		this.color = color;

	}

	public void addPoint() {
		team.getPlayers().stream().filter(OfflinePlayer::isOnline).forEach(player -> {
			Scoreboard scoreboard = player.getPlayer().getScoreboard();
			scoreboard.resetScores(TextConfig.getSideBarText(SideBarText.Point, String.valueOf(point)));
			point++;
			scoreboard.getObjective(DisplaySlot.SIDEBAR)
					.getScore(TextConfig.getSideBarText(SideBarText.Point, String.valueOf(point)))
					.setScore(4);
		});
	}
}
