package com.cozyhills.rules;

import com.cozyhills.actions.Action;
import com.cozyhills.actions.Path;
import com.cozyhills.things.Person;
import com.cozyhills.things.VisibleEntity;

import java.util.Queue;
import java.util.Set;

/**
 * Created by pere5 on 02/01/16.
 */
public class CozyUp extends RuleHelper {

    private static final int COMFORT_ZONE = 25;
    private static final int VISIBLE_ZONE = 80;
    private static final int WALK_DISTANCE = 20;
    private static final int COZY_GROUP = 4;

    public CozyUp(int rank) {
        super(rank);
    }

    @Override
    public int calculateStatus(Person me) {
        int result = 0;
        me.clearTarget();
        for (Person person: getPersons()) {
            double range = person != me ? range(person, me): Double.MAX_VALUE;
            if (range < COMFORT_ZONE) {
                result += 1;
            }
            if (range < VISIBLE_ZONE) {
                me.addTarget(person);
            }
        }
        final int ME = 1;
        final int MARGIN = 1;
        result -= (COZY_GROUP - ME - MARGIN);
        if (result > 0) {
            me.safeSpot(me.xy);
            return result;
        } else {
            return 0;
        }
    }

    @Override
    public void initWork(Person me, int status) {
        Queue<Action> actionQueue = me.getActionQueue();
        Set<VisibleEntity> targets = me.getTargets();
        int[] destination;
        if (targets.size() == 0) {
            destination = randomDestination(me, WALK_DISTANCE);
        } else if (status == 0) {
            destination = centroid(targets);
            if (me.xy[0] == destination[0] && me.xy[1] == destination[1]) {
                destination = randomDestination(me, WALK_DISTANCE);
            }
        } else {
            destination = centroid(targets);
        }
        Action path = new Path(me.xy, destination);
        actionQueue.add(path);
    }
}
