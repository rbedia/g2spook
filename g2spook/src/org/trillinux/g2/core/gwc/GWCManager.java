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
package org.trillinux.g2.core.gwc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class GWCManager {

    private static final String GET_TEMPLATE = "%s?get=1&client=DCAT1.0&net=gnutella2";

    public GWCManager() {

    }

    public GWCQueryResponse get(String address) throws URISyntaxException,
            MalformedURLException, IOException {
        GWCQueryResponse query = new GWCQueryResponse();
        URI uri = new URI(String.format(GET_TEMPLATE, address));
        URLConnection uc = uri.toURL().openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                uc.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            String[] parts = parse(inputLine);
            if (parts.length < 2) {
                continue;
            }
            if (parts[0].equals("h")) {
                query.getHosts().add(parts[1]);
            } else if (parts[0].equals("u")) {
                query.getGwcs().add(parts[1]);
            }
        }
        in.close();

        return query;
    }

    public String[] parse(String input) {
        return input.split("\\|");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        GWCManager manager = new GWCManager();
        try {
            String gwc = "http://cache.trillinux.org/g2/bazooka.php";
            GWCQueryResponse response = manager.get(gwc);
            System.out.println("hosts: " + response.getHosts().size());
            System.out.println("gwcs: " + response.getGwcs().size());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
