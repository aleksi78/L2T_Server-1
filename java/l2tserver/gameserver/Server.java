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

package l2tserver.gameserver;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.swing.UIManager;

import l2tserver.Config;
import l2tserver.L2DatabaseFactory;
import l2tserver.ServerMode;
import l2tserver.gameserver.cache.CrestCache;
import l2tserver.gameserver.cache.HtmCache;
import l2tserver.gameserver.communitybbs.Manager.CustomCommunityBoard;
import l2tserver.gameserver.datatables.AbilityTable;
import l2tserver.gameserver.datatables.AccessLevels;
import l2tserver.gameserver.datatables.AdminCommandAccessRights;
import l2tserver.gameserver.datatables.ArmorSetsTable;
import l2tserver.gameserver.datatables.AugmentationData;
import l2tserver.gameserver.datatables.BeautyTable;
import l2tserver.gameserver.datatables.CharNameTable;
import l2tserver.gameserver.datatables.CharTemplateTable;
import l2tserver.gameserver.datatables.ClanTable;
import l2tserver.gameserver.datatables.ComboSkillTable;
import l2tserver.gameserver.datatables.CompoundTable;
import l2tserver.gameserver.datatables.CoreMessageTable;
import l2tserver.gameserver.datatables.DoorTable;
import l2tserver.gameserver.datatables.EnchantCostsTable;
import l2tserver.gameserver.datatables.EnchantHPBonusData;
import l2tserver.gameserver.datatables.EventDroplist;
import l2tserver.gameserver.datatables.ExtraDropTable;
import l2tserver.gameserver.datatables.FishTable;
import l2tserver.gameserver.datatables.FlyMoveTable;
import l2tserver.gameserver.datatables.GMSkillTable;
import l2tserver.gameserver.datatables.GlobalDropTable;
import l2tserver.gameserver.datatables.HelperBuffTable;
import l2tserver.gameserver.datatables.HennaTable;
import l2tserver.gameserver.datatables.HeroSkillTable;
import l2tserver.gameserver.datatables.ImageTable;
import l2tserver.gameserver.datatables.ItemTable;
import l2tserver.gameserver.datatables.MapRegionTable;
import l2tserver.gameserver.datatables.MerchantPriceConfigTable;
import l2tserver.gameserver.datatables.MultiSell;
import l2tserver.gameserver.datatables.NobleSkillTable;
import l2tserver.gameserver.datatables.NpcTable;
import l2tserver.gameserver.datatables.NpcWalkersTable;
import l2tserver.gameserver.datatables.OfflineTradersTable;
import l2tserver.gameserver.datatables.PetDataTable;
import l2tserver.gameserver.datatables.PlayerStatDataTable;
import l2tserver.gameserver.datatables.PledgeSkillTree;
import l2tserver.gameserver.datatables.ResidentialSkillTable;
import l2tserver.gameserver.datatables.ScenePlayerDataTable;
import l2tserver.gameserver.datatables.ShuttleTable;
import l2tserver.gameserver.datatables.SkillTable;
import l2tserver.gameserver.datatables.SkillTreeTable;
import l2tserver.gameserver.datatables.SpawnTable;
import l2tserver.gameserver.datatables.StaticObjects;
import l2tserver.gameserver.datatables.SubPledgeSkillTree;
import l2tserver.gameserver.datatables.SummonItemsData;
import l2tserver.gameserver.datatables.TeleportLocationTable;
import l2tserver.gameserver.datatables.UITable;
import l2tserver.gameserver.events.DMGManager;
import l2tserver.gameserver.events.HiddenChests;
import l2tserver.gameserver.events.LotterySystem;
import l2tserver.gameserver.events.RankingKillInfo;
import l2tserver.gameserver.events.instanced.EventsManager;
import l2tserver.gameserver.gui.ServerGui;
import l2tserver.gameserver.handler.AdminCommandHandler;
import l2tserver.gameserver.handler.ChatHandler;
import l2tserver.gameserver.handler.ItemHandler;
import l2tserver.gameserver.handler.SkillHandler;
import l2tserver.gameserver.handler.UserCommandHandler;
import l2tserver.gameserver.handler.VoicedCommandHandler;
import l2tserver.gameserver.idfactory.IdFactory;
import l2tserver.gameserver.instancemanager.AirShipManager;
import l2tserver.gameserver.instancemanager.AntiBotsManager;
import l2tserver.gameserver.instancemanager.ArtificialPlayersManager;
import l2tserver.gameserver.instancemanager.BoatManager;
import l2tserver.gameserver.instancemanager.CastleManager;
import l2tserver.gameserver.instancemanager.CastleManorManager;
import l2tserver.gameserver.instancemanager.ClanHallAuctionManager;
import l2tserver.gameserver.instancemanager.ClanHallManager;
import l2tserver.gameserver.instancemanager.ClanRecruitManager;
import l2tserver.gameserver.instancemanager.CoupleManager;
import l2tserver.gameserver.instancemanager.CursedWeaponsManager;
import l2tserver.gameserver.instancemanager.CustomAuctionManager;
import l2tserver.gameserver.instancemanager.CustomOfflineBuffersManager;
import l2tserver.gameserver.instancemanager.CustomWorldAltars;
import l2tserver.gameserver.instancemanager.DayNightSpawnManager;
import l2tserver.gameserver.instancemanager.FarmZoneManager;
import l2tserver.gameserver.instancemanager.FortManager;
import l2tserver.gameserver.instancemanager.FortSiegeManager;
import l2tserver.gameserver.instancemanager.FourSepulchersManager;
import l2tserver.gameserver.instancemanager.GMEventManager;
import l2tserver.gameserver.instancemanager.GlobalVariablesManager;
import l2tserver.gameserver.instancemanager.GraciaSeedsManager;
import l2tserver.gameserver.instancemanager.GrandBossManager;
import l2tserver.gameserver.instancemanager.InstanceManager;
import l2tserver.gameserver.instancemanager.ItemAuctionManager;
import l2tserver.gameserver.instancemanager.ItemsOnGroundManager;
import l2tserver.gameserver.instancemanager.MailManager;
import l2tserver.gameserver.instancemanager.MentorManager;
import l2tserver.gameserver.instancemanager.MercTicketManager;
import l2tserver.gameserver.instancemanager.OfflineAdminCommandsManager;
import l2tserver.gameserver.instancemanager.PartySearchManager;
import l2tserver.gameserver.instancemanager.PetitionManager;
import l2tserver.gameserver.instancemanager.QuestManager;
import l2tserver.gameserver.instancemanager.RaidBossPointsManager;
import l2tserver.gameserver.instancemanager.SiegeManager;
import l2tserver.gameserver.instancemanager.TransformationManager;
import l2tserver.gameserver.instancemanager.ZoneManager;
import l2tserver.gameserver.model.AutoChatHandler;
import l2tserver.gameserver.model.L2Manor;
import l2tserver.gameserver.model.L2World;
import l2tserver.gameserver.model.PartyMatchRoomList;
import l2tserver.gameserver.model.PartyMatchWaitingList;
import l2tserver.gameserver.model.entity.ClanWarManager;
import l2tserver.gameserver.model.olympiad.HeroesManager;
import l2tserver.gameserver.model.olympiad.Olympiad;
import l2tserver.gameserver.network.L2GameClient;
import l2tserver.gameserver.network.L2GamePacketHandler;
import l2tserver.gameserver.pathfinding.PathFinding;
import l2tserver.gameserver.scripting.CompiledScriptCache;
import l2tserver.gameserver.scripting.L2ScriptEngineManager;
import l2tserver.gameserver.taskmanager.AutoAnnounceTaskManager;
import l2tserver.gameserver.taskmanager.KnownListUpdateTaskManager;
import l2tserver.gameserver.taskmanager.TaskManager;
import l2tserver.log.Log;
import l2tserver.network.Core;
import l2tserver.network.CoreConfig;
import l2tserver.util.DeadLockDetector;
import l2tserver.util.IPv4Filter;

/**
 * This class ...
 * 
 * @version $Revision: 1.29.2.15.2.19 $ $Date: 2005/04/05 19:41:23 $
 */
public class Server
{
	private final Core<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	private final IdFactory _idFactory;
	public static Server gameServer;
	public static ServerGui gui;
	private LoginServerThread _loginThread;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576; // ;
	}
	
	public Core<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public L2GamePacketHandler getL2GamePacketHandler()
	{
		return _gamePacketHandler;
	}
	
	public DeadLockDetector getDeadLockDetectorThread()
	{
		return _deadDetectThread;
	}
	
	public Server() throws Exception
	{
		long serverLoadStart = System.currentTimeMillis();
		
		gameServer = this;
		Log.finest("used mem:" + getUsedMemoryMB() + "MB");
		
		_idFactory = IdFactory.getInstance();
		
		if (!_idFactory.isInitialized())
		{
			Log.severe("Could not read object IDs from DB. Please Check Your Data.");
			throw new Exception("Could not initialize the ID factory");
		}
		
		ThreadPoolManager.getInstance();
		
		new File(Config.DATAPACK_ROOT, Config.DATA_FOLDER + "crests").mkdirs();
		new File("log/game").mkdirs();
		
		// load script engines
		printSection("Engines");
		L2ScriptEngineManager.getInstance();
		
		printSection("World");
		// start game time control early
		TimeController.getInstance();
		InstanceManager.getInstance();
		L2World.getInstance();
		MapRegionTable.getInstance();
		Announcements.getInstance();
		GlobalVariablesManager.getInstance();
		
		printSection("Skills");
		EnchantCostsTable.getInstance();
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		PledgeSkillTree.getInstance();
		NobleSkillTable.getInstance();
		GMSkillTable.getInstance();
		HeroSkillTable.getInstance();
		ResidentialSkillTable.getInstance();
		SubPledgeSkillTree.getInstance();
		AbilityTable.getInstance();
		ComboSkillTable.getInstance();
		
		printSection("Items");
		ItemTable.getInstance();
		SummonItemsData.getInstance();
		EnchantHPBonusData.getInstance();
		MerchantPriceConfigTable.getInstance().load();
		TradeController.getInstance();
		MultiSell.getInstance();
		RecipeController.getInstance();
		ArmorSetsTable.getInstance();
		FishTable.getInstance();
		
		printSection("Characters");
		CharTemplateTable.getInstance();
		PlayerStatDataTable.getInstance();
		CharNameTable.getInstance();
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		GmListTable.getInstance();
		RaidBossPointsManager.getInstance();
		PetDataTable.getInstance();
		PartySearchManager.getInstance();
		MentorManager.getInstance();
		BeautyTable.getInstance();
		ScenePlayerDataTable.getInstance();
		CompoundTable.getInstance();
		
		printSection("Clans");
		ClanTable.getInstance();
		ClanHallAuctionManager.getInstance();
		ClanHallManager.getInstance();
		ClanWarManager.getInstance();
		ClanRecruitManager.getInstance();

		printSection("Auction");
		//AuctionManager.getInstance();
		
		printSection("Geodata");
		GeoData.getInstance();
		if (Config.GEODATA == 2)
			PathFinding.getInstance();
		
		printSection("NPCs");
		CastleManager.getInstance().load();
		ExtraDropTable.getInstance();
		NpcTable.getInstance();
		GrandBossManager.getInstance();
		FarmZoneManager.getInstance();
		ZoneManager.getInstance();
		FlyMoveTable.getInstance();
		DoorTable.getInstance();
		StaticObjects.getInstance();
		ItemAuctionManager.getInstance();
		FortManager.getInstance();
		SpawnTable.getInstance();
		NpcWalkersTable.getInstance();
		GraciaSeedsManager.getInstance();
		DayNightSpawnManager.getInstance().notifyChangeMode();
		CastleManager.getInstance().spawnCastleTendencyNPCs();
		GrandBossManager.getInstance().initZones();
		FourSepulchersManager.getInstance().init();
		EventDroplist.getInstance();
		
		printSection("Siege");
		SiegeManager.getInstance().getSieges();
		FortSiegeManager.getInstance();
		CastleManorManager.getInstance();
		MercTicketManager.getInstance();
		L2Manor.getInstance();
		
		printSection("Olympiad");
		Olympiad.getInstance();
		HeroesManager.getInstance();
		
		// Call to load caches
		printSection("Cache");
		HtmCache.getInstance();
		CrestCache.getInstance();
		TeleportLocationTable.getInstance();
		UITable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		PetitionManager.getInstance();
		HennaTable.getInstance();
		HelperBuffTable.getInstance();
		AugmentationData.getInstance();
		CursedWeaponsManager.getInstance();
		CoreMessageTable.getInstance();
		ImageTable.getInstance();
		
		printSection("Scripts");
		QuestManager.getInstance();
		TransformationManager.getInstance();
		BoatManager.getInstance();
		AirShipManager.getInstance();
		ShuttleTable.getInstance();
		
		try
		{
			Log.info("Loading Server Scripts");
			File scripts = new File(Config.DATAPACK_ROOT + "/" + Config.DATA_FOLDER + "scripts.cfg");
			if (!Config.ALT_DEV_NO_HANDLERS || !Config.ALT_DEV_NO_QUESTS)
				L2ScriptEngineManager.getInstance().executeScriptList(scripts);
		}
		catch (IOException ioe)
		{
			Log.severe("Failed loading scripts.cfg, no script going to be loaded");
		}
		try
		{
			CompiledScriptCache compiledScriptCache = L2ScriptEngineManager.getInstance().getCompiledScriptCache();
			if (compiledScriptCache == null)
			{
				Log.info("Compiled Scripts Cache is disabled.");
			}
			else
			{
				compiledScriptCache.purge();
				
				if (compiledScriptCache.isModified())
				{
					compiledScriptCache.save();
					Log.info("Compiled Scripts Cache was saved.");
				}
				else
				{
					Log.info("Compiled Scripts Cache is up-to-date.");
				}
			}
			
		}
		catch (IOException e)
		{
			Log.log(Level.SEVERE, "Failed to store Compiled Scripts Cache.", e);
		}
		QuestManager.getInstance().report();
		TransformationManager.getInstance().report();
		
		if (Config.SAVE_DROPPED_ITEM)
			ItemsOnGroundManager.getInstance();
		
		if (Config.AUTODESTROY_ITEM_AFTER * 1000 > 0 || Config.HERB_AUTO_DESTROY_TIME * 1000 > 0)
			ItemsAutoDestroy.getInstance();
		
		MonsterRace.getInstance();
		
		AutoChatHandler.getInstance();
		
		Log.info("AutoChatHandler: Loaded " + AutoChatHandler.getInstance().size() + " handlers in total.");
		
		AdminCommandHandler.getInstance();
		ChatHandler.getInstance();
		ItemHandler.getInstance();
		SkillHandler.getInstance();
		UserCommandHandler.getInstance();
		VoicedCommandHandler.getInstance();
		
		if (Config.L2JMOD_ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		printSection("Others");
		TaskManager.getInstance();

		MerchantPriceConfigTable.getInstance().updateReferences();
		CastleManager.getInstance().activateInstances();
		FortManager.getInstance().activateInstances();
		
		if (Config.ALLOW_MAIL)
			MailManager.getInstance();

		OfflineAdminCommandsManager.getInstance();
		GlobalDropTable.getInstance();
		EventsManager.getInstance().start();
		
		if (Config.isServer(Config.TENKAI))
		{
			HiddenChests.getInstance().spawnChests();
			//CloneInvasion.getInstance().scheduleEventStart();
			//MonsterInvasion.getInstance().scheduleEventStart();
			//Curfew.getInstance().scheduleEventStart();
			//ChessEvent.start();
		
			//LasTravel
			CustomCommunityBoard.getInstance();
			GMEventManager.getInstance();
		}
		
		//CustomWarAreas.getInstance();
		
		if (Config.ENABLE_CUSTOM_KILL_INFO)
			RankingKillInfo.getInstance();
		
		if (Config.ENABLE_WORLD_ALTARS)
			CustomWorldAltars.getInstance();
		
		if (Config.OFFLINE_BUFFERS_ENABLE)
			CustomOfflineBuffersManager.getInstance();
		
		if (Config.ENABLE_CUSTOM_DAMAGE_MANAGER)
			DMGManager.getInstance();
		
		if (Config.ENABLE_CUSTOM_AUCTIONS)
			CustomAuctionManager.getInstance();
		
		if (Config.ENABLE_CUSTOM_LOTTERY)
			LotterySystem.getInstance();
		
		KnownListUpdateTaskManager.getInstance();
		
		if (Config.ANTI_BOTS_ENABLED)
			AntiBotsManager.getInstance();
		
		if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
			OfflineTradersTable.restoreOfflineTraders();
		
		//Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		Log.info("IdFactory: Free ObjectID's remaining: " + IdFactory.getInstance().size());
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
			_deadDetectThread = null;
		
		System.gc();
		// maxMemory is the upper limit the jvm can use, totalMemory the size of
		// the current allocation pool, freeMemory the unused memory in the
		// allocation pool
		long freeMem = (Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory()) / 1048576;
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		Log.info("GameServer Started, free memory " + freeMem + " Mb of " + totalMem + " Mb");
		Toolkit.getDefaultToolkit().beep();
		
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();
		
		final CoreConfig sc = new CoreConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		_gamePacketHandler = new L2GamePacketHandler();
		_selectorThread = new Core<L2GameClient>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				Log.log(Level.SEVERE, "WARNING: The GameServer bind address is invalid, using all avaliable IPs. Reason: " + e1.getMessage(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			Log.log(Level.SEVERE, "FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		_selectorThread.start();
		Log.info("Maximum Numbers of Connected Players: " + Config.MAXIMUM_ONLINE_USERS);
		long serverLoadEnd = System.currentTimeMillis();
		Log.info("Server Loaded in " + ((serverLoadEnd - serverLoadStart) / 1000) + " seconds");
		
		AutoAnnounceTaskManager.getInstance();
		ArtificialPlayersManager.getInstance();
		//BotsManager.getInstance();
	}
	
	public static void main(String[] args) throws Exception
	{
		ServerMode.serverMode = ServerMode.MODE_GAMESERVER;
		// Local Constants
		final String LOG_FOLDER = "log"; // Name of folder for log file
		final String LOG_NAME = "./log.cfg"; // Name of log file
		
		/*** Main ***/
		// Create log folder
		File logFolder = new File(Config.DATAPACK_ROOT, LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		InputStream is = new FileInputStream(new File(LOG_NAME));
		LogManager.getLogManager().readConfiguration(is);
		is.close();
		
		// Initialize config
		Config.load();
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		gui = new ServerGui();
		gui.init();
		
		printSection("Database");
		L2DatabaseFactory.getInstance();

		//SqlToXml.races();
		//SqlToXml.classes();
		//SqlToXml.shops();
		//SqlToXml.customShops();
		//SqlToXml.enchantSkillGroups();
		//SqlToXml.armorSets();
		//SqlToXml.henna();
		//SqlToXml.fortSpawns();
		
		gameServer = new Server();
		
		//SqlToXml.spawns();
		//SqlToXml.raidBosses();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
	}
	
	static long _t = 0;
	public static void printSection(String s)
	{
		long t = System.currentTimeMillis();
		//if (_t > 0)
		//	Log.info("Time spent in last section: " + (t - _t) / 1000 + "s");
		_t = t;
		
		s = "=[ " + s + " ]";
		while (s.length() < 78)
			s = "-" + s;
		Log.info(s);
	}
}