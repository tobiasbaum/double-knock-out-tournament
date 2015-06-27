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

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

import com.sun.net.httpserver.HttpServer;

public class Main {

    public static void main(final String[] args) throws IOException {
        final Properties settings = loadSettings(args[0]);

        final List<Contestant> contestants = Contestant.loadFromCsv(settings);
        final Writer redo = new FileWriter("redo." + System.currentTimeMillis() + ".log");
        final Tournament t = new Tournament(contestants, redo);

        final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/doubleOut", new Handler(t, Integer.parseInt(settings.getProperty("breakLimit"))));
        server.setExecutor(null);
        server.start();
        System.out.println("Server started...");
    }

    private static Properties loadSettings(final String filename) throws IOException {
        try (FileInputStream is = new FileInputStream(filename)) {
            final Properties settings = new Properties();
            settings.load(is);
            return settings;
        }
    }

}
