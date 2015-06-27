/**
    This file is part of double-knock-out-tournament.

    double-knock-out-tournament is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    double-knock-out-tournament is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with double-knock-out-tournament. If not, see <http://www.gnu.org/licenses/>.
 */

package de.tntinteractive.doubleOutTournament;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Contestant {

    private final String id;
    private final String link;
    private final String text;
    private final boolean dummy;

    public Contestant(final String id) {
        this(id, false);
    }

    Contestant(final String id, final boolean dummy) {
        this.id = id;
        this.link = id;
        this.text = id;
        this.dummy = dummy;
    }

    Contestant(final String id, final String link, final String text) {
        this.id = id;
        this.link = link;
        this.text = text;
        this.dummy = false;
    }

    public String getID() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String getLink() {
        return this.link;
    }

    public boolean isDummy() {
        return this.dummy;
    }

    public static List<Contestant> loadFromCsv(final Properties settings) throws IOException {
        final String filename = settings.getProperty("input");
        final String idFormat = settings.getProperty("id.format");
        final String linkFormat = settings.getProperty("link.format");
        final String textFormat = settings.getProperty("text.format");
        final List<Contestant> ret = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = r.readLine()) != null) {
                final String lt = line.trim();
                final Object[] parts = toObjectArray(lt.split(";"));
                ret.add(new Contestant(
                        String.format(idFormat, parts),
                        String.format(linkFormat, parts),
                        String.format(textFormat, parts)));
            }
        }
        return ret;
    }

    private static Object[] toObjectArray(final String[] split) {
        final Object[] ret = new Object[split.length];
        for (int i = 0; i < split.length; i++) {
            ret[i] = split[i];
        }
        return ret;
    }

}
