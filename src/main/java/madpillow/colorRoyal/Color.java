package madpillow.colorRoyal;

import lombok.Data;

@Data
public class Color {
	private int red;
	private int green;
	private int blue;

	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
}
