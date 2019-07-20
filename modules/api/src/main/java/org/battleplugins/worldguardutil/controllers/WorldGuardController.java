package org.battleplugins.worldguardutil.controllers;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.battleplugins.worldguardutil.WorldGuardInterface;
import org.battleplugins.worldguardutil.exception.RegionNotFoundException;
import org.battleplugins.worldguardutil.math.BlockSelection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * @author alkarin
 *
 * The key to these optional dependencies(OD) seem to be there can be no direct
 * function call to a method that USES any of the OD classes. So this entire
 * class is just a wrapper for functions. Also other classes should not declare
 * variables of the OD as a class variable
 *
 */
public class WorldGuardController {

    static boolean hasWorldGuard = false;
    static boolean hasWorldEdit = false;

    public static final WorldGuardInterface wg = WorldGuardInterface.newInstance();

    public static class WorldGuardException extends Exception {

        private static final long serialVersionUID = 1L;

        public WorldGuardException(String msg) {
            super(msg);
        }
    }

    public static boolean hasWorldGuard() {
        return Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
    }

    public static boolean hasWorldEdit() {
        return Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
    }

    public boolean addRegion(Player sender, String id) throws Exception {
        return wg.createProtectedRegion(sender, id) != null;
    }

    public static boolean hasRegion(World world, String id) {
        return wg.hasRegion(world, id);
    }

    public static boolean hasRegion(String world, String id) {
        return wg.hasRegion(world, id);
    }

    public static void updateProtectedRegion(Player p, String id) throws Exception {
        wg.updateProtectedRegion(p, id);
    }

    public static void createProtectedRegion(Player p, String id) throws Exception {
        wg.createProtectedRegion(p, id);
    }

    public static void clearRegion(String world, String id) {
        wg.clearRegion(world, id);
    }

    public static boolean isLeavingArea(final Location from, final Location to, World w, String id) {
        return wg.isLeavingArea(from, to, w, id);
    }

    public static boolean setWorldGuard(Plugin plugin) {
        hasWorldGuard = wg.setWorldGuard(plugin);
        return hasWorldGuard;
    }

    public static boolean setWorldEdit(Plugin plugin) {
        hasWorldEdit = WorldEditController.setWorldEdit(plugin);
        return hasWorldEdit;
    }

    public static boolean setFlag(String worldName, String id, String flag, boolean enable) {
        return wg.setFlag(worldName, id, flag, enable);
    }

    public static Flag<?> getWGFlag(String flagString) {
        return wg.getWGFlag(flagString);
    }

    public static StateFlag getStateFlag(String flagString) {
        return wg.getStateFlag(flagString);
    }

    public static void allowEntry(Player player, String regionWorld, String id) {
        wg.allowEntry(player, regionWorld, id);
    }

    public static void addMember(String playerName, String regionWorld, String id) {
        wg.addMember(playerName, regionWorld, id);
    }

    public static void removeMember(String playerName, String regionWorld, String id) {
        wg.removeMember(playerName, regionWorld, id);
    }

    public static void deleteRegion(String worldName, String id) {
        wg.deleteRegion(worldName, id);
    }

    public static int regionCount() {
        return wg.regionCount();
    }

    public static boolean trackRegion(String world, String id) throws RegionNotFoundException {
        return wg.trackRegion(world, id);
    }

    public static ProtectedRegion getRegion(String world, String id) {
        return wg.getRegion(world, id);
    }

    public static ProtectedRegion getRegion(World world, String id) {
        return wg.getRegion(world, id);
    }

    public static BlockSelection getBlockSelection(Region region) {
        return wg.getBlockSelection(region);
    }

    public static BlockSelection getBlockSelection(World world, ProtectedRegion region) {
        return wg.getBlockSelection(world, region);
    }

    public static boolean queryFlag(Location loc, Player player, String flagString, String stateString) {
        return queryFlag(loc, player, getStateFlag(flagString), StateFlag.State.valueOf(stateString.toUpperCase()));
    }

    public static boolean queryFlag(Location loc, Player player, StateFlag flag, StateFlag.State state) {
        return wg.queryFlag(loc, player, flag, state);
    }
}
