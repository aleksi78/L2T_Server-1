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

package handlers.bypasshandlers;

import l2server.gameserver.handler.IBypassHandler;
import l2server.gameserver.model.actor.Npc;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.StringTokenizer;

public class PlayerHelp implements IBypassHandler {
	private static final String[] COMMANDS = {"player_help"};
	
	@Override
	public boolean useBypass(String command, Player activeChar, Npc target) {
		try {
			if (command.length() < 13) {
				return false;
			}
			
			final String path = command.substring(12);
			if (path.indexOf("..") != -1) {
				return false;
			}
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			NpcHtmlMessage html;
			if (cmd.length > 1) {
				final int itemId = Integer.parseInt(cmd[1]);
				html = new NpcHtmlMessage(1, itemId);
			} else {
				html = new NpcHtmlMessage(1);
			}
			
			html.setFile(activeChar.getHtmlPrefix(), "help/" + cmd[0]);
			html.disableValidation();
			activeChar.sendPacket(html);
		} catch (Exception e) {
			log.info("Exception in " + e.getMessage(), e);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList() {
		return COMMANDS;
	}
}
