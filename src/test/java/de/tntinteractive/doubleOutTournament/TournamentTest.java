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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TournamentTest {

    private static Tournament createTournament(final String... ids) throws Exception {
        final List<Contestant> cs = new ArrayList<Contestant>();
        for (final String id : ids) {
            cs.add(new Contestant(id));
        }
        return new Tournament(cs, new StringWriter());
    }

    @Test
    public void testLdCeil() {
        assertEquals(Tournament.ldCeil(1), 0);
        assertEquals(Tournament.ldCeil(2), 1);
        assertEquals(Tournament.ldCeil(3), 2);
        assertEquals(Tournament.ldCeil(4), 2);
        assertEquals(Tournament.ldCeil(5), 3);
        assertEquals(Tournament.ldCeil(6), 3);
        assertEquals(Tournament.ldCeil(7), 3);
        assertEquals(Tournament.ldCeil(8), 3);
        assertEquals(Tournament.ldCeil(9), 4);
        assertEquals(Tournament.ldCeil(15), 4);
        assertEquals(Tournament.ldCeil(16), 4);
        assertEquals(Tournament.ldCeil(17), 5);
    }

    @Test
    public void testGetNextGameWithOnlyTwoContestants() throws Exception {
        final Tournament t = createTournament("p1", "p2");
        final Game g = t.getOpenGame("a");
        assertEquals(g.getContestant1().getID(), "p1");
        assertEquals(g.getContestant2().getID(), "p2");
        assertNull(t.getOpenGame("b"));
    }

    @Test
    public void testGetNextGameWithOnlyThreeContestants() throws Exception {
        final Tournament t = createTournament("p1", "p2", "p3");
        final Game g = t.getOpenGame("a");
        //p1 hat Freilos
        assertEquals(g.getContestant1().getID(), "p2");
        assertEquals(g.getContestant2().getID(), "p3");
        assertSame(t.getOpenGame("a"), g);
        assertNull(t.getOpenGame("b"));
    }

    @Test
    public void testGetNextGameWithFourContestants() throws Exception {
        final Tournament t = createTournament("p1", "p2", "p3", "p4");
        final Game g1 = t.getOpenGame("a");
        assertEquals(g1.getContestant1().getID(), "p1");
        assertEquals(g1.getContestant2().getID(), "p4");
        final Game g2 = t.getOpenGame("b");
        assertEquals(g2.getContestant1().getID(), "p2");
        assertEquals(g2.getContestant2().getID(), "p3");
        assertNull(t.getOpenGame("c"));
    }

    @Test
    public void testFullFourPlayerTournament() throws Exception {
        final Tournament t = createTournament("p1", "p2", "p3", "p4");
        final Game gw21 = t.getOpenGame("a");
        assertEquals(gw21.getKey().toString(), "w,1,0");
        assertEquals(gw21.getContestant1().getID(), "p1");
        assertEquals(gw21.getContestant2().getID(), "p4");
        t.setResult(gw21.getKey(), 1, 0);
        final Game gw22 = t.getOpenGame("a");
        assertEquals(gw22.getKey().toString(), "w,1,1");
        assertEquals(gw22.getContestant1().getID(), "p2");
        assertEquals(gw22.getContestant2().getID(), "p3");
        t.setResult(gw22.getKey(), 0, 1);
        final Game gw11 = t.getOpenGame("a");
        assertEquals(gw11.getKey().toString(), "w,0,0");
        assertEquals(gw11.getContestant1().getID(), "p1");
        assertEquals(gw11.getContestant2().getID(), "p3");
        t.setResult(gw11.getKey(), 1, 0);
        final Game gl21 = t.getOpenGame("a");
        assertEquals(gl21.getKey().toString(), "l,1,0");
        assertEquals(gl21.getContestant1().getID(), "p4");
        assertEquals(gl21.getContestant2().getID(), "p2");
        t.setResult(gl21.getKey(), 1, 0);
        final Game gl11 = t.getOpenGame("a");
        assertEquals(gl11.getKey().toString(), "l,0,0");
        assertEquals(gl11.getContestant1().getID(), "p3");
        assertEquals(gl11.getContestant2().getID(), "p4");
        t.setResult(gl11.getKey(), 0, 1);
        final Game gf = t.getOpenGame("a");
        assertEquals(gf.getKey().toString(), "f,0,0");
        assertEquals(gf.getContestant1().getID(), "p1");
        assertEquals(gf.getContestant2().getID(), "p4");
        t.setResult(gf.getKey(), 1, 0);
        assertNull(t.getOpenGame("a"));
    }

}
