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
package org.trillinux.g2.hub.workers;

import java.util.TimerTask;

import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * A task that is associated with a channel context. When the task is executed
 * it will delegate execution to the abstract exec() method. If the channel
 * associated with this channel context is disconnected then this task will not
 * call exec() and instead the task will be cancelled.
 * 
 * @author Rafael Bedia
 */
public abstract class WorkerTask extends TimerTask {
    protected final ChannelHandlerContext ctx;

    public WorkerTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    /**
     * The task to execute. It will only be executed if the channel associated
     * with this channel context is still connected.
     */
    protected abstract void exec();

    @Override
    public void run() {
        // If the channel is disconnected then cancel the task.
        if (ctx.getChannel().isConnected()) {
            exec();
        } else {
            cancel();
        }

    }
}
