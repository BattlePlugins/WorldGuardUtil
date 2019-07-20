package org.battleplugins.worldguardutil.worldguard.v0;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.battleplugins.worldguardutil.WorldGuardInterface;
import org.battleplugins.worldguardutil.exception.RegionNotFoundException;
import org.battleplugins.worldguardutil.math.BlockSelection;
import org.battleplugins.worldguardutil.region.ProtectedArenaRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Fixes NoClassDefFound caused by ClassNotFoundException.
 * This is an implementation where WorldGuard
 * is not installed, not compatible, or not supported.
 *
 * @author Nikolai
 */
public class WG extends WorldGuardInterface {

    @Override
    public boolean setWorldGuard(Plugin plugin) {
        return false;
    }

    /**
     * Even if getPlugin("WorldGuard"); does not equal null, the plugin
     * may not be fully intialized.
     * @return Always returns false.
     */
    @Override
    public boolean hasWorldGuard() {
        return false;
    }

    @Override
    public ProtectedRegion getRegion(String world, String id) {
        return null;
    }

    @Override
    public ProtectedRegion getRegion(World w, String id) {
        return null;
    }

    @Override
    public boolean hasRegion(World world, String id) {
        return false;
    }

    @Override
    public boolean hasRegion(String world, String id) {
        return false;
    }

    @Override
    public ProtectedRegion updateProtectedRegion(Player p, String id) throws Exception {
        return null;
    }

    @Override
    public ProtectedRegion createProtectedRegion(Player p, String id) throws Exception {
        return null;
    }

    @Override
    public void clearRegion(String world, String id) {
    }

    @Override
    public boolean isLeavingArea(Location from, Location to, World w, String id) {
        return false;
    }

    @Override
    public Flag<?> getWGFlag(String flagString) {
        return null;
    }

    @Override
    public StateFlag getStateFlag(String flagString) {
        return null;
    }

    @Override
    public boolean setFlag(String worldName, String id, String flag, boolean enable) {
        return false;
    }

    @Override
    public boolean allowEntry(Player player, String regionWorld, String id) {
        return true;
    }

    @Override
    public boolean addMember(String playerName, String regionWorld, String id) {
        return false;
    }

    @Override
    public boolean removeMember(String playerName, String regionWorld, String id) {
        return false;
    }

    @Override
    public void deleteRegion(String worldName, String id) {
    }

    @Override
    public boolean contains(Location location, String regionWorld, String id) {
        return false;
    }

    @Override
    public boolean hasPlayer(String playerName, String regionWorld, String id) {
        return false;
    }

    @Override
    public boolean trackRegion(String world, String id) throws RegionNotFoundException {
        return false;
    }

    @Override
    public int regionCount() {
        return 0;
    }

    @Override
    public ProtectedArenaRegion getContainingRegion(Location loc) {
        return null;
    }

    @Override
    public BlockSelection getBlockSelection(ProtectedArenaRegion region) {
        return null;
    }

    @Override
    public BlockSelection getBlockSelection(Region region) {
        return null;
    }

    @Override
    public BlockSelection getBlockSelection(World world, ProtectedRegion region) {
        return null;
    }

    @Override
    public boolean queryFlag(Location loc, Player player, StateFlag flag, StateFlag.State state) {
        return false;
    }
}