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

import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.network.serverpackets.ExResponseCommissionInfo;
import l2server.gameserver.network.serverpackets.ExResponseCommissionItemList;
import l2server.gameserver.network.serverpackets.ExResponseCommissionList;
import l2server.gameserver.network.serverpackets.ExResponseCommissionRegister;

/**
 * @author Erlandys
 */
public final class RequestCommissionRegister extends L2GameClientPacket {
	int itemOID;
	String itemName;
	long price;
	long count;
	int duration;
	
	@Override
	protected void readImpl() {
		itemOID = readD();
		itemName = readS();
		price = readQ();
		count = readQ();
		duration = readD();
		readQ(); // Unknown
	}
	
	@Override
	protected void runImpl() {
		Player player = getClient().getActiveChar();
		if (player == null) {
		}

		/*long destroyPrice = price;
		AuctionManager am = AuctionManager.getInstance();
		am.checkForAuctionsDeletion();
		long timeToAdd = 0;
		switch (duration)
		{
			case 0:
				timeToAdd = 86400000;
				destroyPrice *= 0.0001;
				break;
			case 1:
				timeToAdd = 259200000;
				destroyPrice *= 0.0003;
				break;
			case 2:
				timeToAdd = 432000000;
				destroyPrice *= 0.0005;
				break;
			case 3:
				timeToAdd = 604800000;
				destroyPrice *= 0.0007;
		}
		if (destroyPrice < 1000)
			destroyPrice = 1000;

		if (player.getInventory().getItemByItemId(57) == null || player.getInventory().getItemByItemId(57).getCount() < destroyPrice)
		{
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			reloadAuction(player, false);
			return;
		}

		if (player.getInventory().getItemByObjectId(itemOID) == null)
		{
			player.sendPacket(SystemMessageId.REGISTRATION_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST);
			reloadAuction(player, false);
			return;
		}

		if (player.getInventory().getItemByObjectId(itemOID).isEquipped())
		{
			player.sendPacket(SystemMessageId.THE_ITEM_THAT_IS_CURRENTLY_WORN_CANNOT_BE_REGISTERED);
			reloadAuction(player, false);
			return;
		}

		int itemID = player.getInventory().getItemByObjectId(itemOID).getItemId();
		ItemTemplate item = ItemTable.getInstance().getTemplate(itemID);

		if ((player.getAuctionInventory().getSize() >= 10 && !player.isGM()) || (player.getAuctionInventory().getSize() >= 99999 && player.isGM()) || !item.isTradeable() || !item.isSellable())
		{
			player.sendPacket(SystemMessageId.THE_ITEM_CANNOT_BE_REGISTERED_BECAUSE_REQUIREMENTS_ARE_NOT_MET);
			reloadAuction(player, false);
			return;
		}

		int category = am.getCategoryByItem(player.getInventory().getItemByObjectId(itemOID));
		player.getInventory().destroyItemByItemId("CreateAuction", 57, destroyPrice, null, null);
		player.getInventory().transferItem("CreateAuction", itemOID, count, player.getAuctionInventory(), player, null);
		long finishTime = (System.currentTimeMillis() + timeToAdd)/1000;

		int auctionID = IdFactory.getInstance().getNextId();
		if (player.getAuctionInventory().getItemByObjectId(itemOID) == null)
			am.createAuction(auctionID, player.getObjectId(), itemOID, player.getAuctionInventory().getItemByItemId(itemID), itemName, price, count, duration, finishTime, category);
		else
			am.createAuction(auctionID, player.getObjectId(), itemOID, player.getAuctionInventory().getItemByObjectId(itemOID), itemName, price, count, duration, finishTime, category);
		am.insertAuction(am.getAuctionById(auctionID));
		player.sendPacket(SystemMessageId.THE_ITEM_HAS_BEEN_SUCCESSFULLY_REGISTERED);
		InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(player.getInventory().getItemByItemId(57));
		iu.addModifiedItem(player.getInventory().getItemByObjectId(itemOID));
		player.sendPacket(iu);
		reloadAuction(player, true);*/
	}
	
	@SuppressWarnings("unused")
	private void reloadAuction(Player player, boolean success) {
		player.sendPacket(new ExResponseCommissionRegister(success));
		player.sendPacket(new ExResponseCommissionList(player));
		player.sendPacket(new ExResponseCommissionInfo(player, 0, success));
		player.sendPacket(new ExResponseCommissionItemList(player));
	}
}
