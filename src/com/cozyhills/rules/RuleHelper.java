package com.cozyhills.rules;

import com.cozyhills.cozy.StateHolder;
import com.cozyhills.cozy.Util;
import com.cozyhills.things.*;
import com.cozyhills.things.buildings.Home;
import com.cozyhills.things.items.Item;
import com.cozyhills.things.resources.Resource;
import com.cozyhills.things.resources.Rock;
import com.cozyhills.things.resources.Tree;

import java.util.*;
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

    protected static double range(VisibleEntity visibleEntity, VisibleEntity me) {
        return Math.sqrt(Math.pow((visibleEntity.xy[0] - me.xy[0]), 2) + Math.pow((visibleEntity.xy[1] - me.xy[1]), 2));
    }

    protected static double rangeSimplified(VisibleEntity visibleEntity, VisibleEntity me) {
        return Math.pow((visibleEntity.xy[0] - me.xy[0]), 2) + Math.pow((visibleEntity.xy[1] - me.xy[1]), 2);
    }

    protected static int[] centroid(Set<VisibleEntity> visibleEntityList) {
        int[] centroid = { 0, 0 };

        for (VisibleEntity visibleEntity: visibleEntityList) {
            centroid[0] += visibleEntity.xy[0];
            centroid[1] += visibleEntity.xy[1];
        }

        int totalPoints = visibleEntityList.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return centroid;
    }

    protected static int[] randomDestination(Person me, final int DISTANCE) {
        int r1 = 1 - ThreadLocalRandom.current().nextInt(0, 2 + 1);
        int r2 = 1 - ThreadLocalRandom.current().nextInt(0, 2 + 1);
        return new int[]{me.xy[0] + DISTANCE * r1, me.xy[1] + DISTANCE * r2};
    }

    protected Optional<Rock> getClosestVisibleRock(Person me, final int VISIBLE_ZONE) {
        return (Optional<Rock>)getClosestVisibleEntity(me, VISIBLE_ZONE, Rock.class);
    }

    protected Optional<Tree> getClosestVisibleTree(Person me, final int VISIBLE_ZONE) {
        return (Optional<Tree>)getClosestVisibleEntity(me, VISIBLE_ZONE, Tree.class);
    }

    protected Optional<Home> getClosestVisibleHome(Person me, final int VISIBLE_ZONE) {
        return (Optional<Home>)getClosestVisibleEntity(me, VISIBLE_ZONE, Home.class);
    }

    protected Optional<Item> getClosestVisibleItem(Person me, final int VISIBLE_ZONE, Map<Class<?>, Integer> itemTypes) {
        Util.print("Not implemented yet: getClosestVisibleItem");
        for (Class classType : itemTypes.keySet()) {
            Optional<Item> items = (Optional<Item>)getClosestVisibleEntity(me, VISIBLE_ZONE, classType);
        }
        return Optional.empty();
    }

    protected Optional<Resource> getClosestVisibleResource(Person me, int visibleZone, Map<Class<?>, Integer> resourceTypes) {
        Util.print("Not implemented yet: getClosestVisibleResource");
        return Optional.empty();
    }

    protected Optional<Home> getClosestUnvisitedVisibleHome(Person me, final int VISIBLE_ZONE) {
        return getHomes().stream().parallel()
                .filter(home -> !me.getVisitedHomes().contains(home)) //Unvisited
                .min(Comparator.comparingDouble(home -> rangeSimplified(me, home))) //Closest
                .map(result -> range(me, result) <= VISIBLE_ZONE ? result : null); //Visible
    }

    private Optional<? extends VisibleEntity> getClosestVisibleEntity(Person me, final int VISIBLE_ZONE, Class<?> type) {
        return getEntityList(type).stream().parallel()
                .min(Comparator.comparingDouble(entity -> rangeSimplified(me, (VisibleEntity) entity))) //Closest
                .map(result -> range(me, result) <= VISIBLE_ZONE ? result : null); //Visible
    }

    private Set<? extends VisibleEntity> getEntityList(Class<?> entity) {
        return StateHolder.instance().getEntities(entity);
    }

    protected Set<Person> getPersons() {
        return StateHolder.instance().getPersons();
    }

    protected Set<Home> getHomes() {
        return StateHolder.instance().getHomes();
    }

    protected Set<Tree> getTrees() {
        return StateHolder.instance().getTrees();
    }

    protected Set<Rock> getRocks() {
        return StateHolder.instance().getRocks();
    }
}
