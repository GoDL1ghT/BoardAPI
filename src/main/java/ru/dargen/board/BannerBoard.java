package ru.dargen.board;

import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.dargen.board.impl.PrivateBannerBoard;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public interface BannerBoard extends Livable {

    /**
     * @return масив фрагметов борда
     */
    Fragment[][] getFragments();

    /**
     * @return список фрагметов борда
     */
    List<Fragment> getFragmentsAsList();

    /**
     * @return список entityId фрагметов борда
     */
    List<Integer> getFragmentsEntityIds();

    /**
     * @return игроки видящее борд сейчас
     */
    Set<Player> getRecipients();

    /**
     * @param player игрок для проверки
     * @return может ли игрок видеть борд для {@link PrivateBannerBoard}
     */
    boolean canReceive(Player player);

    /**
     * @return утилита для рендера на борде
     */
    BoardRenderer getRenderer();

    /**
     * Обновление карт на фрагментах
     * т.е обновление борда
     * */
    void updateFragments();

    /**
     * Установка нового фрагмента
     * по кординатам
     *
     * @param location локация для фрагмента
     * @param column x фрагмента
     * @param row y фрагмента
     */
    void createFragment(Location location, int column, int row);

    /**
     * Нормально работает с направлением SOUTH и NORTH
     * из за бага с хитбоксами рамки
     *
     * @param listener слушатель кликов по рамке
     * */
    void setInteractListener(BiConsumer<PacketPlayInUseEntity.EnumEntityUseAction, Player> listener);

    /**
     * @return слушатель кликов
     */
    BiConsumer<PacketPlayInUseEntity.EnumEntityUseAction, Player> getInteractListener();

}
