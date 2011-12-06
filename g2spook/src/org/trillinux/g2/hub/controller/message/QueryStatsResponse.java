package org.trillinux.g2.hub.controller.message;

import java.io.Serializable;

import org.trillinux.g2.hub.stats.QueryStats;

public class QueryStatsResponse implements Serializable {
    public QueryStats stats;
}
