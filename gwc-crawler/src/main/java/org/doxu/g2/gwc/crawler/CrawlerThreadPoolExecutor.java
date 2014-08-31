/**
 * Copyright 2014 Rafael Bedia
 *
 * This file is part of g2spook.
 *
 * g2spook is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * g2spook is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * g2spook. If not, see <http://www.gnu.org/licenses/>.
 */
package org.doxu.g2.gwc.crawler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class extends the capabilities of the ThreadPoolExecutor with the
 * ability to receive events when the thread pool becomes idle.
 *
 */
public class CrawlerThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean usedOnce;

    private IdleListener listener;

    public CrawlerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        usedOnce = false;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        usedOnce = true;
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (listener != null) {
            long activeCount = getTaskCount() - getCompletedTaskCount();
            if (activeCount == 1 && usedOnce) {
                listener.idle();
            }
        }
    }

    public void setListener(IdleListener listener) {
        this.listener = listener;
    }
}
