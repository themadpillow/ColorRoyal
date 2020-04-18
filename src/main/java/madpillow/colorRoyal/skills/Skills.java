package madpillow.colorRoyal.skills;

import org.bukkit.Material;

import madpillow.colorRoyal.configUtils.SkillText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GamePlayer;

public enum Skills {
	Blindness, Glowing, Invisiblity, Speed, Teleport;

	public Skill getSkill(GamePlayer gamePlayer) {
		switch (this) {
		case Blindness:
			return new Blindness(gamePlayer);
		case Glowing:
			return new Glowing(gamePlayer);
		case Invisiblity:
			return new Invisiblity(gamePlayer);
		case Speed:
			return new Speed(gamePlayer);
		case Teleport:
			return new Teleport(gamePlayer);
		default:
			throw new RuntimeException("不明なSkill");
		}
	}

	public Material getMaterial() {
		switch (this) {
		case Blindness:
			return Material.CHARCOAL;
		case Glowing:
			return Material.GLOWSTONE_DUST;
		case Invisiblity:
			return Material.TOTEM_OF_UNDYING;
		case Speed:
			return Material.FEATHER;
		case Teleport:
			return Material.ENDER_EYE;
		default:
			throw new RuntimeException("不明なSkill");
		}
	}

	public String getSkillName() {
		switch (this) {
		case Blindness:
			return TextConfig.getSkillText(SkillText.Blindness);
		case Glowing:
			return TextConfig.getSkillText(SkillText.Glowing);
		case Invisiblity:
			return TextConfig.getSkillText(SkillText.Invisiblity);
		case Speed:
			return TextConfig.getSkillText(SkillText.Speed);
		case Teleport:
			return TextConfig.getSkillText(SkillText.Teleport);
		default:
			throw new RuntimeException("不明なSkill");
		}
	}
}
