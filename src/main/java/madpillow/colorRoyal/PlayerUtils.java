package madpillow.colorRoyal;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import madpillow.colorRoyal.configUtils.GameMessageText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.game.GameMap;

public class PlayerUtils {
	public static boolean canSee(Player player, Player target) {
		if (player.getWorld() != target.getWorld()) {
			return false;
		} else {
			World world = player.getWorld();
			Location from = player.getEyeLocation();
			Location to = target.getEyeLocation();
			int i = to.getBlockX();
			int j = to.getBlockY();
			int k = to.getBlockZ();
			int l = from.getBlockX();
			int i1 = from.getBlockY();
			int j1 = from.getBlockZ();
			for (int traceDistance = 100; traceDistance >= 0; --traceDistance) {
				if (l == i && i1 == j && j1 == k) {
					return true;
				}
				double d0 = 999.0D;
				double d1 = 999.0D;
				double d2 = 999.0D;
				double d3 = 999.0D;
				double d4 = 999.0D;
				double d5 = 999.0D;
				double d6 = to.getX() - from.getX();
				double d7 = to.getY() - from.getY();
				double d8 = to.getZ() - from.getZ();
				if (i > l) {
					d0 = (double) l + 1.0D;
					d3 = (d0 - from.getX()) / d6;
				} else if (i < l) {
					d0 = (double) l + 0.0D;
					d3 = (d0 - from.getX()) / d6;
				}
				if (j > i1) {
					d1 = (double) i1 + 1.0D;
					d4 = (d1 - from.getY()) / d7;
				} else if (j < i1) {
					d1 = (double) i1 + 0.0D;
					d4 = (d1 - from.getY()) / d7;
				}
				if (k > j1) {
					d2 = (double) j1 + 1.0D;
					d5 = (d2 - from.getZ()) / d8;
				} else if (k < j1) {
					d2 = (double) j1 + 0.0D;
					d5 = (d2 - from.getZ()) / d8;
				}
				byte b0;
				if (d3 < d4 && d3 < d5) {
					if (i > l) {
						b0 = 4;
					} else {
						b0 = 5;
					}
					from.setX(d0);
					from.add(0.0D, d7 * d3, d8 * d3);
				} else if (d4 < d5) {
					if (j > i1) {
						b0 = 0;
					} else {
						b0 = 1;
					}
					from.add(d6 * d4, 0.0D, d8 * d4);
					from.setY(d1);
				} else {
					if (k > j1) {
						b0 = 2;
					} else {
						b0 = 3;
					}
					from.add(d6 * d5, d7 * d5, 0.0D);
					from.setZ(d2);
				}
				l = from.getBlockX();
				i1 = from.getBlockY();
				j1 = from.getBlockZ();
				if (b0 == 5) {
					--l;
				}
				if (b0 == 1) {
					--i1;
				}
				if (b0 == 3) {
					--j1;
				}
				if (world.getBlockAt(l, i1, j1).getType().isOccluding()) {
					return false;
				}
			}
			return true;
		}
	}

	public static void broadcastMessage(String message) {
		String prefix = TextConfig.getGameMessageText(GameMessageText.ChatPrefix);
		Bukkit.broadcastMessage(prefix + message);
	}

	public static void sendMessage(Player player, String message) {
		String prefix = TextConfig.getGameMessageText(GameMessageText.ChatPrefix);
		player.sendMessage(prefix + message);
	}

	public static void createHelix(Player player, Location loc, double offset) {
		int radius = 1;
		for (double y = 0; y <= 21; y += 0.05) {
			double x = radius * Math.cos(y + offset);
			double z = radius * Math.sin(y + offset);

			player.spawnParticle(Particle.REDSTONE,
					(float) (loc.getX() + x), (float) (loc.getY() + (y / 8)), (float) (loc.getZ() + z), 0, 0,
					0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, (int) (8 * y), 255), 0.3F));
		}
	}

	public static void teleportPlayer(Player player, GameMap gameMap) {
		while (true) {
			Location tempLoc = gameMap.getLocation().clone();
			int x = (int) (Math.random() * gameMap.getTeleportDistance());
			if ((int) (Math.random() * 2) == 0) {
				x = -x;
			}
			int z = (int) (Math.random() * gameMap.getTeleportDistance());
			if ((int) (Math.random() * 2) == 0) {
				z = -z;
			}
			tempLoc.add(x, 0, z);
			if (tempLoc.getBlock().getType() != Material.AIR
					|| tempLoc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
				continue;
			}
			player.teleport(tempLoc);

			break;
		}
	}

}
