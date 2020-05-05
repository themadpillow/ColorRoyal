package madpillow.colorRoyal.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.configUtils.SkillText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GamePlayer;

public enum SkillType {
	Blindness, Glowing, Invisiblity, Speed, Teleport, HighJump, ChargeJump, Decoy;

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
		case HighJump:
			return new HighJump(gamePlayer);
		case ChargeJump:
			return new ChargeJump(gamePlayer);
		case Decoy:
			return new Decoy(gamePlayer);
		default:
			throw new RuntimeException("不明なSkill");
		}
	}

	public ItemStack getItemStack() {
		ItemStack itemStack = new ItemStack(this.getMaterial());
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(this.getSkillName());
		meta.setLore(getLore());

		itemStack.setItemMeta(meta);

		return itemStack;
	}

	public Material getMaterial() {
		switch (this) {
		case Blindness:
			return Material.CHARCOAL;
		case Glowing:
			return Material.GLOWSTONE_DUST;
		case Invisiblity:
			return Material.QUARTZ;
		case Speed:
			return Material.FEATHER;
		case Teleport:
			return Material.ENDER_EYE;
		case HighJump:
			return Material.SLIME_BALL;
		case ChargeJump:
			return Material.GUNPOWDER;
		case Decoy:
			return Material.TOTEM_OF_UNDYING;
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
		case HighJump:
			return TextConfig.getSkillText(SkillText.HighJump);
		case ChargeJump:
			return TextConfig.getSkillText(SkillText.ChargeJump);
		case Decoy:
			return TextConfig.getSkillText(SkillText.Decoy);
		default:
			throw new RuntimeException("不明なSkill");
		}
	}

	public List<String> getLore() {
		List<String> loresInfo = new ArrayList<>();
		switch (this) {
		case Blindness:
			loresInfo.add("右クリックで使用");
			loresInfo.add("半径10ブロック以内の");
			loresInfo.add(" 他チームのプレイヤーに3秒間盲目付与");
			break;
		case Glowing:
			loresInfo.add("右クリックで使用");
			loresInfo.add("他チームのプレイヤーに5秒間発光を付与");
			break;
		case Invisiblity:
			loresInfo.add("右クリックで使用");
			loresInfo.add("5秒間透明化");
			break;
		case Speed:
			loresInfo.add("右クリックで使用");
			loresInfo.add("5秒間移動速度上昇");
			break;
		case Teleport:
			loresInfo.add("右クリックで使用");
			loresInfo.add("一度目の使用でテレポート先を設定");
			loresInfo.add("二度目の使用で設定先にテレポート");
			break;
		case HighJump:
			loresInfo.add("右クリックで使用");
			loresInfo.add("5秒間跳躍力上昇を付与");
			break;
		case ChargeJump:
			loresInfo.add("右クリックで使用");
			loresInfo.add("シフトでチャージ");
			loresInfo.add("チャージした分だけ強く視点の方向へ飛ぶ");
			break;
		case Decoy:
			loresInfo.add("右クリックで使用");
			loresInfo.add("自分の分身を召喚する（１体のみ）");
			loresInfo.add("分身が破壊された時コンパスが破壊者を指す");
			loresInfo.add("破壊後" + getCharge(20) + "秒間は再使用できない");
			break;
		default:
			throw new RuntimeException("不明なSkill");
		}

		loresInfo.add("CT：" + getCoolTime() + "秒");

		return loresInfo;
	}

	public int getCoolTime() {
		FileConfiguration configuration = ColorRoyal.getPlugin().getConfig();
		if (!configuration.contains(this.name() + ".CT")) {
			configuration.set(this.name() + ".CT", 30);
			ColorRoyal.getPlugin().saveConfig();
		}

		return configuration.getInt(this.name() + ".CT", 30);
	}

	public int getCharge(int def) {
		FileConfiguration configuration = ColorRoyal.getPlugin().getConfig();
		if (!configuration.contains(this.name() + ".CHARGE")) {
			configuration.set(this.name() + ".CHARGE", def);
			ColorRoyal.getPlugin().saveConfig();
		}

		return configuration.getInt(this.name() + ".CHARGE", def);
	}

	public float getCharge(float def) {
		FileConfiguration configuration = ColorRoyal.getPlugin().getConfig();
		if (!configuration.contains(this.name() + ".CHARGE")) {
			configuration.set(this.name() + ".CHARGE", def);
			ColorRoyal.getPlugin().saveConfig();
		}

		return (float) configuration.getDouble(this.name() + ".CHARGE", def);
	}

	public float getSpeed(float def) {
		FileConfiguration configuration = ColorRoyal.getPlugin().getConfig();
		if (!configuration.contains(this.name() + ".SPEED")) {
			configuration.set(this.name() + ".SPEED", def);
			ColorRoyal.getPlugin().saveConfig();
		}

		return (float) configuration.getDouble(this.name() + ".SPEED", def);
	}
}
