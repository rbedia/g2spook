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
package org.trillinux.g2.hub.controller;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.trillinux.g2.hub.controller.message.QueryRequest;
import org.trillinux.g2.hub.controller.message.QueryResponse;
import org.trillinux.g2.hub.controller.message.QueryStatsRequest;
import org.trillinux.g2.hub.controller.message.QueryStatsResponse;
import org.trillinux.g2.hub.stats.QueryLogger;

public class ControllerServerHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // Echo back the received object to the client.
        // e.getChannel().write(e.getMessage());
        if (e.getMessage() instanceof QueryRequest) {
            QueryResponse response = new QueryResponse();
            response.queries.addAll(QueryLogger.getInstance().getList());
            e.getChannel().write(response);
        } else if (e.getMessage() instanceof QueryStatsRequest) {
            QueryStatsResponse response = new QueryStatsResponse();
            response.stats = QueryLogger.getInstance().getStats();
            e.getChannel().write(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}