package ru.dargen.board.impl;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.server.v1_12_R1.EnumDirection;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapPalette;
import ru.dargen.board.BannerBoard;
import ru.dargen.board.BoardAPI;
import ru.dargen.board.BoardRenderer;
import ru.dargen.board.Fragment;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class SimpleBannerBoard implements BannerBoard {

    private final Fragment[][] fragments;
    private final BoardRenderer renderer;
    private final int rows, columns;

    private Location location;
    private EnumDirection direction;
    private BiConsumer<PacketPlayInUseEntity.EnumEntityUseAction, Player> interactListener;

    private final Set<Player> recipients;

    public SimpleBannerBoard(Location location, EnumDirection direction, int columns, int rows) {
        if (columns == 0 || rows == 0)
            throw new IllegalArgumentException("Rows or Columns must > 0");

        this.columns = columns;
        this.rows = rows;
        this.direction = direction;
        this.location = location;

        fragments = new Fragment[rows][columns];
        renderer = new BoardRenderer(new BufferedImage(columns * 128, rows * 128, 6));
        recipients = new ConcurrentSet<>();

        teleport(location);

        updateFragments();
    }

    public void updateFragments() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(BoardAPI.getApi().getPlugin(), () -> {
            byte[] buffer = MapPalette.imageToBytes(renderer.getBufferedImage());
            for (Fragment[] fragment : fragments)
                for (Fragment value : fragment) value.updateMap(buffer);
            broadcastUpdate();
        }, 1);
    }

    public List<Fragment> getFragmentsAsList() {
        List<Fragment> fragments = new ArrayList<>();
        for (Fragment[] row : this.fragments)
            fragments.addAll(Arrays.asList(row));
        return fragments;
    }

    public List<Integer> getFragmentsEntityIds() {
        return getFragmentsAsList().stream().map(Fragment::getEntityId).collect(Collectors.toList());
    }

    public boolean canReceive(Player player) {
        return true;
    }

    public void spawn(Player player) {
        recipients.add(player);
        for (Fragment[] row : fragments)
            for (Fragment fragment : row)
                if (fragment != null)
                    fragment.spawn(player);
    }

    public void broadcastSpawn() {
        location.getWorld().getPlayers().forEach(this::spawn);
    }

    public void destroy(Player player) {
        recipients.remove(player);
        for (Fragment[] row : fragments)
            for (Fragment fragment : row)
                if (fragment != null)
                    fragment.destroy(player);
    }

    public void broadcastDestroy() {
        location.getWorld().getPlayers().forEach(this::destroy);
    }

    public void update(Player player) {
        for (Fragment[] row : fragments)
            for (Fragment fragment : row)
                if (fragment != null)
                    fragment.update(player);
    }

    public void broadcastUpdate() {
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    public void teleport(Location location) {
        broadcastDestroy();
        for (int row = 0; row < rows; row++)
            for (int column = 0; column < columns; column++)
                if (fragments[row][column] == null)
                    createFragment(location, column, row);
                else
                    fragments[row][column].teleport(locationForFragment(location, column, row));
        this.location = location;
    }

    public void setDirection(EnumDirection direction) {
        if (direction == EnumDirection.UP || direction == EnumDirection.DOWN)
            throw new IllegalStateException("direction UP, DOWN not supported");
        broadcastDestroy();
        for (int row = 0; row < rows; row++)
            for (int column = 0; column < columns; column++)
                if (fragments[row][column] == null)
                    createFragment(location, column, row);
                else
                    fragments[row][column].setDirection(direction);
        this.direction = direction;
    }

    public void createFragment(Location location, int column, int row) {
        if (direction == EnumDirection.UP || direction == EnumDirection.DOWN)
            throw new IllegalStateException("direction UP, DOWN not supported");
        Location loc = locationForFragment(location, column, row);
        fragments[row][column] = new FragmentImpl(loc, direction, this, column, row);
    }

    public Location locationForFragment(Location location, int column, int row) {
        Location loc = location.clone();
        switch (direction) {
            case NORTH:
                loc.add(-column, -row, 0);
                break;
            case WEST:
                loc.add(0, -row, column);
                break;
            case EAST:
                loc.add(0, -row, -column);
                break;
            case SOUTH:
                loc.add(column, -row, 0);
                break;
        }
        return loc;
    }

    public Set<Player> getRecipients() {
        return recipients;
    }

    public BiConsumer<PacketPlayInUseEntity.EnumEntityUseAction, Player> getInteractListener() {
        return interactListener;
    }

    public void setInteractListener(BiConsumer<PacketPlayInUseEntity.EnumEntityUseAction, Player> interactListener) {
        this.interactListener = interactListener;
    }


    public BoardRenderer getRenderer() {
        return renderer;
    }


    public Fragment[][] getFragments() {
        return fragments;
    }

    public Location getLocation() {
        return location;
    }

    public EnumDirection getDirection() {
        return direction;
    }
}
