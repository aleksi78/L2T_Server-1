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

import l2server.gameserver.model.L2ShortCut;

/**
 * sample
 * <p>
 * 56
 * 01000000 04000000 dd9fb640 01000000
 * <p>
 * 56
 * 02000000 07000000 38000000 03000000 01000000
 * <p>
 * 56
 * 03000000 00000000 02000000 01000000
 * <p>
 * format   dd d/dd/d d
 *
 * @version $Revision: 1.3.2.1.2.3 $ $Date: 2005/03/27 15:29:39 $
 */
public final class ShortCutRegister extends L2GameServerPacket {
	
	private L2ShortCut shortcut;
	
	/**
	 * Register new skill shortcut
	 */
	public ShortCutRegister(L2ShortCut shortcut) {
		this.shortcut = shortcut;
	}
	
	@Override
	protected final void writeImpl() {
		writeD(shortcut.getType());
		writeD(shortcut.getSlot() + shortcut.getPage() * 12); // C4 Client
		switch (shortcut.getType()) {
			case L2ShortCut.TYPE_ITEM: //1
				writeD(shortcut.getId());
				writeD(shortcut.getCharacterType());
				writeD(shortcut.getSharedReuseGroup());
				writeD(0x00); // unknown
				writeD(0x00); // unknown
				writeQ(0x00); // item augment id
				break;
			case L2ShortCut.TYPE_SKILL: //2
				writeD(shortcut.getId());
				writeD(shortcut.getLevel());
				writeD(shortcut.getSharedReuseGroup());
				writeC(0x00); // C5
				writeD(shortcut.getCharacterType());
				break;
			/* these are same as default case, no need to duplicate, enable if packet get changed
			 */
			/*	case L2ShortCut.TYPE_ACTION: //3
			 *		writeD(shortcut.getId());
			 *		writeD(shortcut.getUserCommand());
			 *		break;
			 *	case L2ShortCut.TYPE_MACRO: //4
			 *		writeD(shortcut.getId());
			 *		writeD(shortcut.getUserCommand());
			 *		break;
			 *	case L2ShortCut.TYPE_RECIPE: //5
			 *		writeD(shortcut.getId());
			 *		writeD(shortcut.getUserCommand());
			 *		break;
			 */
			default: {
				writeD(shortcut.getId());
				writeD(shortcut.getCharacterType());
			}
		}
	}
}