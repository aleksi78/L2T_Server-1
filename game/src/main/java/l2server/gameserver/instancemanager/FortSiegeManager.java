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

package l2server.gameserver.instancemanager;

import l2server.DatabasePool;
import l2server.gameserver.datatables.SkillTable;
import l2server.gameserver.model.*;
import l2server.gameserver.model.actor.Creature;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.model.entity.Fort;
import l2server.gameserver.model.entity.FortSiege;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FortSiegeManager {
	private static Logger log = LoggerFactory.getLogger(FortSiegeManager.class.getName());

	public static FortSiegeManager getInstance() {
		return SingletonHolder.instance;
	}

	private List<FortSiege> sieges;

	public final void addSiegeSkills(Player character) {
		character.addSkill(SkillTable.FrequentSkill.IMPRINT_OF_LIGHT.getSkill(), false);
		character.addSkill(SkillTable.FrequentSkill.IMPRINT_OF_DARKNESS.getSkill(), false);
		character.addSkill(SkillTable.FrequentSkill.BUILD_HEADQUARTERS.getSkill(), false);
	}

	/**
	 * Return true if character summon<BR><BR>
	 *
	 * @param activeChar The Creature of the character can summon
	 */
	public final boolean checkIfOkToSummon(Creature activeChar, boolean isCheckOnly) {
		if (!(activeChar instanceof Player)) {
			return false;
		}

		String text = "";
		Player player = (Player) activeChar;
		Fort fort = FortManager.getInstance().getFort(player);

		if (fort == null || fort.getFortId() <= 0) {
			text = "You must be on fort ground to summon this";
		} else if (!fort.getSiege().getIsInProgress()) {
			text = "You can only summon this during a siege.";
		} else if (player.getClanId() != 0 && fort.getSiege().getAttackerClan(player.getClanId()) == null) {
			text = "You can only summon this as a registered attacker.";
		} else {
			return true;
		}

		if (!isCheckOnly) {
			player.sendMessage(text);
		}
		return false;
	}

	/**
	 * Return true if the clan is registered or owner of a fort<BR><BR>
	 *
	 * @param clan The L2Clan of the player
	 */
	public final boolean checkIsRegistered(L2Clan clan, int fortid) {
		if (clan == null) {
			return false;
		}

		Connection con = null;
		boolean register = false;
		try {
			con = DatabasePool.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM fortsiege_clans WHERE clan_id=? AND fort_id=?");
			statement.setInt(1, clan.getClanId());
			statement.setInt(2, fortid);
			ResultSet rs = statement.executeQuery();

			if (rs.next()) {
				register = true;
			}

			rs.close();
			statement.close();
		} catch (Exception e) {
			log.warn("Exception: checkIsRegistered(): " + e.getMessage(), e);
		} finally {
			DatabasePool.close(con);
		}
		return register;
	}

	public final FortSiege getSiege(WorldObject activeObject) {
		return getSiege(activeObject.getX(), activeObject.getY(), activeObject.getZ());
	}

	public final FortSiege getSiege(int x, int y, int z) {
		for (Fort fort : FortManager.getInstance().getForts()) {
			if (fort.getSiege().checkIfInZone(x, y, z)) {
				return fort.getSiege();
			}
		}
		return null;
	}

	public final List<FortSiege> getSieges() {
		if (sieges == null) {
			sieges = new ArrayList<>();
		}
		return sieges;
	}

	public final void addSiege(FortSiege fortSiege) {
		if (sieges == null) {
			sieges = new ArrayList<>();
		}
		sieges.add(fortSiege);
	}

	public boolean isCombat(int itemId) {
		return itemId == 9819;
	}

	public boolean activateCombatFlag(Player player, Item item) {
		if (!checkIfCanPickup(player)) {
			return false;
		}

		Fort fort = FortManager.getInstance().getFort(player);

		List<CombatFlag> fcf = fort.getFlags();
		for (CombatFlag cf : fcf) {
			if (cf.itemInstance == item) {
				cf.activate(player, item);
			}
		}
		return true;
	}

	public boolean checkIfCanPickup(Player player) {
		SystemMessage sm;
		sm = SystemMessage.getSystemMessage(SystemMessageId.THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED);
		sm.addItemName(9819);
		// Cannot own 2 combat flag
		if (player.isCombatFlagEquipped()) {
			player.sendPacket(sm);
			return false;
		}

		// here check if is siege is in progress
		// here check if is siege is attacker
		Fort fort = FortManager.getInstance().getFort(player);

		if (fort == null || fort.getFortId() <= 0) {
			player.sendPacket(sm);
			return false;
		} else if (!fort.getSiege().getIsInProgress()) {
			player.sendPacket(sm);
			return false;
		} else if (fort.getSiege().getAttackerClan(player.getClan()) == null) {
			player.sendPacket(sm);
			return false;
		}
		return true;
	}

	public void dropCombatFlag(Player player, int fortId) {
		Fort fort = FortManager.getInstance().getFortById(fortId);

		List<CombatFlag> fcf = fort.getFlags();
		for (CombatFlag cf : fcf) {
			if (cf.playerId == player.getObjectId()) {
				cf.dropIt();
				if (fort.getSiege().getIsInProgress()) {
					cf.spawnMe();
				}
			}
		}
	}

	public static class SiegeSpawn {
		Location location;
		private int npcId;
		private int heading;
		private int fortId;
		private int id;

		public SiegeSpawn(int fort_id, int x, int y, int z, int heading, int npc_id, int id) {
			fortId = fort_id;
			location = new Location(x, y, z, heading);
			this.heading = heading;
			npcId = npc_id;
			this.id = id;
		}

		public int getFortId() {
			return fortId;
		}

		public int getNpcId() {
			return npcId;
		}

		public int getHeading() {
			return heading;
		}

		public int getId() {
			return id;
		}

		public Location getLocation() {
			return location;
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final FortSiegeManager instance = new FortSiegeManager();
	}
}
