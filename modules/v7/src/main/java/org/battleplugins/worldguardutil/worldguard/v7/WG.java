package org.battleplugins.worldguardutil.worldguard.v7;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.battleplugins.worldguardutil.WorldGuardAbstraction;
import org.battleplugins.worldguardutil.controllers.WorldEditController;
import org.battleplugins.worldguardutil.math.BlockSelection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * The WorldGuard v7.x implementation.

 * @author alkarin, Europia79, Paaattiii
 */
public class WG extends WorldGuardAbstraction {

    protected final WorldGuard wg = WorldGuard.getInstance();

    @Override
    public BlockSelection getBlockSelection(Region region) {
        World world = BukkitAdapter.adapt(region.getWorld());

        Location min = new Location(world, region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
        Location max = new Location(world, region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());

        return new BlockSelection(world, min, max);
    }

    @Override
    public BlockSelection getBlockSelection(World world, ProtectedRegion region) {
        return new BlockSelection(world, BukkitAdapter.adapt(world, region.getMinimumPoint()), BukkitAdapter.adapt(world, region.getMaximumPoint()));
    }

    @Override
    public boolean queryFlag(Location loc, Player player, StateFlag flag, StateFlag.State state) {
        RegionQuery query = wg.getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        return regionSet.queryState(WorldGuardPlugin.inst().wrapPlayer(player), flag) == state;
    }

    @Override
    public ProtectedRegion getRegion(World w, String id) {
        if (w == null) {
            return null;
        }
        return wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w)).getRegion(id);
    }

    @Override
    public boolean hasRegion(World world, String id) {
        RegionManager mgr = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
        return mgr.hasRegion(id);
    }

    @Override
    public boolean hasRegion(String world, String id) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return false;
        }
        RegionManager mgr = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
        return mgr.hasRegion(id);
    }

    @Override
    public Flag<?> getWGFlag(String id) {
        Flag<?> f = WorldGuard.getInstance().getFlagRegistry().get(id);
        if (f != null && f.getName().equalsIgnoreCase(id)) {
            return f;
        }

        throw new IllegalStateException("Worldguard flag " + id + " not found");
    }

    @Override
    public StateFlag getStateFlag(String id) {
        Flag<?> f = WorldGuard.getInstance().getFlagRegistry().get(id);
        if (f instanceof StateFlag) {
            return (StateFlag) f;
        }

        throw new IllegalStateException("Worldguard flag " + id + " not found");
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
        Region sel = WorldEditController.getWorldEditPlugin().getSession(p).getSelection(BukkitAdapter.adapt(p.getWorld()));
        com.sk89q.worldedit.world.World w = sel.getWorld();
        RegionManager regionManager = wg.getPlatform().getRegionContainer().get(w);
        deleteRegion(w.getName(), id);
        ProtectedRegion region;
        // Detect the type of region from WorldEdit
        if (sel instanceof Polygonal2DRegion) {
            Polygonal2DRegion polySel = (Polygonal2DRegion) sel;
            int minY = polySel.getMinimumPoint().getBlockY();
            int maxY = polySel.getMaximumPoint().getBlockY();
            region = new ProtectedPolygonalRegion(id, polySel.getPoints(), minY, maxY);
        } else { /// default everything to cuboid
            region = new ProtectedCuboidRegion(id,
                    sel.getMinimumPoint(),
                    sel.getMaximumPoint());
        }
        region.setPriority(11); /// some relatively high priority
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
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
        RegionManager mgr = wg.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(w));
        if (mgr == null) {
            return;
        }
        mgr.removeRegion(id);
    }
}
