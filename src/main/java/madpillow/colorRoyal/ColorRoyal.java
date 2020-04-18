package madpillow.colorRoyal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import madpillow.colorRoyal.commands.SetCommand;
import madpillow.colorRoyal.commands.SkillItemCommand;
import madpillow.colorRoyal.commands.StartCommand;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GameManager;
import madpillow.colorRoyal.game.GameTeam;
import madpillow.colorRoyal.scoreboard.ScoreBoardUtils;
import madpillow.colorRoyal.skills.SkillSelectInventory;

public class ColorRoyal extends JavaPlugin {
	@Getter
	private static ColorRoyal plugin;
	@Getter
	private GameManager gameManager;

	@Override
	public void onEnable() {
		this.plugin = this;
		this.gameManager = new GameManager();
		TextConfig.initTextConfig();

		Bukkit.getPluginManager().registerEvents(new Events(), this);

		getCommand("start").setExecutor(new StartCommand());
		getCommand("set").setExecutor(new SetCommand());
		getCommand("skillitem").setExecutor(new SkillItemCommand());

		loadTeam();
		initTimeBossBar();

		for (Player player : Bukkit.getOnlinePlayers()) {
			gameManager.getGameTeamListManager().joinGame(player);
			if (!player.getInventory().contains(Material.NETHER_STAR)) {
				player.getInventory().addItem(SkillSelectInventory.getItemStack());
			}
		}
	}

	@Override
	public void onDisable() {
		Bukkit.getBossBar(NamespacedKey.minecraft("time")).removeAll();
	}

	private void loadTeam() {
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
			Team team = ScoreBoardUtils.createTeam(teamName);
			gameManager.getGameTeamListManager().addGameTeam(new GameTeam(team, (int) set.getValue()));
		}
	}

	private void createTeamConfig() {
		FileConfiguration configuration = getConfig();
		Map<String, Integer> teams = new HashMap<>();
		teams.put("Red", 16711680);

		configuration.set("Team", teams);
		saveConfig();
	}

	private void initTimeBossBar() {
		FileConfiguration configuration = this.getConfig();
		if (!configuration.contains("Time")) {
			configuration.set("Time", 300);
			this.saveConfig();
		}

		BossBar bar = Bukkit.createBossBar(NamespacedKey.minecraft("time"), "残り時間：試合開始前", BarColor.GREEN,
				BarStyle.SEGMENTED_10);
		bar.setProgress(1.0);
	}
}
