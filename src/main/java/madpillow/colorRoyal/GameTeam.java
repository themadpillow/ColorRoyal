package madpillow.colorRoyal;

import org.bukkit.scoreboard.Team;
import lombok.Getter;

public class GameTeam {
	@Getter
	private Team team;
	@Getter
	private Color color;

	public GameTeam(Team team, Color color) {
		this.team = team;
		this.color = color;
	}
}
