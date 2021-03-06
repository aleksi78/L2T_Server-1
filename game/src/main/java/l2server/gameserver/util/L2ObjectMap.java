/*
 * $Header: L2ObjectMap.java, 22/07/2005 13:17:51 luisantonioa Exp $
 *
 * $Author: luisantonioa $
 * $Date: 22/07/2005 13:17:51 $
 * $Revision: 1 $
 * $Log: L2ObjectMap.java,v $
 * Revision 1  22/07/2005 13:17:51  luisantonioa
 * Added copyright notice
 *
 *
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

package l2server.gameserver.util;

import l2server.Config;
import l2server.gameserver.model.WorldObject;

import java.util.Iterator;

/**
 * This class ...
 *
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */

public abstract class L2ObjectMap<T extends WorldObject> implements Iterable<T> {

	public abstract int size();

	public abstract boolean isEmpty();

	public abstract void clear();

	public abstract void put(T obj);

	public abstract void remove(T obj);

	public abstract T get(int id);

	public abstract boolean contains(T obj);

	@Override
	public abstract Iterator<T> iterator();

	public static L2ObjectMap<WorldObject> createL2ObjectMap() {
		switch (Config.MAP_TYPE) {
			case WorldObjectMap:
				return new WorldObjectMap<>();
			default:
				return new WorldObjectTree<>();
		}
	}
}
