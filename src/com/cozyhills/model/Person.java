package com.cozyhills.model;

import com.cozyhills.actions.Action;
import com.cozyhills.cozy.Util;
import com.cozyhills.items.Item;
import com.cozyhills.rules.support.Rule;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by pere5 on 21/12/15.
 */
public class Person extends VisibleEntity {

    private final List<VisibleEntity> targets = new ArrayList<>();
    private final Map<Class<?>, Integer> resources = new HashMap<>();
    private final Queue<Action> actionQueue = new LinkedList<>();
    private Home home = new Home();
    private Rule currentRule = null;
    private int searchForHome = 2;

    public Person () {
        setDefaults();
        this.x = Util.generateWidth();
        this.y = Util.generateHeight();
    }

    public Person (int x, int y) {
        setDefaults();
        this.x = x;
        this.y = y;
    }

    private void setDefaults() {
        this.size = 3;
        this.color = Color.BLACK;
    }

    public boolean working() {
        return actionQueue.size() > 0;
    }

    public void work() {
        boolean continueWorking = actionQueue.peek().doIt(this);
        if (!continueWorking) {
            Action discard = actionQueue.poll();
        }
    }

    public void startWorking(Rule selectedRule, int status) {
        currentRule = selectedRule;
        selectedRule.initWork(this, status);
    }

    public void addTarget(VisibleEntity visibleEntity) {
        targets.add(visibleEntity);
    }

    public void clearTarget() {
        targets.clear();
    }

    public List<VisibleEntity> getTargets() {
        return targets;
    }

    public Home getHome() {
        return home;
    }

    public Rule getCurrentRule () {
        return currentRule;
    }

    public boolean hasEnoughResources(Map<Class<?>, Integer> items) {
        for (Map.Entry<Class<?>, Integer> itemType: items.entrySet()) {
            if (resources.containsKey(itemType.getKey()) && resources.get(itemType.getKey()).compareTo(itemType.getValue()) >= 0) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean searchForHome() {
        searchForHome--;
        return searchForHome >= 0;
    }

    public Queue<Action> getActionQueue() {
        return actionQueue;
    }
}
