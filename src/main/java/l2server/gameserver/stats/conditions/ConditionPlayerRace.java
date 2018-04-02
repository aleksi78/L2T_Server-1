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

package l2server.gameserver.stats.conditions;

import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.model.base.Race;
import l2server.gameserver.stats.Env;
import l2server.gameserver.util.Util;

/**
 * The Class ConditionPlayerRace.
 *
 * @author mkizub
 */
public class ConditionPlayerRace extends Condition {

	private final Race[] races;

	/**
	 * Instantiates a new condition player race.
	 *
	 * @param races the list containing the allowed races.
	 */
	public ConditionPlayerRace(Race[] races) {
		this.races = races;
	}

	@Override
	public boolean testImpl(Env env) {
		if (!(env.player instanceof Player)) {
			return false;
		}

		return Util.contains(races, env.player.getActingPlayer().getRace());
	}
}
