package org.battleplugins.worldguardutil;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.battleplugins.worldguardutil.controllers.WorldEditController;
import org.battleplugins.worldguardutil.exception.RegionNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 *
 * @author alkarin
 */
public abstract class WorldGuardAbstraction extends WorldGuardInterface {

    protected final WorldGuardPlugin wgp = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    boolean hasWorldGuard = (wgp != null);

    Map<String, Set<String>> trackedRegions = new ConcurrentHashMap<String, Set<String>>();

    /**
     * Provides legacy support.
     */
    @Override
    public boolean setWorldGuard(Plugin plugin) {
        if (plugin != null) {
            hasWorldGuard = true;
        }
        return hasWorldGuard();
    }

    @Override
    public boolean hasWorldGuard() {
        return WorldEditController.hasWorldEdit() && hasWorldGuard;
    }

    @Override
    public ProtectedRegion getRegion(String world, String id) {
        World w = Bukkit.getWorld(world);
        return getRegion(w, id);
    }

    @Override
    public void clearRegion(String world, String id) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            return;
        }
        ProtectedRegion region = getRegion(w, id);
        if (region == null) {
            return;
        }

        Location l;
        for (Entity entity : w.getEntitiesByClasses(Item.class, Creature.class)) {
            l = entity.getLocation();
            if (region.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ())) {
                entity.remove();
            }
        }
    }

    @Override
    public boolean isLeavingArea(final Location from, final Location to, final World w, String id) {
        ProtectedRegion pr = getRegion(w, id);
        return pr != null
                && (!pr.contains(to.getBlockX(), to.getBlockY(), to.getBlockZ())
                && pr.contains(from.getBlockX(), from.getBlockY(), from.getBlockZ()));
    }

    @Override
    public boolean setFlag(String worldName, String id, String flag, boolean enable) {
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            return false;
        }
        ProtectedRegion pr = getRegion(w, id);
        if (pr == null) {
            return false;
        }
        StateFlag f = getStateFlag(flag);
        StateFlag.State newState = enable ? StateFlag.State.ALLOW : StateFlag.State.DENY;
        StateFlag.State state = pr.getFlag(f);

        if (state == null || state != newState) {
            pr.setFlag(f, newState);
        }
        return true;
    }

    @Override
    public boolean allowEntry(Player player, String regionWorld, String id) {
        World w = Bukkit.getWorld(regionWorld);
        if (w == null) {
            return false;
        }
        ProtectedRegion pr = getRegion(w, id);
        if (pr == null) {
            return false;
        }
        DefaultDomain dd = pr.getMembers();
        dd.addPlayer(player.getName());
        pr.setMembers(dd);
        return true;
    }

    @Override
    public boolean addMember(String playerName, String regionWorld, String id) {
        return changeMember(playerName, regionWorld, id, true);
    }

    @Override
    public boolean removeMember(String playerName, String regionWorld, String id) {
        return changeMember(playerName, regionWorld, id, false);
    }

    private boolean changeMember(String name, String regionWorld, String id, boolean add) {
        World w = Bukkit.getWorld(regionWorld);
        if (w == null) {
            return false;
        }
        ProtectedRegion pr = getRegion(w, id);
        if (pr == null) {
            return false;
        }

        DefaultDomain dd = pr.getMembers();
        if (add) {
            dd.addPlayer(name);
        } else {
            dd.removePlayer(name);
        }
        pr.setMembers(dd);
        return true;
    }

    @Override
    public boolean contains(Location location, String regionWorld, String id) {
        ProtectedRegion pr = getRegion(regionWorld, id);
        return pr != null
                && pr.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean hasPlayer(String playerName, String regionWorld, String id) {
        ProtectedRegion pr = getRegion(regionWorld, id);
        if (pr == null) {
            return true;
        }
        DefaultDomain dd = pr.getMembers();
        if (dd.contains(playerName)) {
            return true;
        }
        dd = pr.getOwners();
        return dd.contains(playerName);
    }

    @Override
    public boolean trackRegion(String world, String id) throws RegionNotFoundException {
        ProtectedRegion pr = getRegion(world, id);
        if (pr == null) {
            throw new RegionNotFoundException("The region " + id + " not found in world " + world);
        }
        Set<String> regions = trackedRegions.get(world);
        if (regions == null) {
            regions = new CopyOnWriteArraySet<String>();
            trackedRegions.put(world, regions);
        }
        return regions.add(id);
    }

    @Override
    public int regionCount() {
        if (trackedRegions.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (String world : trackedRegions.keySet()) {
            Set<String> sets = trackedRegions.get(world);
            if (sets != null) {
                count += sets.size();
            }
        }
        return count;
    }
}
