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

		for (GameTeam team : gameTeamList) {
			if (team.getTeam().getSize() == 0) {
				GamePlayer gamePlayer = new GamePlayer(player, team);
				gameManager.getGamePlayerList().add(gamePlayer);

				break;
			}
		}

		return null;
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

	public Optional<GamePlayer> getHardGamePlayerAtList(Player player) {
		GameManager gameManager = ColorRoyal.getPlugin().getGameManager();
		for (GamePlayer gamePlayer : gameManager.getGamePlayerList()) {
			if (gamePlayer.getPlayer().getName().equals(player.getName())) {
				return Optional.of(gamePlayer);
			}
		}

		return Optional.empty();
	}
}
