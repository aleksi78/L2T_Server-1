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

package l2server.gameserver.network.clientpackets;

import l2server.gameserver.datatables.EnchantCostsTable;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.model.Skill;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.network.serverpackets.ExEnchantSkillInfo;

/**
 * Format (ch) dd
 * c: (id) 0xD0
 * h: (subid) 0x06
 * d: skill id
 * d: skill lvl
 *
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfo extends L2GameClientPacket {
	
	private int skillId;
	private int skillLvl;
	private int skillEnchant;
	
	@Override
	protected void readImpl() {
		skillId = readD();
		skillLvl = readH();
		skillEnchant = readH();
	}
	
	/* (non-Javadoc)
	 * @see l2server.gameserver.clientpackets.ClientBasePacket#runImpl()
	 */
	@Override
	protected void runImpl() {
		if (skillId <= 0 || skillLvl <= 0) // minimal sanity check
		{
			return;
		}
		
		Player activeChar = getClient().getActiveChar();
		
		if (activeChar == null) {
			return;
		}
		
		if (activeChar.getLevel() < 76) {
			return;
		}

		/* Npc trainer = activeChar.getLastFolkNPC();
		if (!(trainer instanceof NpcInstance))
			return;

		if (!trainer.canInteract(activeChar) && !activeChar.isGM())
			return;*/
		
		Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl, skillEnchant);
		if (skill == null || skill.getId() != skillId) {
			return;
		}
		
		if (EnchantCostsTable.getInstance().getSkillEnchantmentBySkillId(skillId) == null) {
			return;
		}
		
		int playerSkillLvl = activeChar.getSkillLevelHash(skillId);
		if (playerSkillLvl == -1 || playerSkillLvl != skillLvl + (skillEnchant << 16)) {
			return;
		}
		
		activeChar.sendPacket(new ExEnchantSkillInfo(skillId, skillLvl, skillEnchant / 1000, skillEnchant % 1000));
	}
}
