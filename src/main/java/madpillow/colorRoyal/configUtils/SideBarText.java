package madpillow.colorRoyal.configUtils;

public enum SideBarText {
	Title, ParentColor, NowColor, Point, SkillTitle, Skill, Active, CoolTime;

	public String getDefaultString() {
		switch (this) {
		case Title:
			return "§cColor§9Royal";
		case ParentColor:
			return "§d最初の色 >> <VALUE>";
		case NowColor:
			return "§d現在の色 >> <VALUE>";
		case Point:
			return "§b§nポイント >> <VALUE>pt";
		case SkillTitle:
			return "§6☆ 発動スキル:状態 ☆";
		case Skill:
			return "§f<SKILL_NAME> >> <STATUS>";
		case Active:
			return "§a可能";
		case CoolTime:
			return "§cCT<VALUE>秒";
		default:
			throw new RuntimeException("不明なText");
		}
	}
}
