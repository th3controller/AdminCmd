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
package be.Balor.Manager.Commands.Player;

import java.util.HashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Balor.Tools.Utils;
import com.Balor.bukkit.AdminCmd.ACHelper;

import be.Balor.Manager.ACCommands;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class BanPlayer extends ACCommands {

	/**
	 * 
	 */
	public BanPlayer() {
		permNode = "admincmd.player.ban";
		cmdName = "bal_ban";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * be.Balor.Manager.ACCommands#execute(org.bukkit.command.CommandSender,
	 * java.lang.String[])
	 */
	@Override
	public void execute(CommandSender sender, String... args) {
		Player toBan = sender.getServer().getPlayer(args[0]);
		HashMap<String, String> replace = new HashMap<String, String>();
		replace.put("player", args[0]);
		String message = "";
		if (args.length >= 2)
			for (int i = 1; i < args.length; i++)
				message += args[i] + " ";
		else {
			message = "You have been ban by ";
			if (!Utils.isPlayer(sender, false))
				message += "Server Admin";
			else
				message += ((Player) sender).getName();
		}
		message = message.trim();
		if (toBan != null)
		{
			ACHelper.getInstance().addPowerUser("ban", toBan);
			toBan.kickPlayer(message);
			toBan.getServer().broadcastMessage(Utils.I18n("ban", replace));
		}
		else
			Utils.sI18n(sender, "playerNotFound", replace);
		

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null && args.length >= 1;
	}

}