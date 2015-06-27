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

public class SlotKey {

    private final GameKey game;
    private final boolean first;

    public SlotKey(final char type, final int level, final int nbr, final boolean first) {
        this(new GameKey(type, level, nbr), first);
    }

    public SlotKey(final GameKey game, final boolean first) {
        this.game = game;
        this.first = first;
    }

    public GameKey getGame() {
        return this.game;
    }

    public boolean isFirst() {
        return this.first;
    }

    @Override
    public int hashCode() {
        return this.game.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SlotKey)) {
            return false;
        }
        final SlotKey other = (SlotKey) obj;
        return this.game.equals(other.game)
            && this.first == other.first;
    }

    @Override
    public String toString() {
        return this.game + ":" + this.first;
    }

}
