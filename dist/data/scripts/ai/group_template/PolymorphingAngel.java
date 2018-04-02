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

package ai.group_template;

import l2server.gameserver.model.actor.Attackable;
import l2server.gameserver.model.actor.Npc;
import l2server.gameserver.model.actor.instance.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Angel spawns...when one of the angels in the keys dies, the other angel will spawn.
 */
public class PolymorphingAngel extends L2AttackableAIScript {

	private static final Map<Integer, Integer> ANGELSPAWNS = new HashMap<Integer, Integer>();

	static {
		ANGELSPAWNS.put(20830, 20859);
		ANGELSPAWNS.put(21067, 21068);
		ANGELSPAWNS.put(21062, 21063);
		ANGELSPAWNS.put(20831, 20860);
		ANGELSPAWNS.put(21070, 21071);
	}

	public PolymorphingAngel(int questId, String name, String descr) {
		super(questId, name, descr);
		int[] temp = {20830, 21067, 21062, 20831, 21070};
		this.registerMobs(temp, QuestEventType.ON_KILL);
	}

	@Override
	public String onKill(Npc npc, Player killer, boolean isPet) {
		int npcId = npc.getNpcId();
		if (ANGELSPAWNS.containsKey(npcId)) {
			Attackable newNpc = (Attackable) this.addSpawn(ANGELSPAWNS.get(npcId), npc);
			newNpc.setRunning();
		}
		return super.onKill(npc, killer, isPet);
	}

	public static void main(String[] args) {
		// now call the constructor (starts up the ai)
		new PolymorphingAngel(-1, "polymorphing_angel", "ai");
	}
}
