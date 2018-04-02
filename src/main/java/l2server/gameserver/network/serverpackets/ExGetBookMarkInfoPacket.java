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

import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.model.actor.instance.Player.TeleportBookmark;

/**
 * @author ShanSoft
 * @Structure d dd (ddddSdS)
 */
public class ExGetBookMarkInfoPacket extends L2GameServerPacket {
	
	private Player player;
	
	public ExGetBookMarkInfoPacket(Player cha) {
		player = cha;
	}
	
	@Override
	protected final void writeImpl() {
		writeD(0x00); // Dummy
		writeD(player.getBookmarkslot());
		writeD(player.getTpbookmark().size());
		
		for (TeleportBookmark tpbm : player.getTpbookmark()) {
			writeD(tpbm.id);
			writeD(tpbm.x);
			writeD(tpbm.y);
			writeD(tpbm.z);
			writeS(tpbm.name);
			writeD(tpbm.icon);
			writeS(tpbm.tag);
		}
	}
}
