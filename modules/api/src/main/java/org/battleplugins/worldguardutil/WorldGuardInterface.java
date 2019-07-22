package org.battleplugins.worldguardutil;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.lang.reflect.Constructor;
import org.battleplugins.worldguardutil.exception.RegionNotFoundException;
import org.battleplugins.worldguardutil.math.BlockSelection;
import mc.euro.version.FieldTester;
import mc.euro.version.Version;
import mc.euro.version.VersionFactory;
import org.battleplugins.worldguardutil.region.ProtectedArenaRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * We want to always reference an abstraction of WorldGuard and have the
 * actual implementation decided at runtime: v0, v5, v6. <br/><br/>
 *
 * This class was a conversion from the static wrapper WorldGuardUtil.
 * Converted to an abstraction so that our implementation can vary at runtime.
 *
 * @author alkarin, Nikolai
 */
public abstract class WorldGuardInterface {

    /**
     * Instantiates: org.battleplugins.worldguardutil.worldguard.{version}.WG.
     *
     * Based on the version of WorldGuard that the server has.
     */
    public static WorldGuardInterface newInstance() {
        WorldGuardInterface WGI = null;
        // WorldEdit is needed in order for WorldGuard to run
        Version<Plugin> we = VersionFactory.getPluginVersion("WorldEdit");
        Version<Plugin> wg = VersionFactory.getPluginVersion("WorldGuard");
        WorldGuardPlugin wgp = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

        boolean wgIsInitialized = FieldTester.isInitialized(wgp);
        if (we.isCompatible("7") && wg.isCompatible("7") && wgIsInitialized) {
            WGI = instantiate("v7");
        } else if (we.isLessThan("7") && we.isCompatible("6") && wg.isLessThan("7") && wg.isCompatible("6") && wgIsInitialized) {
            WGI = instantiate("v6");
        } else if (we.isLessThan("6") && we.isCompatible("5") && wg.isLessThan("6") && wg.isCompatible("5") && wgIsInitialized) {
            WGI = instantiate("v5");
        } else {
            WGI = instantiate("v0");
        }
        return WGI;
    }

    /**
     * Instantiates: org.battleplugins.worldguardutil.worldguard.{version}.WG.
     */
    private static WorldGuardInterface instantiate(String version) {
        String classPackage = "org.battleplugins.worldguardutil.worldguard." + version + ".WG";
        WorldGuardInterface WGI = null;
        Class<?>[] args = {};
        Class clazz = null;
        Constructor con = null;
        try {
            clazz = Class.forName(classPackage);
            con = clazz.getConstructor(args);
            WGI = (WorldGuardInterface) con.newInstance();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return WGI;
    }

    public abstract boolean setWorldGuard(Plugin plugin);

    public abstract boolean hasWorldGuard();

    public abstract ProtectedRegion getRegion(String world, String id);

    public abstract ProtectedRegion getRegion(World w, String id);

    public abstract boolean hasRegion(World world, String id);

    public abstract boolean hasRegion(String world, String id);

    public abstract ProtectedRegion updateProtectedRegion(Player p, String id) throws Exception;

    public abstract ProtectedRegion createProtectedRegion(Player p, String id) throws Exception;

    public abstract void clearRegion(String world, String id);

    public abstract boolean isLeavingArea(final Location from, final Location to, final World w, String id);

    public abstract Flag<?> getWGFlag(String flagString);

    public abstract StateFlag getStateFlag(String flagString);

    public abstract boolean setFlag(String worldName, String id, String flag, boolean enable);

    public abstract boolean allowEntry(Player player, String regionWorld, String id);

    public abstract boolean addMember(String playerName, String regionWorld, String id);

    public abstract boolean removeMember(String playerName, String regionWorld, String id);

    public abstract void deleteRegion(String worldName, String id);

    public abstract boolean contains(Location location, String regionWorld, String id);

    public abstract boolean hasPlayer(String playerName, String regionWorld, String id);

    public abstract boolean trackRegion(String world, String id) throws RegionNotFoundException;

    public abstract int regionCount();

    public abstract ProtectedArenaRegion getContainingRegion(Location loc);

    public abstract BlockSelection getBlockSelection(ProtectedArenaRegion region);

    public abstract BlockSelection getBlockSelection(Region region);

    public abstract BlockSelection getBlockSelection(World world, ProtectedRegion region);

    public abstract boolean queryFlag(Location loc, Player player, StateFlag flag, StateFlag.State state);
}
