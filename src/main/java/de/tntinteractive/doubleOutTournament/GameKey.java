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

public class GameKey {

    private final char type;
    private final int level;
    private final int nbr;

    public GameKey(final char type, final int level, final int nbr) {
        assert type == 'w' || type == 'l' || type == 'f';
        assert level >= 0;
        assert nbr >= 0;
        this.type = type;
        this.level = level;
        this.nbr = nbr;
    }

    public static GameKey parse(final String game) {
        final String[] parts = game.split(",");
        return new GameKey(parts[0].charAt(0), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.level;
        result = prime * result + this.nbr;
        result = prime * result + this.type;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof GameKey)) {
            return false;
        }
        final GameKey other = (GameKey) obj;
        return this.level == other.level
            && this.nbr == other.nbr
            && this.type == other.type;
    }

    @Override
    public String toString() {
        return this.type + "," + this.level + "," + this.nbr;
    }

    public SlotKey nextSlotForWinner() {
        if (this.isFinal()) {
            return null;
        } else if (this.isWinnerBracket()) {
            if (this.level == 0) {
                return new SlotKey('f', 0, 0, true);
            } else {
                return new SlotKey('w', this.level - 1, this.nbr / 2, this.nbr % 2 == 0);
            }
        } else if (this.isLoserBracket()) {
            if (this.level == 0) {
                return new SlotKey('f', 0, 0, false);
            } else if (this.level % 2 == 0) {
                return new SlotKey('l', this.level - 1, this.nbr / 2, this.nbr % 2 == 0);
            } else {
                return new SlotKey('l', this.level - 1, this.nbr, false);
            }
        }
        throw new RuntimeException("should not happen " + this.type);
    }

    public SlotKey nextSlotForLoser() {
        if (this.isFinal()) {
            return null;
        } else if (this.isWinnerBracket()) {
            int nextNbr;
            if (this.level % 2 == 0) {
                nextNbr = (1 << this.level) - this.nbr - 1;
            } else {
                nextNbr = this.nbr;
            }
            return new SlotKey('l', this.level * 2, nextNbr, true);
        } else if (this.isLoserBracket()) {
            return null;
        }
        throw new RuntimeException("should not happen " + this.type);
    }

    public boolean isWinnerBracket() {
        return this.type == 'w';
    }

    public boolean isFinal() {
        return this.type == 'f';
    }

    public boolean isLoserBracket() {
        return this.type == 'l';
    }

    public int getLevel() {
        return this.level;
    }

}
