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

package l2server.gameserver.network.serverpackets;

import l2server.gameserver.model.actor.Npc;
import l2server.gameserver.model.actor.instance.Player;

/**
 * @author KenM
 */
public class ExSpawnEmitter extends L2GameServerPacket {
	private final int playerObjectId;
	private final int npcObjectId;
	
	public ExSpawnEmitter(int playerObjectId, int npcObjectId) {
		this.playerObjectId = playerObjectId;
		this.npcObjectId = npcObjectId;
	}
	
	public ExSpawnEmitter(Player player, Npc npc) {
		this(player.getObjectId(), npc.getObjectId());
	}

    /*
	  @see l2server.gameserver.network.serverpackets.L2GameServerPacket#getType()
     */
	
	/**
	 * @see l2server.gameserver.network.serverpackets.L2GameServerPacket#writeImpl()
	 */
	@Override
	protected final void writeImpl() {
		writeD(npcObjectId);
		writeD(playerObjectId);
		writeD(0x00); // ?
	}
}
