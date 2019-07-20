package org.battleplugins.worldguardutil.region;

import org.bukkit.World;

public class ProtectedArenaRegion {

    protected String id;
    protected World world;

    public ProtectedArenaRegion() { }

    public ProtectedArenaRegion(String id, World world) {
        this.id = id;
        this.world = world;
    }

    public String getID() {
        return id;
    }

    public World getWorld() {
        return world;
    }
}
