/************************************************************************
 * This file is part of AdminCmd.									
 *																		
 * AdminCmd is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by	
 * the Free Software Foundation, either version 3 of the License, or		
 * (at your option) any later version.									
 *																		
 * AdminCmd is distributed in the hope that it will be useful,	
 * but WITHOUT ANY WARRANTY; without even the implied warranty of		
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the			
 * GNU General Public License for more details.							
 *																		
 * You should have received a copy of the GNU General Public License
 * along with AdminCmd.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************/
package be.Balor.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import be.Balor.Manager.LocaleManager;
import be.Balor.Manager.Permissions.PermissionManager;
import be.Balor.bukkit.AdminCmd.ACHelper;
import be.Balor.bukkit.AdminCmd.AdminCmd;
import belgium.Balor.Workers.InvisibleWorker;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Utils {
	private static Block currentBlock;

	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public static MaterialContainer checkMaterial(String mat) {
		MaterialContainer mc = new MaterialContainer();
		String[] info = new String[2];
		if (mat.contains(":")) {
			info = mat.split(":");
			mc = new MaterialContainer(info[0], info[1]);
		} else {
			info[0] = mat;
			info[1] = "0";
			if ((mc = ACHelper.getInstance().getAlias(info[0])) == null) {
				mc = new MaterialContainer(info[0], info[1]);
			}
		}

		return mc;

	}

	/**
	 * Parse a string and replace the color in it
	 * 
	 * @author Speedy64
	 * @param toParse
	 * @return
	 */
	public static String colorParser(String toParse, String delimiter) {
		String ResultString = null;
		try {
			Pattern regex = Pattern.compile(delimiter + "[A-Fa-f]|" + delimiter + "1[0-5]|"
					+ delimiter + "[0-9]");
			Matcher regexMatcher = regex.matcher(toParse);
			String result = null;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				int colorint = Integer.parseInt(ResultString.substring(1, 2), 16);
				if (ResultString.length() > 1) {
					if (colorint == 1 && ResultString.substring(2).matches("[012345]")) {
						colorint = colorint * 10 + Integer.parseInt(ResultString.substring(2));
					}
				}
				result = regexMatcher.replaceFirst(ChatColor.getByCode(colorint).toString());
				regexMatcher = regex.matcher(result);
			}
			return result;
		} catch (Exception ex) {
			return null;
		}
	}

	public static String colorParser(String toParse) {
		return colorParser(toParse, "&");
	}

	public static double getDistanceSquared(Player player1, Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName()))
			return Long.MAX_VALUE;
		Location loc1 = player1.getLocation();
		Location loc2 = player2.getLocation();
		return Math.pow((loc1.getX() - loc2.getX()), 2) + Math.pow((loc1.getZ() - loc2.getZ()), 2);
	}

	/**
	 * Check if the command sender is a Player
	 * 
	 * @return
	 */
	public static boolean isPlayer(CommandSender sender) {
		return isPlayer(sender, true);
	}

	public static boolean isPlayer(CommandSender sender, boolean msg) {
		if (sender instanceof Player)
			return true;
		else {
			if (msg)
				sender.sendMessage("You must be a player to use this command.");
			return false;
		}
	}

	/**
	 * Heal the selected player.
	 * 
	 * @param name
	 * @return
	 */
	public static boolean setPlayerHealth(CommandSender sender, String[] name, String toDo) {
		Player target = getUser(sender, name, "admincmd.player." + toDo);
		if (target == null)
			return false;
		if (toDo.equals("heal")) {
			target.setHealth(20);
			target.setFireTicks(0);
		} else
			target.setHealth(0);

		return true;
	}

	/**
	 * Get the user and check who launched the command.
	 * 
	 * @param sender
	 * @param args
	 * @param permNode
	 * @param index
	 * @param errorMsg
	 * @return
	 */
	public static Player getUser(CommandSender sender, String[] args, String permNode, int index,
			boolean errorMsg) {
		Player target = null;
		if (args.length >= index + 1) {
			if (PermissionManager.hasPerm(sender, permNode + ".other"))
				target = sender.getServer().getPlayer(args[index]);
			else
				return target;
		} else if (isPlayer(sender, false))
			target = ((Player) sender);
		else if (errorMsg) {
			sender.sendMessage("You must type the player name");
			return target;
		}
		if (target == null && errorMsg) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", args[index]);
			Utils.sI18n(sender, "playerNotFound", replace);
			return target;
		}
		return target;

	}

	public static Player getUser(CommandSender sender, String[] args, String permNode) {
		return getUser(sender, args, permNode, 0, true);
	}

	public static void sendMessage(CommandSender sender, CommandSender player, String key) {
		sendMessage(sender, player, key, null);
	}

	public static void sendMessage(CommandSender sender, CommandSender player, String key,
			Map<String, String> replace) {
		String msg = I18n(key, replace);
		if (msg != null && !msg.isEmpty()) {
			if (!sender.equals(player))
				player.sendMessage(msg);
			sender.sendMessage(msg);
		}

	}

	public static void sI18n(CommandSender sender, String key, Map<String, String> replace) {
		String locale = I18n(key, replace);
		if (locale != null && !locale.isEmpty())
			sender.sendMessage(locale);
	}

	public static void sI18n(CommandSender sender, String key, String alias, String toReplace) {
		String locale = I18n(key, alias, toReplace);
		if (locale != null && !locale.isEmpty())
			sender.sendMessage(locale);
	}

	public static void sI18n(CommandSender sender, String key) {
		sI18n(sender, key, null);
	}

	public static String I18n(String key) {
		return I18n(key, null);
	}

	public static String I18n(String key, String alias, String toReplace) {
		return LocaleManager.getInstance().get(key, alias, toReplace);
	}

	public static String I18n(String key, Map<String, String> replace) {
		return LocaleManager.getInstance().get(key, replace);
	}

	public static void addLocale(String key, String value) {
		LocaleManager.getInstance().addLocale(key, value);
	}

	private static void setTime(World w, String arg) {
		long curtime = w.getTime();
		long newtime = curtime - (curtime % 24000);
		if (arg.equalsIgnoreCase("day"))
			newtime += 0;
		else if (arg.equalsIgnoreCase("night"))
			newtime += 14000;
		else if (arg.equalsIgnoreCase("dusk"))
			newtime += 12500;
		else if (arg.equalsIgnoreCase("dawn"))
			newtime += 23000;
		else
			// if not a constant, use raw time
			try {
				newtime += Integer.parseInt(arg);
			} catch (Exception e) {
			}
		w.setTime(newtime);
	}

	// all functions return if they handled the command
	// false then means to show the default handle
	// ! make sure the player variable IS a player!
	// set world time to a new value
	public static boolean timeSet(CommandSender sender, String arg) {
		if (isPlayer(sender, false)) {
			Player p = (Player) sender;
			setTime(p.getWorld(), arg);
		} else {
			for (World w : sender.getServer().getWorlds())
				setTime(w, arg);
		}
		return true;

	}

	public static boolean tpP2P(CommandSender sender, String nFrom, String nTo) {
		boolean found = true;
		Player pFrom = AdminCmd.getBukkitServer().getPlayer(nFrom);
		Player pTo = AdminCmd.getBukkitServer().getPlayer(nTo);
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", nFrom);
		if (pFrom == null) {
			replace.put("player", nFrom);
			Utils.sI18n(sender, "playerNotFound", replace);
			found = false;
		}
		if (pTo == null) {
			replace.put("player", nTo);
			Utils.sI18n(sender, "playerNotFound", replace);
			found = false;
		}

		if (found) {
			if (InvisibleWorker.getInstance().hasInvisiblePowers(pTo.getDisplayName())
					&& !PermissionManager.hasPerm(pFrom, "admincmd.invisible.cansee", false)) {
				replace.put("player", nTo);
				Utils.sI18n(sender, "playerNotFound", replace);
				return false;
			}
			if ((InvisibleWorker.getInstance().hasInvisiblePowers(pFrom.getDisplayName()) && !PermissionManager
					.hasPerm(pTo, "admincmd.invisible.cansee", false))) {
				replace.put("player", nFrom);
				Utils.sI18n(sender, "playerNotFound", replace);
				return false;
			}
			pFrom.teleport(pTo);
			replace.put("fromPlayer", pFrom.getName());
			replace.put("toPlayer", pTo.getName());
			Utils.sI18n(sender, "tp", replace);
		}
		return found;
	}

	private static void weatherChange(CommandSender sender, World w, Weather type, String[] duration) {
		if (!type.equals(Weather.FREEZE)
				&& ACHelper.getInstance().isValueSet(Type.WEATHER_FREEZED, w.getName())) {
			sender.sendMessage(ChatColor.GOLD + Utils.I18n("wFreezed") + " " + w.getName());
			return;
		}
		switch (type) {
		case CLEAR:
			w.setThundering(false);
			w.setStorm(false);
			sender.sendMessage(ChatColor.GOLD + Utils.I18n("sClear") + " " + w.getName());
			break;
		case STORM:
			HashMap<String, String> replace = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setThundering(true);
				w.setWeatherDuration(12000);
				replace.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
			} else {
				try {
					w.setStorm(true);
					w.setThundering(true);
					int time = Integer.parseInt(duration[0]);
					w.setWeatherDuration(time * 1200);
					replace.put("duration", duration[0]);
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration[0]
							+ ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replace.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sStorm", replace) + w.getName());
				}
			}
			break;
		case FREEZE:
			if (ACHelper.getInstance().isValueSet(Type.WEATHER_FREEZED, w.getName())) {
				ACHelper.getInstance().removeValue(Type.WEATHER_FREEZED, w.getName());
				sender.sendMessage(ChatColor.GREEN + Utils.I18n("wUnFreezed") + " "
						+ ChatColor.WHITE + w.getName());
			} else {
				ACHelper.getInstance().addValue(Type.WEATHER_FREEZED, w.getName());
				sender.sendMessage(ChatColor.RED + Utils.I18n("wFreezed") + " " + ChatColor.WHITE
						+ w.getName());
			}
			break;
		case RAIN:
			HashMap<String, String> replaceRain = new HashMap<String, String>();
			if (duration == null || duration.length < 1) {
				w.setStorm(true);
				w.setThundering(false);
				w.setWeatherDuration(12000);
				replaceRain.put("duration", "10");
				sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain) + w.getName());
			} else {
				try {
					w.setStorm(true);
					w.setThundering(false);
					int time = Integer.parseInt(duration[0]);
					w.setWeatherDuration(time * 1200);
					replaceRain.put("duration", duration[0]);
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain)
							+ w.getName());
				} catch (NumberFormatException e) {
					sender.sendMessage(ChatColor.BLUE + "Sorry, that (" + duration[0]
							+ ") isn't a number!");
					w.setStorm(true);
					w.setWeatherDuration(12000);
					replaceRain.put("duration", "10");
					sender.sendMessage(ChatColor.GOLD + Utils.I18n("sRain", replaceRain)
							+ w.getName());
				}
			}
			break;
		default:
			break;
		}
	}

	public static boolean weather(CommandSender sender, Weather type, String[] duration) {
		if (isPlayer(sender, false)) {
			weatherChange(sender, ((Player) sender).getWorld(), type, duration);
		} else
			for (World w : sender.getServer().getWorlds())
				weatherChange(sender, w, type, duration);

		return true;
	}

	public static void sParsedLocale(Player p, String locale) {
		if ((Boolean) ACHelper.getInstance().getConfValue("MessageOfTheDay")) {
			HashMap<String, String> replace = new HashMap<String, String>();
			replace.put("player", p.getName());
			replace.put(
					"nb",
					String.valueOf(p.getServer().getOnlinePlayers().length
							- InvisibleWorker.getInstance().nbInvisibles()));
			String connected = "";
			for (Player player : p.getServer().getOnlinePlayers())
				if (!InvisibleWorker.getInstance().hasInvisiblePowers(player.getName()))
					connected += p.getDisplayName() + ", ";
			if (!connected.equals("")) {
				if (connected.endsWith(", "))
					connected = connected.substring(0, connected.lastIndexOf(","));
			}
			replace.put("connected", connected);
			String motd = I18n(locale, replace);
			if (motd != null)
				for (String toSend : motd.split("/n"))
					p.sendMessage(toSend);
		}
	}

	public static Integer replaceBlockByAir(CommandSender sender, String[] args,
			List<Material> mat, int defaultRadius) {
		if (Utils.isPlayer(sender)) {
			int radius = defaultRadius;
			if (args.length >= 1) {
				try {
					radius = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					if (args.length >= 2)
						try {
							radius = Integer.parseInt(args[1]);
						} catch (NumberFormatException e2) {

						}
				}

			}
			Stack<BlockRemanence> blocks;
			Block block = ((Player) sender).getLocation().getBlock();

			if (mat.contains(Material.WATER) || mat.contains(Material.LAVA)) {
				if (radius > 50)
					radius = 50;
			} else if (radius > 30)
				radius = 30;
			if (!mat.contains(Material.FIRE))
				blocks = replaceAdjacentBlocks(mat, block, radius);
			else
				blocks = replaceInSphere(mat, block, radius);

			ACHelper.getInstance().addInUndoQueue(((Player) sender).getName(), blocks);
			return blocks.size();
		}
		return null;
	}

	private static Stack<BlockRemanence> replaceInSphere(List<Material> mat, Block block, int radius) {
		Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		int limitX = block.getX() + radius;
		int limitY = block.getY() + radius;
		int limitZ = block.getZ() + radius;
		Block current;
		for (int x = block.getX() - radius; x <= limitX; x++)
			for (int y = block.getY() - radius; y <= limitY; y++)
				for (int z = block.getZ() - radius; z <= limitZ; z++) {
					current = block.getWorld().getBlockAt(x, y, z);
					if (mat.contains(current.getType())) {
						blocks.push(new BlockRemanence(current));
						current.setType(Material.AIR);
					}

				}
		return blocks;
	}

	private static Stack<BlockRemanence> replaceAdjacentBlocks(List<Material> mat, Block block,
			int radius) {
		Stack<BlockRemanence> blocks = new Stack<BlockRemanence>();
		Stack<SimplifiedLocation> processQueue = new Stack<SimplifiedLocation>();
		double squaredRadius = Math.pow(radius, 2);
		World w = block.getWorld();
		Location start = block.getLocation();
		for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
			for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
				for (int y = block.getY() - 2; y <= block.getY() + 2; y++) {
					processQueue.push(new SimplifiedLocation(w, x, y, z));
				}
			}
		}
		BlockRemanence current = null;
		while (!processQueue.isEmpty()) {
			SimplifiedLocation loc = processQueue.pop();
			if (!isSameMaterial(mat, loc))
				continue;
			if (loc.isVisited())
				continue;
			loc.setVisited();

			if (start.distanceSquared(loc) > squaredRadius)
				continue;

			for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; ++x) {
				for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; ++z) {
					for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; ++y) {
						SimplifiedLocation newPos = new SimplifiedLocation(w, x, y, z);

						if (!loc.equals(newPos)) {
							processQueue.push(newPos);
						}
					}
				}
			}
			current = new BlockRemanence(currentBlock);
			blocks.push(current);
			current.setBlockType(Material.AIR);
		}
		return blocks;
	}

	private static boolean isSameMaterial(List<Material> mat, Location loc) {
		Block b = loc.getWorld().getBlockAt(loc);
		if (b == null)
			return false;
		currentBlock = b;
		return mat.contains(b.getType());
	}
}
