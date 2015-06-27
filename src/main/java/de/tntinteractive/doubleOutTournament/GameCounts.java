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

public class GameCounts {

    private final int closed;
    private final int running;
    private final int total;

    public GameCounts(final int closed, final int running, final int total) {
        this.closed = closed;
        this.running = running;
        this.total = total;
    }

    public int getClosed() {
        return this.closed;
    }

    public int getRunning() {
        return this.running;
    }

    public int getOpen() {
        return this.total - this.closed - this.running;
    }

}
