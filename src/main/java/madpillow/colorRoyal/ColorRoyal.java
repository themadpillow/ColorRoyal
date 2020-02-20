package madpillow.colorRoyal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import lombok.Getter;

public class ColorRoyal extends JavaPlugin {
	@Getter
	private static ColorRoyal plugin;
	@Getter
	public boolean isGameing = false;

	@Getter
	private List<GamePlayer> gamePlayerList;
	@Getter
	private List<GameTeam> teamList;

	@Override
	public void onEnable() {
		this.plugin = this;
		this.gamePlayerList = new ArrayList<>();
		this.teamList = new ArrayList<>();

		Commands commands = new Commands();
		getCommand("start").setExecutor(commands);
		getCommand("stop").setExecutor(commands);

		Bukkit.getPluginManager().registerEvents(new GameEvent(), this);
		initTeam();
	}

	private void initTeam() {
		FileConfiguration configuration = getConfig();
		ConfigurationSection section = configuration.getConfigurationSection("Team");
		if (section == null) {
			createTeamConfig();
			reloadConfig();
			configuration = getConfig();
			section = configuration.getConfigurationSection("Team");
		}

		Map<String, Object> teams = section.getValues(false);

		for (Entry<String, Object> set : teams.entrySet()) {
			String teamName = set.getKey();
			Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
			if (board.getTeam(teamName) != null) {
				MemorySection teamSection = (MemorySection) set.getValue();
				teamList.add(new GameTeam(board.getTeam(teamName), teamSection.getInt("Color")));

				continue;
			}

			Team team = board.registerNewTeam(teamName);
			MemorySection teamSection = (MemorySection) set.getValue();
			teamList.add(new GameTeam(team, teamSection.getInt("Color")));
		}
	}

	private void createTeamConfig() {
		FileConfiguration configuration = getConfig();
		Map<String, Integer> teams = new HashMap<>();
		teams.put("Red", 16711680);

		configuration.set("Team", teams);
		saveConfig();
	}

	public void start() {
		if (isGameing) {
			Bukkit.broadcastMessage("すでに開始しています");
			return;
		}

		int teamIndex = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			gamePlayerList.add(new GamePlayer(player, teamList.get(teamIndex)));
			teamList.get(teamIndex).getTeam().addEntry(player.getName());

			teamIndex++;
			if (teamIndex >= teamList.size()) {
				teamIndex = 0;
			}
		}

		isGameing = true;
	}

	public void stop() {
		if (!isGameing) {
			Bukkit.broadcastMessage("まだ開始されていません");
			return;
		}
		isGameing = false;
	}

	public GamePlayer convertToGamePlayer(Player player) {
		for (GamePlayer gamePlayer : gamePlayerList) {
			if (gamePlayer.getPlayer() == player) {
				return gamePlayer;
			}
		}

		return null;
	}
}
