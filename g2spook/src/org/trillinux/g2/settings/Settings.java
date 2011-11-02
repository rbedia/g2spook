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
package org.trillinux.g2.settings;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    private static Settings instance = new Settings();

    private final Map<String, Setting<?>> settings;

    private Settings() {
        settings = new HashMap<String, Setting<?>>();
        init();
    }

    private void init() {
        addInt("leafToHub", 3);
        addInt("hubToHub", 5);
        addInt("hubToLeaf", 25);
    }

    /**
     * @return the instance
     */
    public static Settings getInstance() {
        return instance;
    }

    public void addInt(String key, Integer value) {
        settings.put(key, new Setting<Integer>(key, value));
    }

    public void addString(String key, String value) {
        settings.put(key, new Setting<String>(key, value));
    }

    public Setting<?> getSetting(String key) {
        return settings.get(key);
    }
}
