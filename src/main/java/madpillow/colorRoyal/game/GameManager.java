package madpillow.colorRoyal.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import madpillow.colorRoyal.ColorRoyal;
import madpillow.colorRoyal.ColorType;
import madpillow.colorRoyal.NPCUtils;
import madpillow.colorRoyal.PlayerUtils;
import madpillow.colorRoyal.configUtils.GameMessageText;
import madpillow.colorRoyal.configUtils.TextConfig;
import madpillow.colorRoyal.scoreboard.ScoreBoardUtils;
import madpillow.colorRoyal.skills.Skill;
import madpillow.colorRoyal.skills.SkillSelectInventory;
import net.md_5.bungee.api.ChatColor;

public class GameManager {
	@Getter
	private boolean isGameing = false;
	@Getter
	private List<GamePlayer> gamePlayerList;
	@Getter
	private GameTeamListManager gameTeamListManager;
	@Getter
	private GameMap gameMap;

	public GameManager() {
		this.gamePlayerList = new ArrayList<>();
		this.gameTeamListManager = new GameTeamListManager();
	}

	public void start(GameMap gameMap) {
		if (isGameing) {
			PlayerUtils.broadcastMessage("すでに開始しています");
			return;
		}
		this.gameMap = gameMap;
		gameMap.getWorld().setDifficulty(Difficulty.PEACEFUL);

		NPCUtils.init();

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.setGameMode(GameMode.ADVENTURE);
			player.getInventory().clear();
			player.updateInventory();

			GamePlayer gamePlayer = gameTeamListManager.getGamePlayerAtList(player)
					.orElseGet(() -> gameTeamListManager.joinGame(player));

			if (gamePlayer == null) {
				PlayerUtils.sendMessage(player, "参加可能なチームが存在しません");
				continue;
			}
			gamePlayer.sendArmor(gamePlayer.getNowTeam());

			SkillSelectInventory.sendSkillItem(gamePlayer);
			PlayerUtils.sendMessage(gamePlayer.getPlayer(), "======選択したスキル=====");
			for (Skill skill : gamePlayer.getSkillList()) {
				PlayerUtils.sendMessage(gamePlayer.getPlayer(), skill.getName());
			}
			PlayerUtils.sendMessage(gamePlayer.getPlayer(), "=======================");

			gamePlayer.getPlayer().setPlayerListName(gamePlayer.getPlayer().getName());

			ScoreBoardUtils.createSideBar(gamePlayer);
		}

		gamePlayerList.forEach(gamePlayer -> PlayerUtils.teleportPlayer(gamePlayer.getPlayer(), gameMap));
		startTime();
	}

	public void stop() {
		if (!isGameing) {
			PlayerUtils.broadcastMessage("まだ開始されていません");
			return;
		}

		isGameing = false;
		Bukkit.getBossBar(NamespacedKey.minecraft("time")).removeAll();

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(ChatColor.RED + "ゲーム終了！", "");
			player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
			player.getInventory().clear();

			player.teleport(gameMap.getLocation());
			player.setGameMode(GameMode.ADVENTURE);

			Optional<GamePlayer> gamePlayer = gameTeamListManager.getGamePlayerAtList(player);
			if (gamePlayer.isPresent()) {
				gamePlayer.get().sendArmor(gamePlayer.get().getParentTeam());
				player.updateInventory();
			}
		}

		gameTeamListManager.getGameTeamList().sort(Comparator.comparing(GameTeam::getPoint).reversed());
		List<GameTeam> ranking[] = new ArrayList[3];

		for (int i = 0, rankingPos = 0; i < gameTeamListManager.getGameTeamList().size()
				&& rankingPos < 3; i++, rankingPos++) {
			if (gameTeamListManager.getGameTeamList().get(i).getPoint() == 0) {
				break;
			}
			ranking[rankingPos] = new ArrayList<>();
			ranking[rankingPos].add(gameTeamListManager.getGameTeamList().get(i));
			for (int j = i + 1; j < gameTeamListManager.getGameTeamList().size(); j++) {
				if (gameTeamListManager.getGameTeamList().get(i).getPoint() == gameTeamListManager.getGameTeamList()
						.get(j).getPoint()) {
					ranking[rankingPos].add(gameTeamListManager.getGameTeamList().get(j));
				} else {
					i = j - 1;
					break;
				}
			}
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendTitle(TextConfig.getGameMessageText(GameMessageText.StopTitle),
					TextConfig.getGameMessageText(GameMessageText.StopSubTitle));
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
		}

		new BukkitRunnable() {
			int rankingPos = 3;

			@Override
			public void run() {
				if (--rankingPos == -1) {
					ColorRoyal.getPlugin().reload();
					cancel();
					return;
				}
				if (ranking[rankingPos] == null || ranking[rankingPos].size() == 0) {
					return;
				}

				for (Player p : Bukkit.getOnlinePlayers()) {
					p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.5f);
				}
				Bukkit.broadcastMessage(
						"========== " + ChatColor.GREEN + (rankingPos + 1) + "位" + ChatColor.RESET + " ==========");
				Bukkit.broadcastMessage("");

				GamePlayer winnerPlayer = null;
				for (GameTeam gameTeam : ranking[rankingPos]) {
					String teamMemberString = "[";
					for (GamePlayer gamePlayer : gamePlayerList) {
						if (gameTeam.getColor() == gamePlayer.getParentTeam().getColor()) {
							winnerPlayer = gamePlayer;
							teamMemberString += " " + winnerPlayer.getPlayer().getName();
							winnerPlayer.getPlayer()
									.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 60, 1));
						}
					}
					teamMemberString += " ]";
					Bukkit.broadcastMessage(ChatColor.YELLOW + gameTeam.getTeam().getName() + teamMemberString);
				}

				Bukkit.broadcastMessage("");
				Bukkit.broadcastMessage("==========" + ChatColor.GREEN + ranking[rankingPos].get(0).getPoint()
						+ "P獲得" + ChatColor.RESET + "==========");

				if (rankingPos == 0) {
					GamePlayer winner = winnerPlayer;
					new BukkitRunnable() {
						ColorType[] colorTypes = madpillow.colorRoyal.ColorType.values();
						int pos = 0;

						@Override
						public void run() {
							if (pos < colorTypes.length) {
								winner.getParentTeam().getTeam().setColor(colorTypes[pos].getChatColor());
								pos++;
							} else {
								cancel();
								return;
							}
						}
					}.runTaskTimer(ColorRoyal.getPlugin(), 0L, 40L);
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 60L, 120L);
	}

	private void startTime() {
		int untilStart = 10;
		BossBar bar = Bukkit.getBossBar(NamespacedKey.minecraft("time"));
		PlayerUtils.broadcastMessage(
				TextConfig.getGameMessageText(GameMessageText.UntilStartChat, String.valueOf(untilStart)));
		for (GamePlayer gamePlayer : gamePlayerList) {
			bar.addPlayer(gamePlayer.getPlayer());
			gamePlayer.getPlayer().sendTitle(TextConfig.getGameMessageText(GameMessageText.UntilStartTitle),
					TextConfig.getGameMessageText(GameMessageText.UntilStartSubTitle, String.valueOf(untilStart)));
		}

		new BukkitRunnable() {
			int x = 0;

			@Override
			public void run() {
				if (x++ == untilStart) {
					timeTask();
					cancel();
					return;
				} else {
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (x != untilStart) {
							p.sendTitle(TextConfig.getGameMessageText(GameMessageText.UntilStartTitle),
									TextConfig.getGameMessageText(GameMessageText.UntilStartSubTitle,
											String.valueOf(untilStart - x)));
							p.playSound(p.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
						} else {
							p.sendTitle(TextConfig.getGameMessageText(GameMessageText.StartTitle),
									TextConfig.getGameMessageText(GameMessageText.StartSubTitle));
							p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						}
					}
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 20L);
	}

	private void timeTask() {
		isGameing = true;
		Bukkit.getPluginManager().registerEvents(new GameEvent(), ColorRoyal.getPlugin());
		canSeeTask();
		for (GamePlayer gamePlayer : gamePlayerList) {
			gamePlayer.remainAttackCount();
		}

		int time = ColorRoyal.getPlugin().getConfig().getInt("Time", 300);
		new BukkitRunnable() {
			int x = time;
			BossBar bar = Bukkit.getBossBar(NamespacedKey.minecraft("time"));

			@Override
			public void run() {
				if (isGameing == false) {
					cancel();
					return;
				}
				if (x-- == 0) {
					stop();
					cancel();
					return;
				}
				bar.setProgress((double) x / time);
				bar.setTitle("残り時間：" + x + "秒");

				if (x == 180 + 10) {
					allResetTask(10);
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 20L);
	}

	private void allResetTask(int interval) {
		gamePlayerList.stream().filter(player -> player.getPlayer().isOnline())
				.forEach(player -> player.getPlayer().sendTitle(
						TextConfig.getGameMessageText(GameMessageText.AllResetAnnounceTitle),
						TextConfig.getGameMessageText(GameMessageText.AllResetAnnounceSubTitle,
								String.valueOf(interval))));
		PlayerUtils.broadcastMessage(TextConfig.getGameMessageText(GameMessageText.AllResetAnnounceChat,
				String.valueOf(interval)));
		new BukkitRunnable() {
			int tempInterval = interval - 1;

			@Override
			public void run() {
				if (tempInterval == 0) {
					gamePlayerList.stream().filter(player -> player.getPlayer().isOnline())
							.forEach(player -> {
								player.getPlayer()
										.sendTitle(TextConfig.getGameMessageText(GameMessageText.AllResetTitle), "");
								player.changeTeam(player.getParentTeam());
								player.sendArmor(player.getNowTeam());
							});
					PlayerUtils.broadcastMessage(
							TextConfig.getGameMessageText(GameMessageText.AllResetChat));

					cancel();
					return;
				}

				gamePlayerList.stream().filter(player -> player.getPlayer().isOnline())
						.forEach(player -> player.getPlayer().sendTitle(
								TextConfig.getGameMessageText(GameMessageText.AllResetIntervalTitle),
								TextConfig.getGameMessageText(GameMessageText.AllResetIntervalSubTitle,
										String.valueOf(tempInterval))));
				PlayerUtils.broadcastMessage(TextConfig.getGameMessageText(GameMessageText.AllResetIntervalChat,
						String.valueOf(tempInterval)));
				tempInterval--;
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 20L, 20L);
	}

	private void canSeeTask() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!isGameing) {
					for (GamePlayer gamePlayer : gamePlayerList) {
						for (GamePlayer entityGamePlayer : gamePlayerList) {
							if (gamePlayer == entityGamePlayer) {
								continue;
							}

							gamePlayer.getPlayer().showPlayer(ColorRoyal.getPlugin(), entityGamePlayer.getPlayer());
						}
					}
					cancel();
					return;
				}

				for (GamePlayer gamePlayer : gamePlayerList) {
					for (GamePlayer entityGamePlayer : gamePlayerList) {
						if (gamePlayer == entityGamePlayer) {
							continue;
						}

						if (entityGamePlayer.getPlayer().hasPotionEffect(PotionEffectType.GLOWING)
								|| PlayerUtils.canSee(gamePlayer.getPlayer(), entityGamePlayer.getPlayer())) {
							gamePlayer.getPlayer().showPlayer(ColorRoyal.getPlugin(), entityGamePlayer.getPlayer());
						} else {
							gamePlayer.getPlayer().hidePlayer(ColorRoyal.getPlugin(), entityGamePlayer.getPlayer());
						}
					}
				}
			}
		}.runTaskTimer(ColorRoyal.getPlugin(), 0L, 1L);
	}
}
