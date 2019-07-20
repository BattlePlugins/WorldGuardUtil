package org.battleplugins.worldguardutil.worldguard.v5;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.battleplugins.worldguardutil.WorldGuardAbstraction;
import org.battleplugins.worldguardutil.controllers.WorldEditController;
import org.battleplugins.worldguardutil.math.BlockSelection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The WorldGuard v5.x implementation.
 *
 * @author alkarin
 */
public class WG extends WorldGuardAbstraction {

    @Override
    public BlockSelection getBlockSelection(Region region) {
        return new BlockSelection(BukkitUtil.toWorld(region.getWorld()), BukkitUtil.toLocation(BukkitUtil.toWorld(region.getWorld()), region.getMinimumPoint()), BukkitUtil.toLocation(BukkitUtil.toWorld(region.getWorld()), region.getMaximumPoint()));
    }

    @Override
    public BlockSelection getBlockSelection(World world, ProtectedRegion region) {
        return new BlockSelection(world, BukkitUtil.toLocation(world, region.getMinimumPoint()), BukkitUtil.toLocation(world, region.getMaximumPoint()));
    }

    @Override
    public boolean queryFlag(Location loc, Player player, StateFlag flag, StateFlag.State state) {
        RegionManager regionManager = WGBukkit.getRegionManager(loc.getWorld());
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(loc);
        if (player == null)
            return regionSet.getFlag(flag) == state;

        return regionSet.getFlag(flag, WGBukkit.getPlugin().wrapPlayer(player)) == state;
    }

    @Override
    public ProtectedRegion getRegion(World w, String id) {
        if (w == null) {
            return null;
        }
        return wgp.getRegionManager(w).getRegion(id);
    }

    @Override
    public boolean hasRegion(World world, String id) {
        RegionManager mgr = wgp.getGlobalRegionManager().get(world);
        return mgr.hasRegion(id);
    }

    @Override
    public boolean hasRegion(String world, String id) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return false;
        }
        RegionManager mgr = wgp.getGlobalRegionManager().get(w);
        return mgr.hasRegion(id);
    }

    @Override
    public Flag<?> getWGFlag(String flagString) {
        for (Flag<?> f : DefaultFlag.getFlags()) {
            if (f.getName().equalsIgnoreCase(flagString)) {
                return f;
            }
        }
        throw new IllegalStateException("Worldguard flag " + flagString + " not found");
    }

    @Override
    public StateFlag getStateFlag(String flagString) {
        for (Flag<?> f : DefaultFlag.getFlags()) {
            if (f.getName().equalsIgnoreCase(flagString) && f instanceof StateFlag) {
                return (StateFlag) f;
            }
        }
        throw new IllegalStateException("Worldguard flag " + flagString + " not found");
    }

    @Override
    public ProtectedRegion updateProtectedRegion(Player p, String id) throws Exception {
        return createRegion(p, id);
    }

    @Override
    public ProtectedRegion createProtectedRegion(Player p, String id) throws Exception {
        return createRegion(p, id);
    }

    private ProtectedRegion createRegion(Player p, String id) throws Exception {
        Selection sel = WorldEditController.getSelection(p);
        World w = sel.getWorld();
        GlobalRegionManager gmanager = wgp.getGlobalRegionManager();
        RegionManager regionManager = gmanager.get(w);
        deleteRegion(w.getName(), id);
        ProtectedRegion region;
        // Detect the type of region from WorldEdit
        if (sel instanceof Polygonal2DSelection) {
            Polygonal2DSelection polySel = (Polygonal2DSelection) sel;
            int minY = polySel.getNativeMinimumPoint().getBlockY();
            int maxY = polySel.getNativeMaximumPoint().getBlockY();
            region = new ProtectedPolygonalRegion(id, polySel.getNativePoints(), minY, maxY);
        } else { /// default everything to cuboid
            region = new ProtectedCuboidRegion(id,
                    sel.getNativeMinimumPoint().toBlockVector(),
                    sel.getNativeMaximumPoint().toBlockVector());
        }
        region.setPriority(11); /// some relatively high priority
        region.setFlag(DefaultFlag.PVP, StateFlag.State.ALLOW);
        regionManager.addRegion(region);
        regionManager.save();
        return region;
    }

    @Override
    public void deleteRegion(String worldName, String id) {
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            return;
        }
        RegionManager mgr = wgp.getRegionManager(w);
        if (mgr == null) {
            return;
        }
        mgr.removeRegion(id);
    }
}
