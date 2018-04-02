package handlers.admincommandhandlers;

import l2server.Config;
import l2server.gameserver.handler.IAdminCommandHandler;
import l2server.gameserver.model.Abnormal;
import l2server.gameserver.model.World;
import l2server.gameserver.model.actor.Creature;
import l2server.gameserver.model.actor.instance.Player;
import l2server.gameserver.network.SystemMessageId;
import l2server.gameserver.network.serverpackets.NpcHtmlMessage;
import l2server.gameserver.util.GMAudit;
import l2server.util.StringUtil;

import java.util.StringTokenizer;

public class AdminBuffs implements IAdminCommandHandler {
	private static final int PAGE_LIMIT = 20;

	private static final String[] ADMIN_COMMANDS =
			{"admin_getbuffs", "admin_stopbuff", "admin_stopallbuffs", "admin_areacancel", "admin_removereuse"};

	@Override
	public boolean useAdminCommand(String command, Player activeChar) {
		if (command.startsWith("admin_getbuffs")) {
			StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();

			if (st.hasMoreTokens()) {
				Player player = null;
				String playername = st.nextToken();

				try {
					player = World.getInstance().getPlayer(playername);
					if (player == null) {
						for (Player pl : World.getInstance().getAllPlayers().values()) {
							if (pl != null && pl.getName().equalsIgnoreCase(playername)) {
								player = pl;
								break;
							}
						}
					}
				} catch (Exception e) {
				}

				if (player != null) {
					int page = 1;
					if (st.hasMoreTokens()) {
						page = Integer.parseInt(st.nextToken());
					}
					showBuffs(activeChar, player, page);
					return true;
				} else {
					activeChar.sendMessage("The player " + playername + " is not online");
					return false;
				}
			} else if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Creature) {
				showBuffs(activeChar, (Creature) activeChar.getTarget(), 1);
				return true;
			} else {
				activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				return false;
			}
		} else if (command.startsWith("admin_stopbuff")) {
			try {
				StringTokenizer st = new StringTokenizer(command, " ");

				st.nextToken();
				int objectId = Integer.parseInt(st.nextToken());
				int skillId = Integer.parseInt(st.nextToken());

				removeBuff(activeChar, objectId, skillId);
				return true;
			} catch (Exception e) {
				activeChar.sendMessage("Failed removing effect: " + e.getMessage());
				activeChar.sendMessage("Usage: //stopbuff <objectId> <skillId>");
				return false;
			}
		} else if (command.startsWith("admin_stopallbuffs")) {
			try {
				StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				int objectId = Integer.parseInt(st.nextToken());
				removeAllBuffs(activeChar, objectId);
				return true;
			} catch (Exception e) {
				activeChar.sendMessage("Failed removing all effects: " + e.getMessage());
				activeChar.sendMessage("Usage: //stopallbuffs <objectId>");
				return false;
			}
		} else if (command.startsWith("admin_areacancel")) {
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			String val = st.nextToken();
			try {
				int radius = Integer.parseInt(val);

				for (Creature knownChar : activeChar.getKnownList().getKnownCharactersInRadius(radius)) {
					if (knownChar instanceof Player && !knownChar.equals(activeChar)) {
						knownChar.stopAllEffects();
					}
				}

				activeChar.sendMessage("All effects canceled within raidus " + radius);
				return true;
			} catch (NumberFormatException e) {
				activeChar.sendMessage("Usage: //areacancel <radius>");
				return false;
			}
		} else if (command.startsWith("admin_removereuse")) {
			StringTokenizer st = new StringTokenizer(command, " ");
			command = st.nextToken();

			Player player = null;
			if (st.hasMoreTokens()) {
				String playername = st.nextToken();

				try {
					player = World.getInstance().getPlayer(playername);
				} catch (Exception e) {
				}

				if (player == null) {
					activeChar.sendMessage("The player " + playername + " is not online.");
					return false;
				}
			} else if (activeChar.getTarget() instanceof Player) {
				player = (Player) activeChar.getTarget();
			} else {
				activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
				return false;
			}

			try {
				player.removeSkillReuse(true);
				activeChar.sendMessage("Skill reuse was removed from " + player.getName() + ".");
				return true;
			} catch (NullPointerException e) {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}

	public void showBuffs(Player activeChar, Creature target, int page) {
		final Abnormal[] effects = target.getAllEffects();

		if (page > effects.length / PAGE_LIMIT + 1 || page < 1) {
			return;
		}

		int max = effects.length / PAGE_LIMIT;
		if (effects.length > PAGE_LIMIT * max) {
			max++;
		}

		final StringBuilder html = StringUtil.startAppend(500 + effects.length * 200,
				"<html><table width=\"100%\"><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=180><center><font color=\"LEVEL\">Effects of ",
				target.getName(),
				"</font></td><td width=45><button value=\"Back\" action=\"bypass -h admin_current_player\" width=45 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table><br><table width=\"100%\"><tr><td width=200>Skill</td><td width=30>Rem. Time</td><td width=70>Action</td></tr>");

		int start = (page - 1) * PAGE_LIMIT;
		int end = Math.min((page - 1) * PAGE_LIMIT + PAGE_LIMIT, effects.length);

		for (int i = start; i < end; i++) {
			Abnormal e = effects[i];
			if (e != null) {
				StringUtil.append(html,
						"<tr><td>",
						e.getSkill().getName(),
						"</td><td>",
						e.getSkill().isToggle() ? "toggle" : e.getDuration() - e.getTime() + "s",
						"</td><td><button value=\"Remove\" action=\"bypass -h admin_stopbuff ",
						Integer.toString(target.getObjectId()),
						" ",
						String.valueOf(e.getSkill().getId()),
						"\" width=60 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
			}
		}

		html.append("</table><table width=300 bgcolor=444444><tr>");
		for (int x = 0; x < max; x++) {
			int pagenr = x + 1;
			if (page == pagenr) {
				html.append("<td>Page ");
				html.append(pagenr);
				html.append("</td>");
			} else {
				html.append("<td><a action=\"bypass -h admin_getbuffs ");
				html.append(target.getName());
				html.append(" ");
				html.append(x + 1);
				html.append("\"> Page ");
				html.append(pagenr);
				html.append(" </a></td>");
			}
		}

		html.append("</tr></table>");

		StringUtil.append(html,
				"<br><center><button value=\"Remove All\" action=\"bypass -h admin_stopallbuffs ",
				Integer.toString(target.getObjectId()),
				"\" width=80 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></html>");

		NpcHtmlMessage ms = new NpcHtmlMessage(1);
		ms.setHtml(html.toString());
		activeChar.sendPacket(ms);

		if (Config.GMAUDIT) {
			GMAudit.auditGMAction(activeChar.getName(), "getbuffs", target.getName() + " (" + Integer.toString(target.getObjectId()) + ")", "");
		}
	}

	private void removeBuff(Player activeChar, int objId, int skillId) {
		Creature target = null;
		try {
			target = (Creature) World.getInstance().findObject(objId);
		} catch (Exception e) {
		}

		if (target != null && skillId > 0) {
			Abnormal[] effects = target.getAllEffects();

			for (Abnormal e : effects) {
				if (e != null && e.getSkill().getId() == skillId) {
					e.exit();
					activeChar.sendMessage(
							"Removed " + e.getSkill().getName() + " level " + e.getSkill().getLevel() + " from " + target.getName() + " (" + objId +
									")");
				}
			}
			showBuffs(activeChar, target, 1);
			if (Config.GMAUDIT) {
				GMAudit.auditGMAction(activeChar.getName(), "stopbuff", target.getName() + " (" + objId + ")", Integer.toString(skillId));
			}
		}
	}

	private void removeAllBuffs(Player activeChar, int objId) {
		Creature target = null;
		try {
			target = (Creature) World.getInstance().findObject(objId);
		} catch (Exception e) {
		}

		if (target != null) {
			target.stopAllEffects();
			activeChar.sendMessage("Removed all effects from " + target.getName() + " (" + objId + ")");
			showBuffs(activeChar, target, 1);
			if (Config.GMAUDIT) {
				GMAudit.auditGMAction(activeChar.getName(), "stopallbuffs", target.getName() + " (" + objId + ")", "");
			}
		}
	}
}
