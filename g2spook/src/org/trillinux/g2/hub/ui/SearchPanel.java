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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.TableColumnModel;

import org.trillinux.g2.hub.controller.message.QueryRequest;
import org.trillinux.g2.hub.controller.message.QueryResponse;
import org.trillinux.g2.hub.packet.QueryPacket;

public class SearchPanel extends JPanel {

    private final QueryTableModel model;
    private final JScrollPane scrollPane;
    private final JTable table;

    private final Timer timer;

    /**
     * Create the panel.
     */
    public SearchPanel() {
        setLayout(new BorderLayout(0, 0));

        model = new QueryTableModel();

        scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        table = new JTable(model);
        setColumnWidths(table.getColumnModel());
        scrollPane.setViewportView(table);

        timer = new Timer(1000, new SendActionListener());
        timer.start();
    }

    private void setColumnWidths(TableColumnModel model) {
        model.getColumn(1).setMaxWidth(200);
        model.getColumn(1).setMinWidth(170);
        model.getColumn(2).setMaxWidth(80);
        model.getColumn(2).setMinWidth(80);
    }

    private class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            send();
        }

    }

    private void send() {
        ControllerClient.getInstance().sendMessage(new QueryRequest(),
                searchCallback());
    }

    private ClientCallback<QueryResponse> searchCallback() {
        return new ClientCallback<QueryResponse>() {

            @Override
            public void response(Serializable message, Object obj) {
                QueryResponse response = (QueryResponse) obj;
                List<QueryPacket> queries = new ArrayList<QueryPacket>();
                for (QueryPacket query : response.queries) {
                    if (query.getDn() != null && !query.getDn().isEmpty()) {
                        queries.add(query);
                    }
                }
                model.update(queries);
            }
        };
    }
}
