/**
 * Copyright 2011 Rafael Bedia
 * 
 * This file is part of g2spook.
 * 
 * g2spook is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * g2spook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with g2spook.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.trillinux.g2.core;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Provides a global Timer for all classes to use.
 * 
 * @author Rafael Bedia
 * 
 */
public class TimerManager {
    private static final TimerManager INSTANCE = new TimerManager();

    private final Timer timer;

    private TimerManager() {
        timer = new Timer();
    }

    private static TimerManager getInstance() {
        return INSTANCE;
    }

    /**
     * Schedules a task to occur once after a delay.
     * 
     * @param task
     * @param delay
     *            delay in milliseconds before task is to be executed.
     */
    public static void scheduleOnce(TimerTask task, long delay) {
        getInstance().timer.schedule(task, delay);
    }

    /**
     * Schedules a task to occur repeatedly. The first execution will be after
     * period time.
     * 
     * @param task
     * @param period
     *            time in milliseconds between successive task executions.
     */
    public static void schedule(TimerTask task, long period) {
        getInstance().timer.schedule(task, period, period);
    }

    /**
     * Schedules a task to occur repeatedly. The first execution will be after
     * delay time. Subsequent executions will occur after period time.
     * 
     * @param task
     * @param period
     *            time in milliseconds between successive task executions.
     */
    public static void schedule(TimerTask task, long delay, long period) {
        getInstance().timer.schedule(task, delay, period);
    }

    /**
     * Stops the Timer.
     */
    public static void shutdown() {
        getInstance().timer.cancel();
    }
}
