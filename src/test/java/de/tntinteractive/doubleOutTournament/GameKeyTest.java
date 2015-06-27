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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GameKeyTest {

    private static SlotKey slot(final String game, final boolean first) {
        return new SlotKey(GameKey.parse(game), first);
    }

    private static void checkNextWinnerAndLoser(
            final String game,
            final SlotKey expectedWinner,
            final SlotKey expectedLoser) {
        final GameKey key = GameKey.parse(game);
        final SlotKey nextWinner = key.nextSlotForWinner();
        final SlotKey nextLoser = key.nextSlotForLoser();
        assertEquals("wrong result for winner", expectedWinner, nextWinner);
        assertEquals("wrong result for loser", expectedLoser, nextLoser);
    }

    @Test
    public void testNextWinnerAndLoser() {
        checkNextWinnerAndLoser("f,0,0", null, null);
        checkNextWinnerAndLoser("w,0,0", slot("f,0,0", true), slot("l,0,0", true));
        checkNextWinnerAndLoser("w,1,0", slot("w,0,0", true), slot("l,2,0", true));
        checkNextWinnerAndLoser("w,1,1", slot("w,0,0", false), slot("l,2,1", true));
        checkNextWinnerAndLoser("w,2,0", slot("w,1,0", true), slot("l,4,3", true));
        checkNextWinnerAndLoser("w,2,1", slot("w,1,0", false), slot("l,4,2", true));
        checkNextWinnerAndLoser("w,2,2", slot("w,1,1", true), slot("l,4,1", true));
        checkNextWinnerAndLoser("w,2,3", slot("w,1,1", false), slot("l,4,0", true));
        checkNextWinnerAndLoser("l,0,0", slot("f,0,0", false), null);
        checkNextWinnerAndLoser("l,1,0", slot("l,0,0", false), null);
        checkNextWinnerAndLoser("l,2,0", slot("l,1,0", true), null);
        checkNextWinnerAndLoser("l,2,1", slot("l,1,0", false), null);
        checkNextWinnerAndLoser("l,3,0", slot("l,2,0", false), null);
        checkNextWinnerAndLoser("l,3,1", slot("l,2,1", false), null);
        checkNextWinnerAndLoser("l,4,0", slot("l,3,0", true), null);
        checkNextWinnerAndLoser("l,4,1", slot("l,3,0", false), null);
        checkNextWinnerAndLoser("l,4,2", slot("l,3,1", true), null);
        checkNextWinnerAndLoser("l,4,3", slot("l,3,1", false), null);
    }

}
