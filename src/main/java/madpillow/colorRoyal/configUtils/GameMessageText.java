package madpillow.colorRoyal.configUtils;

public enum GameMessageText {
	UntilStartChat, UntilStartTitle, UntilStartSubTitle, StartTitle, StartSubTitle, ReturnParentColor, AttackCT, DamageCTDamager, DamageCTTarget, PerformSkill, SkillCT, StopTitle, StopSubTitle;

	public String getDefaultString() {
		switch (this) {
		case UntilStartChat:
			return "ゲーム開始まで<VALUE> 秒";
		case UntilStartTitle:
			return "ColorRoyal";
		case UntilStartSubTitle:
			return "開始まで...<VALUE> 秒";
		case StartTitle:
			return "ColorRoyal";
		case StartSubTitle:
			return "スタート";
		case ReturnParentColor:
			return "元の色に戻りました！";
		case AttackCT:
			return "§cあなたは現在アタックCT中です！";
		case DamageCTDamager:
			return "§eあなたは現在ダメージCT中です！";
		case DamageCTTarget:
			return "§eこのプレイヤーは現在ダメージCT中です！";
		case PerformSkill:
			return "§e<VALUE>を発動しました！";
		case SkillCT:
			return "§bこのスキルはCT中です！";
		case StopTitle:
			return "§c～ゲーム終了～";
		case StopSubTitle:
			return "§b[ §6結果発表 チャットを確認 §b]";
		default:
			throw new RuntimeException("不明なText");
		}
	}
}
