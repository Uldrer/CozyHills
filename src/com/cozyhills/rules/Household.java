package com.cozyhills.rules;

import com.cozyhills.actions.*;
import com.cozyhills.cozy.Util;
import com.cozyhills.things.buildings.BasicHut;
import com.cozyhills.things.buildings.Home;
import com.cozyhills.things.Person;
import com.cozyhills.things.items.Item;
import com.cozyhills.things.resources.Resource;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * Created by pere5 on 02/01/16.
 */
public class Household extends RuleHelper {

    private static final int NEIGHBORHOOD_ZONE = 120;
    private static final int VISIBLE_ZONE = 80;

    public Household(int rank) {
        super(rank);
    }

    @Override
    public int calculateStatus(Person me) {
        Optional<Home> myHome = me.getHome();
        if (myHome.isPresent()) {
            int result = 0;
            for (Home home: getHomes()) {
                double range = range(home, myHome.get()); //include my own home
                if (range < NEIGHBORHOOD_ZONE) {
                    result += home.getStatus();
                }
            }
            return result;
        } else {
            return 0;
        }
    }

    @Override
    public void initWork(Person me, int status) {
        Queue<Action> actionQueue = me.getActionQueue();
        if (me.getHome().isPresent()) {
            //improve home: end
            actionQueue.add(new Wait(10));
            Util.print("NOT IMPLEMENTED, improve home!");
        } else if (me.searchForHome()) {
            Optional<Home> home = getClosestUnvisitedVisibleHome(me, VISIBLE_ZONE);
            if (home.isPresent()) {
                actionQueue.add(new Path(me.xy, home.get().xy));
                actionQueue.add(new MoveIn(home.get()));
            } else {
                actionQueue.add(new Path(me.xy, randomDestination(me, VISIBLE_ZONE / 2)));
            }
        } else {
            Optional<Item> item = me.carryingAnItem(BasicHut.buildCost());
            if (item.isPresent()) {
                if (me.getSafeSpot().isPresent()) {
                    actionQueue.add(new Path(me.xy, me.getSafeSpot().get()));
                    actionQueue.add(new Build(BasicHut.class, item.get()));
                } else {
                    actionQueue.add(new Build(BasicHut.class, item.get()));
                }
            } else {
                item = getClosestVisibleItem(me, VISIBLE_ZONE, BasicHut.buildCost());
                if (item.isPresent()) {
                    actionQueue.add(new Path(me.xy, item.get().xy));
                    actionQueue.add(new PickUp(item.get()));
                } else {
                    Optional<Resource> resource = getClosestVisibleResource(me, VISIBLE_ZONE, BasicHut.buildCost());
                }
            }
        }
    }
}

/*
                //build
        Tree closestTree = getClosestVisibleTree(me, VISIBLE_ZONE);
        if (closestTree == null) {
            int[] destination;
            destination = randomDestination(me, VISIBLE_ZONE);
            actionQueue.add(new Path(me.xy, destination));
        } else {
            actionQueue.add(new Path(me.xy, closestTree.xy));
            actionQueue.add(new CutTree(closestTree));
            actionQueue.add(new Path(closestTree.xy, me.xy));
        }
    }
}
*/
