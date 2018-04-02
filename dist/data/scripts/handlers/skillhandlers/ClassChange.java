/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package handlers.skillhandlers;

import l2server.gameserver.events.instanced.EventsManager;
import l2server.gameserver.handler.ISkillHandler;
import l2server.gameserver.instancemanager.GrandBossManager;
import l2server.gameserver.model.WorldObject;
import l2server.gameserver.model.Skill;
import l2server.gameserver.model.actor.Creature;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.ExSubjobInfo;
import l2server.gameserver.network.serverpackets.SystemMessage;
import l2server.gameserver.templates.skills.SkillType;

/**
 * This class ...
 *
 * @version $Revision: 1.1.2.5.2.4 $ $Date: 2005/04/03 15:55:03 $
 */

public class ClassChange implements ISkillHandler {
	private static final SkillType[] SKILL_IDS = {SkillType.CLASS_CHANGE};

	/**
	 * @see l2server.gameserver.handler.ISkillHandler#useSkill(Creature, Skill, WorldObject[])
	 */
	@Override
	public void useSkill(Creature activeChar, Skill skill, WorldObject[] targets) {
		if (!(activeChar instanceof Player)) {
			return;
		}

		Player player = (Player) activeChar;

		if (player.isInCombat() || player.getPvpFlag() > 0) // Cannot switch or change subclasses in combat
		{
			player.sendMessage("You cannot switch your subclass while you are fighting.");
			return;
		}

		if (player.getTemporaryLevel() != 0) {
			player.sendMessage("You canno't switch a subclass while on a temporary level.");
			return;
		}

		if (player.isInOlympiadMode()) {
			player.sendMessage("You cannot switch your subclass while involved in the Grand Olympiads.");
			return;
		}

		if (EventsManager.getInstance().isPlayerParticipant(player.getObjectId()) || player.getEvent() != null) {
			player.sendMessage("You cannot switch your subclass while involved in an event.");
			return;
		}

		if (player.hasIdentityCrisis()) // Cannot switch or change subclasses while he has identity crisis
		{
			player.sendMessage("You cannot switch your subclass while Identity crisis id in progress.");
			return;
		}

		if (player.getInstanceId() != 0 || GrandBossManager.getInstance().checkIfInZone(player)) {
			player.sendMessage("You cannot switch your subclass in this situation!");
			return;
		}

		if (!player.getFloodProtectors().getSubclass().tryPerformAction("change subclass")) {
			log.warn("Player " + player.getName() + " has performed a subclass change too fast");
			return;
		}

		int classIndex = skill.getId() - 1566;

		if (!player.setActiveClass(classIndex)) {
			player.sendMessage("You cannot switch your class right now!.");
			return;
		}

		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED)); // Transfer completed.
		player.sendPacket(new ExSubjobInfo(player));
	}

	/**
	 * @see l2server.gameserver.handler.ISkillHandler#getSkillIds()
	 */
	@Override
	public SkillType[] getSkillIds() {
		return SKILL_IDS;
	}
}
