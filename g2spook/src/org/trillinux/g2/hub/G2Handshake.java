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
package org.trillinux.g2.hub;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class G2Handshake {
    public enum Type {
        REQUEST, RESPONSE, RESPONSE2
    };

    Type type;

    int status;

    String statusMessage;

    Map<String, String> headers;

    public G2Handshake() {
        type = Type.REQUEST;
        status = 0;
        statusMessage = "";
        headers = new HashMap<String, String>();
    }

    public boolean decode(G2Handshake.Type expType, byte[] buf) {
        StringReader in = new StringReader(new String(buf));
        BufferedReader reader = new BufferedReader(in);

        type = expType;

        String line = null;
        try {
            line = reader.readLine();
            if (line == null) {
                System.out.println("No first line");
                return false;
            }

            if (!decodeFirstLine(expType, line)) {
                return false;
            }

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    return true;
                }
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    headers.put(parts[0].trim(), parts[1].trim());
                } else {
                    System.out.println("Bad header");
                    return false;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean decodeFirstLine(G2Handshake.Type expType, String line) {
        switch (expType) {
        case REQUEST:
            if (!line.equals("GNUTELLA CONNECT/0.6")) {
                System.out.println("Not a Gnutella handshake");
                return false;
            }
            break;
        case RESPONSE:
        case RESPONSE2:
            String[] parts = line.split(" ", 3);
            if (parts.length != 3 || !parts[0].equals("GNUTELLA/0.6")) {
                System.out.println("Not a Gnutella handshake");
                return false;
            }

            status = Integer.parseInt(parts[1]);
            statusMessage = parts[2];
            break;
        }

        return true;
    }

    public byte[] encode() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);

        switch (type) {
        case REQUEST:
            writer.write("GNUTELLA CONNECT/0.6\r\n");
            break;
        case RESPONSE:
            writer.write("GNUTELLA/0.6 " + status + " " + statusMessage
                    + "\r\n");
            break;
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        writer.write("\r\n");

        writer.close();
        return out.toByteArray();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

}
