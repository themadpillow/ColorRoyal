package madpillow.colorRoyal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import lombok.Getter;

public class ColorRoyal extends JavaPlugin {
	@Getter
	private static Plugin plugin;

	@Getter
	private List<GamePlayer> gamePlayerList;
	@Getter
	private List<GameTeam> teamList;

	@Override
	public void onEnable() {
		this.plugin = this;
		this.gamePlayerList = new ArrayList<>();
		this.teamList = new ArrayList<>();

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
				teamList.add(
						new GameTeam(board.getTeam(teamName), new Color(teamSection.getInt("R"),
								teamSection.getInt("G"), teamSection.getInt("B"))));

				continue;
			}

			Team team = board.registerNewTeam(teamName);
			MemorySection teamSection = (MemorySection) set.getValue();
			teamList.add(new GameTeam(team, new Color(teamSection.getInt("R"),
					teamSection.getInt("G"), teamSection.getInt("B"))));
		}
	}

	private void createTeamConfig() {
		FileConfiguration configuration = getConfig();
		Map<String, Integer> RGB = new LinkedHashMap<>();
		RGB.put("R", 255);
		RGB.put("G", 0);
		RGB.put("B", 0);

		Map<String, Map<String, Integer>> teams = new HashMap<>();
		teams.put("Red", RGB);

		configuration.set("Team", teams);
		saveConfig();
	}

	public void start() {
		int teamIndex = 0;
		for (Player player : Bukkit.getOnlinePlayers()) {
			gamePlayerList.add(new GamePlayer(player, teamList.get(teamIndex)));
			teamIndex++;
			if (teamIndex >= teamList.size()) {
				teamIndex = 0;
			}
		}
	}
}
