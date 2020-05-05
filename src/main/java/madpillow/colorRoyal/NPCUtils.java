package madpillow.colorRoyal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import madpillow.colorRoyal.game.GamePlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;

public class NPCUtils {
	private static final List<NPC> npcs = new ArrayList<>();

	public static void init() {
		new BukkitRunnable() {
			@Override
			public void run() {
				List<GamePlayer> gamePlayers = ColorRoyal.getPlugin().getGameManager().getGamePlayerList();
				for (NPC npc : npcs) {
					if (!npc.isSpawned()) {
						return;
					}
					GamePlayer gamePlayer = gamePlayers.get(new Random().nextInt(gamePlayers.size()));
					if (gamePlayer.getPlayer().isOnline()) {
						npc.getNavigator().setTarget(gamePlayer.getPlayer(), false);
					}
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 0L, 10 * 20L);
	}

	public static NPC create(GamePlayer gamePlayer) {
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, gamePlayer.getPlayer().getName());
		npcs.add(npc);
		npc.setProtected(false);
		serArmor(npc, gamePlayer);

		npc.spawn(gamePlayer.getPlayer().getLocation());

		return npc;
	}

	private static void serArmor(NPC npc, GamePlayer gamePlayer) {
		Equipment equipment = npc.getTrait(Equipment.class);
		PlayerInventory inventory = gamePlayer.getPlayer().getInventory();
		equipment.set(EquipmentSlot.HELMET, inventory.getHelmet());
		equipment.set(EquipmentSlot.CHESTPLATE, inventory.getChestplate());
		equipment.set(EquipmentSlot.LEGGINGS, inventory.getLeggings());
		equipment.set(EquipmentSlot.BOOTS, inventory.getBoots());
		npc.addTrait(equipment);
	}

	public static void destroy(NPC npc) {
		npcs.remove(npc);
		npc.despawn();
		npc.destroy();
		CitizensAPI.getNPCRegistry().deregister(npc);
	}

	public static void destroyAll() {
		for (NPC npc : npcs) {
			npc.despawn();
			npc.destroy();
			CitizensAPI.getNPCRegistry().deregister(npc);
		}
		CitizensAPI.getNPCRegistry().deregisterAll();
	}
}
