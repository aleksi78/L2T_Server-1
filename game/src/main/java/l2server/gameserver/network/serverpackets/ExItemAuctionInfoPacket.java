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

import l2server.gameserver.model.ItemInfo;
import l2server.gameserver.model.itemauction.ItemAuction;
import l2server.gameserver.model.itemauction.ItemAuctionBid;
import l2server.gameserver.model.itemauction.ItemAuctionState;

/**
 * @author Forsaiken
 * Format: (cdqd)(dddqhhhdhhdddhhhhhhhhhhh)(ddd)(dddqhhhdhhdddhhhhhhhhhhh)
 */
public final class ExItemAuctionInfoPacket extends L2GameServerPacket {
	private final boolean refresh;
	private final int timeRemaining;
	private final ItemAuction currentAuction;
	private final ItemAuction nextAuction;
	
	public ExItemAuctionInfoPacket(final boolean refresh, final ItemAuction currentAuction, final ItemAuction nextAuction) {
		if (currentAuction == null) {
			throw new NullPointerException();
		}
		
		if (currentAuction.getAuctionState() != ItemAuctionState.STARTED) {
			timeRemaining = 0;
		} else {
			timeRemaining = (int) (currentAuction.getFinishingTimeRemaining() / 1000); // in seconds
		}
		
		this.refresh = refresh;
		this.currentAuction = currentAuction;
		this.nextAuction = nextAuction;
	}
	
	@Override
	protected final void writeImpl() {
		writeC(refresh ? 0x00 : 0x01);
		writeD(currentAuction.getInstanceId());
		
		final ItemAuctionBid highestBid = currentAuction.getHighestBid();
		writeQ(highestBid != null ? highestBid.getLastBid() : currentAuction.getAuctionInitBid());
		
		writeD(timeRemaining);
		writeItemInfo(currentAuction.getItemInfo());
		
		if (nextAuction != null) {
			writeQ(nextAuction.getAuctionInitBid());
			writeD((int) (nextAuction.getStartingTime() / 1000)); // unix time in seconds
			writeItemInfo(nextAuction.getItemInfo());
		}
	}
	
	private void writeItemInfo(final ItemInfo item) {
		writeD(item.getItem().getItemId());
		writeD(item.getItem().getItemId());
		writeD(item.getLocationSlot());
		writeQ(item.getCount());
		writeH(item.getItem().getType2());
		writeH(item.getCustomType1());
		writeH(0x00); //Equipped ? ON AUCTION?
		writeD(item.getItem().getBodyPart());
		writeH(item.getEnchantLevel());
		writeH(item.getCustomType2());
		writeQ(item.getAugmentationBonus());
		writeD(item.getMana());
		writeD(item.getRemainingTime());
		writeH(0x01); //God
		
		writeH(item.getAttackElementType());
		writeH(item.getAttackElementPower());
		for (byte i = 0; i < 6; i++) {
			super.writeH(item.getElementDefAttr(i));
		}
		
		writeD(0x00); // enchant effect 1
		writeD(0x00); // enchant effect 2
		writeD(0x00); // enchant effect 3
		
		writeD(item.getAppearance());
	}
}
