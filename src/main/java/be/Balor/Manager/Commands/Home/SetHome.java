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
package be.Balor.Manager.Commands.Home;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import be.Balor.Manager.CoreCommand;
import be.Balor.Player.ACPlayer;
import be.Balor.Tools.Utils;
import be.Balor.bukkit.AdminCmd.ACHelper;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class SetHome extends CoreCommand {

	/**
	 * 
	 */
	public SetHome() {
		permNode = "admincmd.tp.home";
		cmdName = "bal_sethome";
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
		if (Utils.isPlayer(sender)) {
			Player p = ((Player) sender);
			ACPlayer player = ACPlayer.getPlayer(p.getName());
			List<String> tmp = player.getHomeList();
			String home = p.getWorld().getName();
			if (args.length >= 1)
				home = args[0];
			Location loc = p.getLocation();
			if (!tmp.contains(home)
					&& tmp.size() + 1 > ACHelper.getInstance().getLimit(p, "maxHomeByUser")) {
				Utils.sI18n(sender, "homeLimit");
				return;
			}
			player.setHome(home, loc);
			Utils.sI18n(sender, "setMultiHome", "home", home);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.Balor.Manager.ACCommands#argsCheck(java.lang.String[])
	 */
	@Override
	public boolean argsCheck(String... args) {
		return args != null;
	}

	@Override
	public void registerBukkitPerm() {
		super.registerBukkitPerm();
		for (int i = 0; i < 150; i++)
			plugin.getPermissionLinker().addPermChild("admincmd.maxHomeByUser." + i,
					PermissionDefault.FALSE);
	}

}
