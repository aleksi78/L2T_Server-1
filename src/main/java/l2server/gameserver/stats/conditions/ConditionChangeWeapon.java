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
import l2server.gameserver.stats.Env;
import l2server.gameserver.templates.item.WeaponTemplate;

/**
 * The Class ConditionChangeWeapon.
 *
 * @author nBd
 */
public class ConditionChangeWeapon extends Condition {
	private final boolean required;

	/**
	 * Instantiates a new condition change weapon.
	 *
	 * @param required the required
	 */
	public ConditionChangeWeapon(boolean required) {
		this.required = required;
	}

	/**
	 * Test impl.
	 *
	 * @param env the env
	 * @return true, if successful
	 * @see l2server.gameserver.stats.conditions.Condition#testImpl(l2server.gameserver.stats.Env)
	 */
	@Override
	boolean testImpl(Env env) {
		if (!(env.player instanceof Player)) {
			return false;
		}

		if (required) {
			WeaponTemplate weaponItem = env.player.getActiveWeaponItem();

			if (weaponItem == null) {
				return false;
			}

			if (weaponItem.getChangeWeaponId() == 0) {
				return false;
			}

			if (((Player) env.player).isEnchanting()) {
				return false;
			}
		}
		return true;
	}
}
