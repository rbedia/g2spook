package org.trillinux.g2.hub.controller.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.trillinux.g2.hub.packet.QueryPacket;

public class QueryResponse implements Serializable {
    public List<QueryPacket> queries = new ArrayList<QueryPacket>();
}
