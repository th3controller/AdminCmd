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
package com.Balor.files.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class Utils {
	/**
	 * Translate the id or name to a material
	 * 
	 * @param mat
	 * @return Material
	 */
	public static MaterialContainer checkMaterial(String mat) {
		MaterialContainer mc = new MaterialContainer();
		String[] info = new String[2];
		if (mat.contains(":"))
			info = mat.split(":");
		else {
			info[0] = mat;
			info[1] = "0";
		}
		if ((mc = ACHelper.getInstance().getAlias(info[0])) == null) {
			mc = new MaterialContainer(info[0], info[1]);
		}
		return mc;

	}

	/**
	 * Parse a string and replace the color in it
	 * 
	 * @param toParse
	 * @return
	 */
	public static String colorParser(String toParse, String delimiter) {
		String ResultString = null;
		try {
			Pattern regex = Pattern.compile(delimiter + "[0-9]+");
			Matcher regexMatcher = regex.matcher(toParse);
			String result = null;
			while (regexMatcher.find()) {
				ResultString = regexMatcher.group();
				result = regexMatcher.replaceFirst(ChatColor.getByCode(
						Integer.parseInt(ResultString.substring(1))).toString());
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

	public static long getDistanceSquared(Player player1, Player player2) {
		if (!player1.getWorld().getName().equals(player2.getWorld().getName()))
			return Long.MAX_VALUE;
		Location loc1 = player1.getLocation();
		Location loc2 = player2.getLocation();
		return (loc1.getBlockX() - loc2.getBlockX()) ^ 2 + (loc1.getBlockZ() - loc2.getBlockZ())
				^ 2;
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
}