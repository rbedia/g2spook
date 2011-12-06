package org.trillinux.g2.hub.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import org.trillinux.g2.hub.controller.message.QueryStatsRequest;
import org.trillinux.g2.hub.controller.message.QueryStatsResponse;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SearchStatsPanel extends JPanel {
    private final JTextField queries;
    private final JTextField dna;

    private final Timer timer;
    private final JTextField hash;
    private final JTextField keyword;
    private final JTextField xml;
    private final JTextField sha1;
    private final JTextField md5;
    private final JTextField tth;
    private final JTextField btih;
    private final JTextField ed2k;
    private final JTextField hashOther;
    private final JTextField bitprint;
    private final JTextField dnaPer;
    private final JTextField keywordPer;
    private final JTextField xmlPer;
    private final JTextField hashPer;
    private final JTextField sha1Per;
    private final JTextField bitprintPer;
    private final JTextField md5Per;
    private final JTextField tthPer;
    private final JTextField btihPer;
    private final JTextField ed2kPer;
    private final JTextField hashOtherPer;

    /**
     * Create the panel.
     */
    public SearchStatsPanel() {
        setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.DEFAULT_COLSPEC, ColumnSpec.decode("default:grow"),
                FormFactory.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"), }, new RowSpec[] {
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

        JLabel lblQueries = new JLabel("Queries:");
        add(lblQueries, "1, 2, right, default");

        queries = new JTextField();
        add(queries, "2, 2, fill, default");
        queries.setColumns(10);

        JLabel lblGnucdna = new JLabel("GnucDNA:");
        add(lblGnucdna, "1, 4, right, default");

        dna = new JTextField();
        add(dna, "2, 4, fill, default");
        dna.setColumns(10);

        dnaPer = new JTextField();
        add(dnaPer, "4, 4, fill, default");
        dnaPer.setColumns(10);

        JLabel lblNewLabel = new JLabel("Keyword:");
        add(lblNewLabel, "1, 6, right, default");

        keyword = new JTextField();
        add(keyword, "2, 6, fill, default");
        keyword.setColumns(10);

        keywordPer = new JTextField();
        add(keywordPer, "4, 6, fill, default");
        keywordPer.setColumns(10);

        JLabel lblNewLabel_1 = new JLabel("XML:");
        add(lblNewLabel_1, "1, 8, right, default");

        xml = new JTextField();
        add(xml, "2, 8, fill, default");
        xml.setColumns(10);

        xmlPer = new JTextField();
        add(xmlPer, "4, 8, fill, default");
        xmlPer.setColumns(10);

        JLabel lblHash_1 = new JLabel("Hash");
        add(lblHash_1, "1, 10");

        JLabel lblHash = new JLabel("Hash:");
        add(lblHash, "1, 12, right, default");

        hash = new JTextField();
        add(hash, "2, 12, fill, default");
        hash.setColumns(10);

        hashPer = new JTextField();
        add(hashPer, "4, 12, fill, default");
        hashPer.setColumns(10);

        JLabel lblNewLabel_2 = new JLabel("SHA1:");
        add(lblNewLabel_2, "1, 14, right, default");

        sha1 = new JTextField();
        add(sha1, "2, 14, fill, default");
        sha1.setColumns(10);

        sha1Per = new JTextField();
        add(sha1Per, "4, 14, fill, default");
        sha1Per.setColumns(10);

        JLabel lblBitprint = new JLabel("Bitprint:");
        add(lblBitprint, "1, 16, right, default");

        bitprint = new JTextField();
        add(bitprint, "2, 16, fill, default");
        bitprint.setColumns(10);

        bitprintPer = new JTextField();
        add(bitprintPer, "4, 16, fill, default");
        bitprintPer.setColumns(10);

        JLabel lblNewLabel_3 = new JLabel("MD5:");
        add(lblNewLabel_3, "1, 18, right, default");

        md5 = new JTextField();
        add(md5, "2, 18, fill, default");
        md5.setColumns(10);

        md5Per = new JTextField();
        add(md5Per, "4, 18, fill, default");
        md5Per.setColumns(10);

        JLabel lblNewLabel_4 = new JLabel("TTH:");
        add(lblNewLabel_4, "1, 20, right, default");

        tth = new JTextField();
        add(tth, "2, 20, fill, default");
        tth.setColumns(10);

        tthPer = new JTextField();
        tthPer.setText("");
        add(tthPer, "4, 20, fill, default");
        tthPer.setColumns(10);

        JLabel lblBtih = new JLabel("BTIH:");
        add(lblBtih, "1, 22, right, default");

        btih = new JTextField();
        add(btih, "2, 22, fill, default");
        btih.setColumns(10);

        btihPer = new JTextField();
        add(btihPer, "4, 22, fill, default");
        btihPer.setColumns(10);

        JLabel lblEdk = new JLabel("ed2k:");
        add(lblEdk, "1, 24, right, default");

        ed2k = new JTextField();
        add(ed2k, "2, 24, fill, default");
        ed2k.setColumns(10);

        ed2kPer = new JTextField();
        add(ed2kPer, "4, 24, fill, default");
        ed2kPer.setColumns(10);

        JLabel lblOther = new JLabel("other:");
        add(lblOther, "1, 26, right, default");

        hashOther = new JTextField();
        add(hashOther, "2, 26, fill, default");
        hashOther.setColumns(10);

        hashOtherPer = new JTextField();
        add(hashOtherPer, "4, 26, fill, default");
        hashOtherPer.setColumns(10);

        timer = new Timer(1000, new SendActionListener());
        timer.start();
    }

    private class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            send();
        }

    }

    private void send() {
        ControllerClient.getInstance().sendMessage(new QueryStatsRequest(),
                callback());
    }

    private ClientCallback<QueryStatsResponse> callback() {
        return new ClientCallback<QueryStatsResponse>() {

            @Override
            public void response(Serializable message, Object obj) {
                QueryStatsResponse response = (QueryStatsResponse) obj;
                double queryCount = response.stats.getQueries();
                queries.setText(Long.toString(response.stats.getQueries()));
                dna.setText(Long.toString(response.stats.getDna()));
                hash.setText(Long.toString(response.stats.getHash()));
                keyword.setText(Long.toString(response.stats.getKeyword()));
                xml.setText(Long.toString(response.stats.getXml()));
                sha1.setText(Long.toString(response.stats.getHashSha1()));
                md5.setText(Long.toString(response.stats.getHashMd5()));
                tth.setText(Long.toString(response.stats.getHashTTH()));
                btih.setText(Long.toString(response.stats.getHashBT()));
                ed2k.setText(Long.toString(response.stats.getHashEd2k()));
                bitprint.setText(Long.toString(response.stats.getHashBp()));
                hashOther.setText(Long.toString(response.stats.getHashOther()));

                dnaPer.setText(getPercent(response.stats.getDna(), queryCount));
                hashPer.setText(getPercent(response.stats.getHash(), queryCount));
                keywordPer.setText(getPercent(response.stats.getKeyword(),
                        queryCount));
                xmlPer.setText(getPercent(response.stats.getXml(), queryCount));
                sha1Per.setText(getPercent(response.stats.getHashSha1(),
                        queryCount));
                md5Per.setText(getPercent(response.stats.getHashMd5(),
                        queryCount));
                tthPer.setText(getPercent(response.stats.getHashTTH(),
                        queryCount));
                btihPer.setText(getPercent(response.stats.getHashBT(),
                        queryCount));
                ed2kPer.setText(getPercent(response.stats.getHashEd2k(),
                        queryCount));
                bitprintPer.setText(getPercent(response.stats.getHashBp(),
                        queryCount));
                hashOtherPer.setText(getPercent(response.stats.getHashOther(),
                        queryCount));
            }
        };
    }

    private String getPercent(double numer, double denom) {
        return String.format("%2.2f", numer / denom * 100.0d);
    }
}
