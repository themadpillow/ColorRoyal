package madpillow.colorRoyal.configUtils;

public enum GameMessageText {
	UntilStartChat, UntilStartTitle, UntilStartSubTitle, StartTitle, StartSubTitle, ReturnParentColor, AttackCT, DamageCTDamager, DamageCTTarget, PerformSkill, SkillCT, StopTitle, StopSubTitle, ChatPrefix, AllResetAnnounceTitle, AllResetAnnounceSubTitle, AllResetAnnounceChat, AllResetIntervalTitle, AllResetIntervalSubTitle, AllResetIntervalChat, AllResetTitle, AllResetChat, RemainingAttackCount, AllColoredChat, DestroyDecoy;

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
		case ChatPrefix:
			return "§l[ColorRoyal]§r ";
		case AllResetAnnounceTitle:
			return "まもなく色がリセットされリスポーンします";
		case AllResetAnnounceSubTitle:
			return "リセットまで...<VALUE> 秒";
		case AllResetAnnounceChat:
			return "リセットまで...<VALUE> 秒";
		case AllResetIntervalTitle:
			return "まもなく色がリセットされリスポーンします";
		case AllResetIntervalSubTitle:
			return "リセットまで...<VALUE> 秒";
		case AllResetIntervalChat:
			return "リセットまで...<VALUE> 秒";
		case AllResetTitle:
			return "リセットされました！";
		case AllResetChat:
			return "リセットされました！";
		case RemainingAttackCount:
			return "残り：<VALUE>";
		case AllColoredChat:
			return "全員同じ色になったためゲーム終了がしました";
		case DestroyDecoy:
			return "デコイが破壊されました。コンパスが位置を指しています";
		default:
			throw new RuntimeException("不明なText");
		}
	}
}
