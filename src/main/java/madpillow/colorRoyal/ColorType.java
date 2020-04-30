package madpillow.colorRoyal;

import org.bukkit.ChatColor;

public enum ColorType {
	RED, GOLD, YELLOW, GREEN, AQUA, BLUE, LIGHT_PURPLE;

	public ChatColor getChatColor() {
		return ChatColor.valueOf(this.name());
	}
}
