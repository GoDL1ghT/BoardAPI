package ru.dargen.board;

import net.minecraft.server.v1_12_R1.EntityItemFrame;

import java.util.concurrent.atomic.AtomicInteger;

public interface Fragment extends Livable {

    AtomicInteger nextId = new AtomicInteger(Short.MAX_VALUE);

    static short getNextId() {
        return (short) nextId.getAndDecrement();
    }

    /**
     * @return борд которому принадлежит этот фрагмент
     */
    BannerBoard getOwner();

    /**
     * Обновления карты фрагмента
     *
     * @param buffer бафер всей картинки борда
     */
    void updateMap(byte[] buffer);

    /**
     * @return id карты, которых присвоен этому фрагменту
     */
    short getMapId();

    /**
     * @return id {@link EntityItemFrame} этого фрагмента
     */
    int getEntityId();

}
