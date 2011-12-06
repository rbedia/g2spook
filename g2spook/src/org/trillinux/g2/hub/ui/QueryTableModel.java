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
package org.trillinux.g2.hub.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.trillinux.g2.hub.packet.QueryPacket;
import org.trillinux.geoip.GeoIP;

/**
 * @author Rafael Bedia
 * 
 */
public class QueryTableModel extends DefaultTableModel {

    private final List<QueryPacket> queries;

    private final Column[] columns;

    public QueryTableModel() {
        this.queries = new ArrayList<QueryPacket>();
        columns = new Column[7];
        columns[0] = new Column("Query", String.class);
        columns[1] = new Column("Return address", String.class);
        columns[2] = new Column("Country", String.class);
        columns[3] = new Column("GnucDNA", Boolean.class);
        columns[4] = new Column("GUID", Object.class);
        columns[5] = new Column("XML", String.class);
        columns[6] = new Column("Interests", String.class);
    }

    public void update(List<QueryPacket> queries) {
        this.queries.clear();
        this.queries.addAll(queries);
        fireTableDataChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        if (col < columns.length) {
            return columns[col].name;
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col < columns.length) {
            return columns[col].type;
        }
        return Object.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        if (queries == null) {
            return 0;
        }
        return queries.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int col) {
        QueryPacket query = queries.get(row);
        switch (col) {
        case 0:
            return query.getDn();
        case 1:
            return query.getRetAddress();
        case 2:
            if (query.getRetAddress() != null) {
                return GeoIP.getCode(query.getRetAddress().getIp());
            }
            return "";
        case 3:
            return query.isDna();
        case 4:
            return toHexString(query.getGuid());
        case 5:
            return query.getMd();
        case 6:
            return combine(",", query.getInterests());
        }
        return null;
    }

    private String combine(String glue, String... s) {
        int k = s.length;
        if (k == 0) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        out.append(s[0]);
        for (int x = 1; x < k; ++x) {
            out.append(glue).append(s[x]);
        }
        return out.toString();
    }

    private String toHexString(byte[] input) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < input.length; i++) {
            hexString.append(String.format("%02x", 0xFF & input[i]));
        }
        return hexString.toString();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    private static class Column {
        String name;
        Class<?> type;

        /**
         * @param name
         * @param type
         */
        Column(String name, Class<?> type) {
            super();
            this.name = name;
            this.type = type;
        }
    }
}
