package madpillow.colorRoyal.configUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import madpillow.colorRoyal.ColorRoyal;

public class TextConfig {
	private static CustomConfig config;

	private static Map<String, String> sideBarMap;
	private static Map<String, String> skillMap;
	private static Map<String, String> gameMessageMap;

	public static void initTextConfig() {
		config = new CustomConfig(ColorRoyal.getPlugin(), "TextConfig.yml");
		reloadConfig();
	}

	public static void reloadConfig() {
		config.reloadConfig();
		initSideBar();
		initSkill();
		initGameMessage();
	}

	private static void initSideBar() {
		sideBarMap = new HashMap<>();
		for (SideBarText sideBarText : SideBarText.values()) {
			sideBarMap.put(sideBarText.toString(), sideBarText.getDefaultString());
		}

		sideBarMap = checkConfigurationSectionData("SideBar", sideBarMap);
	}

	private static void initSkill() {
		skillMap = new HashMap<>();
		for (SideBarText sideBarText : SideBarText.values()) {
			skillMap.put(sideBarText.toString(), sideBarText.getDefaultString());
		}

		skillMap = checkConfigurationSectionData("Skill", skillMap);
	}

	private static void initGameMessage() {
		gameMessageMap = new HashMap<>();
		for (SideBarText sideBarText : SideBarText.values()) {
			gameMessageMap.put(sideBarText.toString(), sideBarText.getDefaultString());
		}
		gameMessageMap = checkConfigurationSectionData("GameMessage", gameMessageMap);
	}

	private static Map<String, String> checkConfigurationSectionData(String sectionPath,
			Map<String, String> map) {
		FileConfiguration configuration = config.getConfig();
		final ConfigurationSection section = Optional.ofNullable(configuration.getConfigurationSection(sectionPath))
				.orElse(configuration.createSection(sectionPath));
		map.forEach((key, value) -> {
			if (!section.contains(key)) {
				section.set(key, value);
			} else {
				map.put(key, section.getString(key));
			}
		});

		config.saveConfig();
		return map;
	}

	public static String getSideBarText(SideBarText sideBarText, String... values) {
		switch (sideBarText) {
		case ParentColor:
		case NowColor:
		case Point:
			return sideBarMap.get(sideBarText.toString()).replace("<VALUE>", values[0]);
		case Skill:
			String status = values[1].equals("0") ? getActive() : getCoolTime(values[1]);
			return sideBarMap.get(sideBarText.toString())
					.replace("<SKILL_NAME>", values[0]).replace("<STATUS>", values[1]);
		default:
			return sideBarMap.get(sideBarText.toString());
		}
	}

	private static String getActive() {
		return sideBarMap.get(SideBarText.Active.toString());
	}

	private static String getCoolTime(String time) {
		return sideBarMap.get(SideBarText.CoolTime.toString()).replace("<VALUE>", time);
	}

	public static String getSkillText(SkillText skillText, String... values) {
		switch (skillText) {
		case SelectInventoryTitle:
			String skillListString = "";
			if (values == null || values.length == 0) {
				skillListString = "なし";
			} else {
				for (String skillName : values) {
					if (skillListString.length() != 0) {
						skillListString += " ";
					}
					skillListString += skillName;
				}
			}
			return skillMap.get(skillText.toString()).replace("<VALUE>", skillListString);
		default:
			return skillMap.get(skillText.toString());
		}
	}

	public static String getGameMessageText(GameMessageText gameMessageText, String... values) {
		switch (gameMessageText) {
		case UntilStartChat:
		case UntilStartSubTitle:
		case PerformSkill:
			return gameMessageMap.get(gameMessageText.toString()).replace("<VALUE>", values[0]);
		default:
			return gameMessageMap.get(gameMessageText.toString());
		}
	}
}
