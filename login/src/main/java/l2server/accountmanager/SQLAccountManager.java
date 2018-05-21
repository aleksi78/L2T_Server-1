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

package l2server.accountmanager;

import l2server.Base64;
import l2server.Config;
import l2server.DatabasePool;
import l2server.ServerMode;
import l2server.util.crypt.PasswordCrypt;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class SQL Account Manager
 *
 * @author netimperia
 * @version $Revision: 2.3.2.1.2.3 $ $Date: 2005/08/08 22:47:12 $
 */
public class SQLAccountManager {
	private static String uname = "";
	private static String pass = "";
	private static String level = "";
	private static String mode = "";
	
	public static void main(String[] args) throws SQLException, IOException, NoSuchAlgorithmException {
		ServerMode.serverMode = ServerMode.MODE_LOGINSERVER;
		Config.load();
		while (true) {
			System.out.println("Please choose an option:");
			System.out.println("");
			System.out.println("1 - Create new account or update existing one (change pass and access level).");
			System.out.println("2 - Change access level.");
			System.out.println("3 - Delete existing account.");
			System.out.println("4 - List accounts & access levels.");
			System.out.println("5 - Exit.");
			LineNumberReader in = new LineNumberReader(new InputStreamReader(System.in));
			while (!(mode.equals("1") || mode.equals("2") || mode.equals("3") || mode.equals("4") || mode.equals("5"))) {
				System.out.print("Your choice: ");
				mode = in.readLine();
			}
			
			if (mode.equals("1") || mode.equals("2") || mode.equals("3")) {
				if (mode.equals("1") || mode.equals("2")) {
					while (uname.trim().length() == 0) {
						System.out.print("Username: ");
						uname = in.readLine().toLowerCase();
					}
				} else if (mode.equals("3")) {
					while (uname.trim().length() == 0) {
						System.out.print("Account name: ");
						uname = in.readLine().toLowerCase();
					}
				}
				if (mode.equals("1")) {
					while (pass.trim().length() == 0) {
						System.out.print("Password: ");
						pass = in.readLine();
					}
				}
				if (mode.equals("1") || mode.equals("2")) {
					while (level.trim().length() == 0) {
						System.out.print("Access level: ");
						level = in.readLine();
					}
				}
			}
			
			switch (mode) {
				case "1":
					// Add or Update
					addOrUpdateAccount(uname.trim(), pass.trim(), level.trim());
					break;
				case "2":
					// Change Level
					changeAccountLevel(uname.trim(), level.trim());
					break;
				case "3":
					// Delete
					System.out.print("Do you really want to delete this account ? Y/N : ");
					String yesno = in.readLine();
					if (yesno.equalsIgnoreCase("Y")) {
						deleteAccount(uname.trim());
					} else {
						System.out.println("Deletion cancelled");
					}
					break;
				case "4":
					// List
					mode = "";
					System.out.println("");
					System.out.println("Please choose a listing mode:");
					System.out.println("");
					System.out.println("1 - Banned accounts only (accessLevel < 0)");
					System.out.println("2 - GM/privileged accounts (accessLevel > 0)");
					System.out.println("3 - Regular accounts only (accessLevel = 0)");
					System.out.println("4 - List all");
					while (!(mode.equals("1") || mode.equals("2") || mode.equals("3") || mode.equals("4"))) {
						System.out.print("Your choice: ");
						mode = in.readLine();
					}
					System.out.println("");
					printAccInfo(mode);
					break;
				case "5":
					System.exit(0);
			}
			
			uname = "";
			pass = "";
			level = "";
			mode = "";
			System.out.println();
		}
	}
	
	private static void printAccInfo(String m) throws SQLException {
		int count = 0;
		Connection con = null;
		con = DatabasePool.getInstance().getConnection();
		String q = "SELECT login, accessLevel FROM accounts ";
		switch (m) {
			case "1":
				q = q.concat("WHERE accessLevel < 0");
				break;
			case "2":
				q = q.concat("WHERE accessLevel > 0");
				break;
			case "3":
				q = q.concat("WHERE accessLevel = 0");
				break;
		}
		q = q.concat(" ORDER BY login ASC");
		
		PreparedStatement statement = con.prepareStatement(q);
		ResultSet rset = statement.executeQuery();
		while (rset.next()) {
			System.out.println(rset.getString("login") + " -> " + rset.getInt("accessLevel"));
			count++;
		}
		rset.close();
		statement.close();
		DatabasePool.close(con);
		System.out.println("Displayed accounts: " + count + ".");
	}
	
	private static void addOrUpdateAccount(String account, String password, String level) throws IOException, SQLException, NoSuchAlgorithmException {
		// Encode Password
		byte[] newpass = PasswordCrypt.INSTANCE.encryptPassword(account, password);
		
		// Add to Base
		Connection con = null;
		con = DatabasePool.getInstance().getConnection();
		PreparedStatement statement = con.prepareStatement("REPLACE accounts (login, password, accessLevel) VALUES (?,?,?)");
		statement.setString(1, account);
		statement.setString(2, Base64.encodeBytes(newpass));
		statement.setString(3, level);
		statement.executeUpdate();
		statement.close();
		DatabasePool.close(con);
	}
	
	private static void changeAccountLevel(String account, String level) throws SQLException {
		Connection con = null;
		con = DatabasePool.getInstance().getConnection();
		
		// Check Account Exist
		PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE login=?;");
		statement.setString(1, account);
		ResultSet rset = statement.executeQuery();
		if (!rset.next()) {
			System.out.println("False");
		} else if (rset.getInt(1) > 0) {
			// Exist
			// Update
			statement = con.prepareStatement("UPDATE accounts SET accessLevel=? WHERE login=?;");
			statement.setEscapeProcessing(true);
			statement.setString(1, level);
			statement.setString(2, account);
			statement.executeUpdate();
			
			System.out.println("Account " + account + " has been updated.");
		} else {
			// Not Exist
			System.out.println("Account " + account + " does not exist.");
		}
		rset.close();
		statement.close();
		DatabasePool.close(con);
	}
	
	private static void deleteAccount(String account) throws SQLException {
		Connection con = null;
		con = DatabasePool.getInstance().getConnection();
		
		// Check Account Exist
		PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM accounts WHERE login=?;");
		statement.setString(1, account);
		ResultSet rset = statement.executeQuery();
		if (!rset.next()) {
			System.out.println("False");
			rset.close();
		} else if (rset.getInt(1) > 0) {
			rset.close();
			// Account exist
			// Get Accounts ID
			ResultSet rcln;
			statement = con.prepareStatement("SELECT charId, char_name, clanid FROM characters WHERE account_name=?;");
			statement.setEscapeProcessing(true);
			statement.setString(1, account);
			rset = statement.executeQuery();
			
			ArrayList<String> objIds = new ArrayList<>();
			ArrayList<String> charNames = new ArrayList<>();
			ArrayList<String> clanIds = new ArrayList<>();
			
			while (rset.next()) {
				objIds.add(rset.getString("charId"));
				charNames.add(rset.getString("char_name"));
				clanIds.add(rset.getString("clanid"));
			}
			rset.close();
			
			for (int index = 0; index < objIds.size(); index++) {
				System.out.println("Deleting character " + charNames.get(index) + ".");
				
				// Check If clan leader Remove Clan and remove all from it
				statement.close();
				statement = con.prepareStatement("SELECT COUNT(*) FROM clan_data WHERE leader_id=?;");
				statement.setString(1, clanIds.get(index));
				rcln = statement.executeQuery();
				rcln.next();
				if (rcln.getInt(1) > 0) {
					rcln.close();
					// Clan Leader
					
					// Get Clan Name
					statement.close();
					statement = con.prepareStatement("SELECT clan_name FROM clan_data WHERE leader_id=?;");
					statement.setString(1, clanIds.get(index));
					rcln = statement.executeQuery();
					rcln.next();
					
					String clanName = rcln.getString("clan_name");
					
					System.out.println("Deleting clan " + clanName + ".");
					
					// Delete Clan Wars
					statement.close();
					statement = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? OR clan2=?;");
					statement.setEscapeProcessing(true);
					statement.setString(1, clanName);
					statement.setString(2, clanName);
					statement.executeUpdate();
					
					rcln.close();
					
					// Remove All From clan
					statement.close();
					statement = con.prepareStatement("UPDATE characters SET clanid=0 WHERE clanid=?;");
					statement.setString(1, clanIds.get(index));
					statement.executeUpdate();
					// Free Clan Halls
					statement.close();
					statement = con.prepareStatement("UPDATE clanhall SET ownerId=0, paidUntil=0, paid=0 WHERE ownerId=?;");
					statement.setString(1, clanIds.get(index));
					statement.executeUpdate();
					// Delete Clan
					statement.close();
					statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?;");
					statement.setString(1, clanIds.get(index));
					statement.executeUpdate();
					//Clan privileges
					statement.close();
					statement = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?;");
					statement.setString(1, clanIds.get(index));
					statement.executeUpdate();
					//Clan subpledges
					statement.close();
					statement = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?;");
					statement.setString(1, clanIds.get(index));
					statement.executeUpdate();
					//Clan skills
					statement.close();
					statement = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?;");
					statement.setString(1, clanIds.get(index));
					statement.executeUpdate();
				} else {
					rcln.close();
				}
				
				// skills
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_skills WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// skills save
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// subclasses
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_subclasses WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// shortcuts
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// items
				statement.close();
				statement = con.prepareStatement("DELETE FROM items WHERE owner_id=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// recipebook
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// quests
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_quests WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// macroses
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_macroses WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// contacts
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_contacts WHERE charId=? OR contactId=?;");
				statement.setString(1, objIds.get(index));
				statement.setString(2, objIds.get(index));
				statement.executeUpdate();
				
				// friends
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_friends WHERE charId=? OR friendId=?;");
				statement.setString(1, objIds.get(index));
				statement.setString(2, objIds.get(index));
				statement.executeUpdate();
				
				// hennas
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_hennas WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// recommends
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_reco_bonus WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// ui categories
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_ui_categories WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// ui keys
				statement.close();
				statement = con.prepareStatement("DELETE FROM character_ui_keys WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// characters
				statement.close();
				statement = con.prepareStatement("DELETE FROM characters WHERE charId=?;");
				statement.setString(1, objIds.get(index));
				statement.executeUpdate();
				
				// TODO: delete pets, olympiad/noble/hero/statistics stuff
			}
			
			// characters
			statement.close();
			statement = con.prepareStatement("DELETE FROM account_gsdata WHERE account_name=?;");
			statement.setString(1, account);
			statement.executeUpdate();
			
			// Delete Account
			statement.close();
			statement = con.prepareStatement("DELETE FROM accounts WHERE login=?;");
			statement.setEscapeProcessing(true);
			statement.setString(1, account);
			statement.executeUpdate();
			
			System.out.println("Account " + account + " has been deleted.");
		} else {
			// Not Exist
			System.out.println("Account " + account + " does not exist.");
		}
		
		// Close Connection
		statement.close();
		DatabasePool.close(con);
	}
}
