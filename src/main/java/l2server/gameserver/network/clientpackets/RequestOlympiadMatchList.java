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

import l2server.gameserver.handler.BypassHandler;
import l2server.gameserver.handler.IBypassHandler;
import l2server.gameserver.model.actor.instance.Player;

/**
 * format ch
 * c: (id) 0xD0
 * h: (subid) 0x13
 *
 * @author -Wooden-
 */
public final class RequestOlympiadMatchList extends L2GameClientPacket {
	private static final String COMMAND = "arenalist";
	
	@Override
	protected void readImpl() {
		// trigger packet
	}
	
	@Override
	protected void runImpl() {
		final Player activeChar = getClient().getActiveChar();
		if (activeChar == null || !activeChar.inObserverMode()) {
			return;
		}
		
		IBypassHandler handler = BypassHandler.getInstance().getBypassHandler(COMMAND);
		if (handler != null) {
			handler.useBypass(COMMAND, activeChar, null);
		}
	}
}
