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

public class Game {

    private final GameKey key;
    private final Contestant contestant1;
    private final Contestant contestant2;
    private String venue;
    private int score1;
    private int score2;

    public Game(final GameKey gameKey, final Contestant contestant1, final Contestant contestant2) {
        this.key = gameKey;
        this.contestant1 = contestant1;
        this.contestant2 = contestant2;
        this.venue = null;
    }

    public Contestant getContestant1() {
        return this.contestant1;
    }

    public Contestant getContestant2() {
        return this.contestant2;
    }

    public synchronized boolean isOpen() {
        return this.venue == null && !this.isClosed();
    }

    public synchronized boolean isClosed() {
        return this.score1 != 0 || this.score2 != 0;
    }

    public synchronized void setVenue(final String venue) {
        this.venue = venue;
    }

    public GameKey getKey() {
        return this.key;
    }

    public synchronized void setResult(final int score1, final int score2) {
        this.score1 = score1;
        this.score2 = score2;
    }

    public synchronized Contestant getWinner() {
        return this.score1 >= this.score2 ? this.contestant1 : this.contestant2;
    }

    public synchronized Contestant getLoser() {
        return this.score1 >= this.score2 ? this.contestant2 : this.contestant1;
    }

    public synchronized String getVenue() {
        return this.venue;
    }

    public synchronized int getScore1() {
        return this.score1;
    }

    public synchronized int getScore2() {
        return this.score2;
    }

    public int getWinnerScore() {
        return Math.max(this.score1, this.score2);
    }

    public int getLoserScore() {
        return Math.min(this.score1, this.score2);
    }

    public boolean containsDummy() {
        return this.contestant1.isDummy() || this.contestant2.isDummy();
    }

}
