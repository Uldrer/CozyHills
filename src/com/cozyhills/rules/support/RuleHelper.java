package com.cozyhills.rules.support;

import com.cozyhills.cozy.StateHolder;
import com.cozyhills.cozy.Util;
import com.cozyhills.model.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by pere5 on 02/01/16.
 */
public abstract class RuleHelper implements Rule {

    private final int rank;
    protected final int id;

    public RuleHelper(int rank) {
        this.rank = rank;
        this.id = Integer.MAX_VALUE - rank;
    }

    @Override
    public int rank() {
        return rank;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void printInfo(int status) {
        Util.print(id + ":" + status);
    }

    protected int range(VisibleEntity visibleEntity, VisibleEntity me) {
        return (int)Math.sqrt(Math.pow((visibleEntity.x - me.x), 2) + Math.pow((visibleEntity.y - me.y), 2));
    }

    protected int[] centroid(List<VisibleEntity> visibleEntityList) {
        int[] centroid = { 0, 0 };

        for (VisibleEntity visibleEntity: visibleEntityList) {
            centroid[0] += visibleEntity.x;
            centroid[1] += visibleEntity.y;
        }

        int totalPoints = visibleEntityList.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return centroid;
    }

    protected int[] randomDestination(Person me, int distance) {
        int r1 = 1 - ThreadLocalRandom.current().nextInt(0, 2 + 1);
        int r2 = 1 - ThreadLocalRandom.current().nextInt(0, 2 + 1);
        return new int[]{me.x + distance * r1, me.y + distance * r2};
    }

    protected Rock getClosestVisibleRock(Person me, final int VISIBLE_ZONE) {
        return (Rock)getClosestVisibleEntity(me, VISIBLE_ZONE, StateHolder.ROCKS);
    }

    protected Tree getClosestVisibleTree(Person me, final int VISIBLE_ZONE) {
        return (Tree)getClosestVisibleEntity(me, VISIBLE_ZONE, StateHolder.TREES);
    }

    private VisibleEntity getClosestVisibleEntity(Person me, final int VISIBLE_ZONE, String type) {
        VisibleEntity closestEntity = null;
        Integer closestRange = Integer.MAX_VALUE;
        for (VisibleEntity entity: getEntityList(type)) {
            int range = range(me, entity);
            if (range < closestRange) {
                closestRange = range;
                if (closestRange < VISIBLE_ZONE) {
                    closestEntity = entity;
                }
            }
        }
        return closestEntity;
    }

    private List<? extends VisibleEntity> getEntityList(String entity) {
        return StateHolder.instance().getEntities(entity);
    }

    protected List<Person> getPersons() {
        return StateHolder.instance().getPersons();
    }

    protected List<Home> getHomes() {
        return StateHolder.instance().getHomes();
    }

    protected List<Tree> getTrees() {
        return StateHolder.instance().getTrees();
    }

    protected List<Rock> getRocks() {
        return StateHolder.instance().getRocks();
    }
}
