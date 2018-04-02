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

package handlers.itemhandlers;

import l2server.Config;
import l2server.gameserver.model.Item;
import l2server.gameserver.model.actor.Playable;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.ActionFailed;
import l2server.gameserver.network.serverpackets.SystemMessage;

/**
 * Item skills not allowed on olympiad
 */
public class ItemSkills extends ItemSkillsTemplate {
	/**
	 * @see l2server.gameserver.handler.IItemHandler#useItem(Playable, Item, boolean)
	 */
	@Override
	public void useItem(Playable playable, Item item, boolean forceUse) {
		final Player activeChar = playable.getActingPlayer();
		if (activeChar != null && activeChar.isInOlympiadMode() && item.getItemId() != 5589) // Momentum Stone
		{
			activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT));
			return;
		}

		if (activeChar.getEvent() != null && !activeChar.getEvent().onScrollUse(activeChar.getObjectId())) {
			playable.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		//Don't allow the use of blessed scroll of escape
		if (Config.isServer(Config.TENKAI) && item.getName().contains("Blessed Scroll of Escape") && activeChar.getPvpFlag() > 0) {
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		super.useItem(playable, item, forceUse);
	}
}
