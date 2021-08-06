package ru.dargen.board.impl;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.dargen.board.BannerBoard;
import ru.dargen.board.Fragment;

public class FragmentImpl extends EntityItemFrame implements Fragment {

    private final short mapId = Fragment.getNextId();
    private final int column, row;
    private final BannerBoard owner;

    private Location location;
    private EnumDirection direction;

    private PacketPlayOutSpawnEntity spawnPacket;
    private PacketPlayOutEntityDestroy destroyPacket;
    private PacketPlayOutEntityMetadata metadataPacket;
    private PacketPlayOutMap mapPacket;

    FragmentImpl(Location location, EnumDirection direction, BannerBoard board, int column, int row) {
        super(((CraftWorld) location.getWorld()).getHandle(),
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), direction);

        this.owner = board;
        this.column = column;
        this.row = row;

        setInvisible(true);
        setInvulnerable(true);
        setNoGravity(true);

        DataWatcher dataWatcher = getDataWatcher();
        dataWatcher.set(DataWatcherRegistry.f.a(6), CraftItemStack.asNMSCopy(new ItemStack(Material.MAP, 1, mapId)));
        dataWatcher.set(DataWatcherRegistry.b.a(7), 0);

        spawnPacket = new PacketPlayOutSpawnEntity(this, 71);
        destroyPacket = new PacketPlayOutEntityDestroy(getId());
        metadataPacket = new PacketPlayOutEntityMetadata(getId(), dataWatcher, true);
    }

    public BannerBoard getOwner() {
        return owner;
    }

    public void updateMap(byte[] bytes) {
        int width = owner.getRenderer().getBufferedImage().getWidth();
        byte[] buffer = new byte[16384];
        for (int x = column * 128; x < column * 128 + 128; x++)
            for (int y = row * 128; y < row * 128 + 128; y++)
                buffer[(y - row * 128) * 128 + (x - column * 128)] = bytes[y * width + x];

        mapPacket = new PacketPlayOutMap(mapId, ((byte) 3), true,
                Lists.newArrayList(), buffer, 0, 0, 128, 128);
    }

    public short getMapId() {
        return mapId;
    }

    public int getEntityId() {
        return getId();
    }

    public void spawn(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawnPacket);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadataPacket);
    }

    public void broadcastSpawn() {
        location.getWorld().getPlayers().forEach(this::spawn);
    }

    public void destroy(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyPacket);
    }

    public void broadcastDestroy() {
        location.getWorld().getPlayers().forEach(this::destroy);
    }

    public void update(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(mapPacket);
    }

    public void broadcastUpdate() {
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    public Location getLocation() {
        return location;
    }

    public void teleport(Location location) {
        this.location = location;
        super.setPosition(location.getX(), location.getY(), location.getZ());
        spawnPacket = new PacketPlayOutSpawnEntity(this, 71);
    }

    public void setDirection(EnumDirection direction) {
        this.direction = direction;
        super.setDirection(direction);
        spawnPacket = new PacketPlayOutSpawnEntity(this, 71);
    }

}
