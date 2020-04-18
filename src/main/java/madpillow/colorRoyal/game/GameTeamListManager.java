package madpillow.colorRoyal.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;

import lombok.Getter;
import madpillow.colorRoyal.ColorRoyal;

public class GameTeamListManager {
	@Getter
	private List<GameTeam> gameTeamList;

	public GameTeamListManager() {
		gameTeamList = new ArrayList<>();
	}

	public GamePlayer joinGame(Player player) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		if (gameManager.isGameing()) {
			return null;
		}
		while (true) {
			GameTeam gameTeam = null;
			int minSize = 0;
			for (GameTeam team : gameTeamList) {
				if (team.getTeam().getSize() == minSize) {
					gameTeam = team;
					break;
				}
			}
			if (gameTeam != null) {
				GamePlayer gamePlayer = new GamePlayer(player, gameTeam);
				gameManager.getGamePlayerList().add(gamePlayer);

				return gamePlayer;
			} else {
				minSize++;
			}
		}
	}

	public void addGameTeam(GameTeam gameTeam) {
		this.gameTeamList.add(gameTeam);
	}

	public Optional<GamePlayer> getGamePlayerAtList(Player player) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		for (GamePlayer gamePlayer : gameManager.getGamePlayerList()) {
			if (gamePlayer.getPlayer() == player) {
				return Optional.of(gamePlayer);
			}
		}

		return Optional.empty();
	}
}
