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
package be.Balor.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import belgium.Balor.Workers.InvisibleWorker;

import com.Balor.bukkit.AdminCmd.AdminCmd;
import com.Balor.bukkit.AdminCmd.ACHelper;
import com.Balor.files.utils.ShootFireBall;
import com.Balor.files.utils.UpdateStatus;
import com.Balor.files.utils.Utils;

/**
 * @author Balor (aka Antoine Aflalo)
 * 
 */
public class ACPlayerListener extends PlayerListener {
	ACHelper worker;

	/**
	 * 
	 */
	public ACPlayerListener(ACHelper worker) {
		this.worker = worker;
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (playerRespawnOrJoin(event.getPlayer())) {
			event.setJoinMessage(null);
			Utils.sI18n(event.getPlayer(), "stillInv");
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (InvisibleWorker.getInstance().hasInvisiblePowers(event.getPlayer().getName()))
			event.setQuitMessage(null);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		playerRespawnOrJoin(event.getPlayer());

	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		if (ACHelper.getInstance().getConf().getBoolean("resetPowerWhenTpAnotherWorld", true)
				&& !from.getWorld().equals(to.getWorld())) {
			if (ACHelper.getInstance().removePlayerFromAllPowerUser(event.getPlayer().getName())) {
				Utils.sI18n(event.getPlayer(), "changedWorld");
			}
		}
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.LEFT_CLICK_AIR))) {
			String playerName = event.getPlayer().getName();
			if ((worker.hasThorPowers(playerName)))
				event.getPlayer().getWorld()
						.strikeLightning(event.getPlayer().getTargetBlock(null, 600).getLocation());
			Float power = null;
			if ((power = worker.getVulcainExplosionPower(playerName)) != null)
				event.getPlayer()
						.getWorld()
						.createExplosion(event.getPlayer().getTargetBlock(null, 600).getLocation(),
								power, true);
			power = null;
			if ((power = (Float) worker.getPowerOfPowerUser("fireball", playerName)) != null)
				worker.getPluginInstance()
						.getServer()
						.getScheduler()
						.scheduleAsyncDelayedTask(worker.getPluginInstance(),
								new ShootFireBall(event.getPlayer(), power));
		}
	}

	private boolean playerRespawnOrJoin(Player newPlayer) {
		AdminCmd.getBukkitServer()
				.getScheduler()
				.scheduleAsyncDelayedTask(worker.getPluginInstance(),
						new UpdateInvisibleOnJoin(newPlayer), 25);
		if (InvisibleWorker.getInstance().hasInvisiblePowers(newPlayer.getName())) {
			AdminCmd.getBukkitServer()
					.getScheduler()
					.scheduleAsyncDelayedTask(worker.getPluginInstance(),
							new UpdateStatus(newPlayer), 25);
			return true;
		}
		return false;
	}

	protected class UpdateInvisibleOnJoin implements Runnable {
		Player newPlayer;

		/**
		 * 
		 */
		public UpdateInvisibleOnJoin(Player p) {
			newPlayer = p;
		}

		public void run() {
			for (Player toVanish : InvisibleWorker.getInstance().getAllInvisiblePlayers())
				InvisibleWorker.getInstance().invisible(toVanish, newPlayer);
		}
	}

}
