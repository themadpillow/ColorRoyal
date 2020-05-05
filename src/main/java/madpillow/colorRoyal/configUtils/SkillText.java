package madpillow.colorRoyal.configUtils;

public enum SkillText {
	SelectItemName, SelectInventoryTitle, Blindness, Glowing, Invisiblity, Speed, Teleport, HighJump, CoolTime, ChargeJump, Decoy;

	public String getDefaultString() {
		switch (this) {
		case SelectItemName:
			return "§eスキル選択メニュー";
		case SelectInventoryTitle:
			return "スキル選択 §d選択済スキル[<VALUE>]";
		case Blindness:
			return "§eミエナクナール";
		case Glowing:
			return "§eピカピカリーン";
		case Invisiblity:
			return "§eスガタキエール";
		case Speed:
			return "§eハヤクナール";
		case Teleport:
			return "§eシュッスタッ";
		case CoolTime:
			return "§cCT<VALUE>秒";
		case HighJump:
			return "§eハイジャンプ";
		case ChargeJump:
			return "§eチャージジャンプ";
		case Decoy:
			return "§eデコイ";
		default:
			throw new RuntimeException("不明なText");
		}
	}
}
