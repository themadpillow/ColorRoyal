package madpillow.colorRoyal.skills;

import madpillow.colorRoyal.NPCUtils;
import madpillow.colorRoyal.game.GamePlayer;
import net.citizensnpcs.api.npc.NPC;

public class Decoy extends Skill {
	private NPC decoy;
	private final int charge = SkillType.Decoy.getCharge(20);
	private final float speed = SkillType.Decoy.getSpeed(1.5F);

	public Decoy(GamePlayer gamePlayer) {
		super(gamePlayer);
	}

	@Override
	protected boolean actionSkill() {
		if (decoy == null) {
			decoy = NPCUtils.create(gamePlayer);
			decoy.getNavigator().getDefaultParameters().speedModifier(speed);
			return true;
		} else if (!decoy.isSpawned()) {
			gamePlayer.getPlayer().sendMessage("デコイが破壊されて" + charge + "秒間は再起動できません");
			return false;
		} else {
			gamePlayer.getPlayer().sendMessage("既にデコイは起動しています");
			return false;
		}
	}

	public void destroy() {
		NPCUtils.destroy(decoy);
		decoy = null;
	}

	public boolean isNull() {
		return decoy == null;
	}
}
