package madpillow.colorRoyal;

import org.bukkit.scoreboard.Team;

import lombok.Getter;

public class GameTeam {
	@Getter
	private Team team;
	@Getter
	private int color;

	public GameTeam(Team team, int color) {
		this.team = team;
		this.color = color;
	}
}
