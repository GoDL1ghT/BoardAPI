package ru.dargen.board;

import net.minecraft.server.v1_12_R1.EnumDirection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Livable {

    /**
     * Спавн объекта
     * @param player игрок получающих пакет спавна
     */
    void spawn(Player player);

    /**
     * Спавн объекта всем игрокам мира
     */
    void broadcastSpawn();

    /**
     * Уничтожение объекта
     * @param player игрок получающих пакет уничтожения
     */
    void destroy(Player player);

    /**
     * Уничтожение объекта всем игрокам мира
     */
    void broadcastDestroy();

    /**
     * Обновление объекта
     * @param player игрок получающих пакет обновления
     */
    void update(Player player);

    /**
     * Обновление объекта всем игрокам
     */
    void broadcastUpdate();

    /**
     * @return локация текущего объекта
     */
    Location getLocation();

    /**
     * Телепортация на новую локацию
     * @param location локация
     */
    void teleport(Location location);

    /**
     * @return текущее направление
     */
    EnumDirection getDirection();

    /**
     * Установка направления
     * @param direction направление
     */
    void setDirection(EnumDirection direction);

}
