package madpillow.colorRoyal.configUtils;

import java.util.HashMap;
import java.util.Map;

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
		for (SkillText skillText : SkillText.values()) {
			skillMap.put(skillText.toString(), skillText.getDefaultString());
		}

		skillMap = checkConfigurationSectionData("Skill", skillMap);
	}

	private static void initGameMessage() {
		gameMessageMap = new HashMap<>();
		for (GameMessageText gameMessageText : GameMessageText.values()) {
			gameMessageMap.put(gameMessageText.toString(), gameMessageText.getDefaultString());
		}
		gameMessageMap = checkConfigurationSectionData("GameMessage", gameMessageMap);
	}

	private static Map<String, String> checkConfigurationSectionData(String sectionPath,
			Map<String, String> map) {
		FileConfiguration configuration = config.getConfig();
		ConfigurationSection section = configuration.getConfigurationSection(sectionPath);
		if (section == null) {
			section = configuration.createSection(sectionPath);
		}

		for (String key : map.keySet()) {
			if (!section.contains(key)) {
				section.set(key, map.get(key));
			} else {
				map.put(key, section.getString(key));
			}
		}
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
					.replace("<SKILL_NAME>", values[0]).replace("<STATUS>", status);
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
		case AllResetAnnounceSubTitle:
		case AllResetAnnounceChat:
		case AllResetIntervalSubTitle:
		case AllResetIntervalChat:
		case RemainingAttackCount:
			return gameMessageMap.get(gameMessageText.toString()).replace("<VALUE>", values[0]);
		default:
			return gameMessageMap.get(gameMessageText.toString());
		}
	}
}
