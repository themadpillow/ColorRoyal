package madpillow.colorRoyal.game;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;

public class GameMap {
	@Getter
	private World world;
	@Getter
	private Location location;
	@Getter
	private int teleportDistance;

	public GameMap(Location location, int teleportDistance) {
		this.location = location;
		this.teleportDistance = teleportDistance;

		this.world = location.getWorld();
	}
}
