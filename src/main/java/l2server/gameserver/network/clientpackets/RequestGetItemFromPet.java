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

import l2server.Config;
import l2server.gameserver.model.actor.instance.PetInstance;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.util.Util;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public final class RequestGetItemFromPet extends L2GameClientPacket {
	
	private int objectId;
	private long amount;
	@SuppressWarnings("unused")
	private int unknown;
	
	@Override
	protected void readImpl() {
		objectId = readD();
		amount = readQ();
		unknown = readD();// = 0 for most trades
	}
	
	@Override
	protected void runImpl() {
		Player player = getClient().getActiveChar();
		if (player == null) {
			return;
		}
		
		if (!getClient().getFloodProtectors().getTransaction().tryPerformAction("getfrompet")) {
			player.sendMessage("You get items from pet too fast.");
			return;
		}
		
		PetInstance pet = player.getPet();
		if (player.getActiveEnchantItem() != null) {
			return;
		}
		if (amount < 0) {
			Util.handleIllegalPlayerAction(player,
					"[RequestGetItemFromPet] Character " + player.getName() + " of account " + player.getAccountName() +
							" tried to get item with oid " + objectId + " from pet but has count < 0!",
					Config.DEFAULT_PUNISH);
			return;
		} else if (amount == 0) {
			return;
		}
		
		if (pet.transferItem("Transfer", objectId, amount, player.getInventory(), player, pet) == null) {
			log.warn("Invalid item transfer request: " + pet.getName() + " (pet) --> " + player.getName());
		}
	}
}
