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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tournament {

    private final Map<GameKey, Game> games;
    private final Map<GameKey, Contestant> waiting;
    private final int initialLevel;
    private final Writer redoLog;
    private final int contestantCount;

    public Tournament(final List<Contestant> contestants, final Writer redoLog) throws IOException {
        this.redoLog = redoLog;
        this.games = new LinkedHashMap<GameKey, Game>();
        this.waiting = new LinkedHashMap<GameKey, Contestant>();
        this.contestantCount = contestants.size();
        this.initialLevel = ldCeil(contestants.size()) - 1;
        final int initialMatchCount = (1 << this.initialLevel);
        for (int i = 0; i < initialMatchCount; i++) {
            final int endIndex = 2 * initialMatchCount - i - 1;
            if (endIndex >= contestants.size()) {
                //Freilos
                this.createGame('w', this.initialLevel, i, contestants.get(i), new Contestant("d" + i, true));
            } else {
                this.createGame('w', this.initialLevel, i, contestants.get(i), contestants.get(endIndex));
            }
        }
    }

    private void createGame(final char type, final int level, final int nbr, final Contestant c1, final Contestant c2)
        throws IOException {
        this.createGame(new GameKey(type, level, nbr), c1, c2);
    }

    private void createGame(final GameKey key, final Contestant c1, final Contestant c2) throws IOException {
        final Game g = new Game(key, c1, c2);
        this.games.put(g.getKey(), g);
        if (c1.isDummy()) {
            try {
                this.setResult(key, 0, 1);
            } catch (final TournamentException e) {
                throw new RuntimeException("Should not happen", e);
            }
        } else if (c2.isDummy()) {
            try {
                this.setResult(key, 1, 0);
            } catch (final TournamentException e) {
                throw new RuntimeException("Should not happen", e);
            }
        }
    }

    static int ldCeil(final int i) {
        final int ho = Integer.highestOneBit(i);
        final int ldFloor = Integer.numberOfTrailingZeros(ho);
        return ho == i ? ldFloor : ldFloor + 1;
    }

    /**
     * Returns the game that is currently assigned to the given venue. When none is assigned, assigns an open one and returns this.
     * If there are no games left, null is returned.
     */
    public synchronized Game getOpenGame(final String venue) {
        for (final Game game : this.games.values()) {
            if (venue.equals(game.getVenue()) && !game.isClosed()) {
                return game;
            }
        }
        for (final Game game : this.games.values()) {
            if (game.isOpen()) {
                game.setVenue(venue);
                return game;
            }
        }
        return null;
    }

    public synchronized void setResult(final GameKey key, final int score1, final int score2)
        throws TournamentException, IOException {

        if (!key.isFinal() && score1 == score2) {
            throw new TournamentException("A draw is only allowed in finals.");
        }
        if (score1 < 0 || score1 > 99) {
            throw new TournamentException("Invalid score value: " + score1);
        }
        if (score2 < 0 || score2 > 99) {
            throw new TournamentException("Invalid score value: " + score2);
        }
        final Game g = this.games.get(key);
        if (g == null) {
            throw new TournamentException("Game unknown: " + key);
        }
        this.writeRedoEntry(g, score1, score2);
        g.setResult(score1, score2);
        final SlotKey nextWinner = key.nextSlotForWinner();
        final SlotKey nextLoser = key.nextSlotForLoser();
        if (nextWinner != null) {
            this.staffGame(nextWinner, g.getWinner());
        }
        if (nextLoser != null) {
            if (key.getLevel() == this.initialLevel && key.isWinnerBracket()) {
                //Sonderfall für den Anfang des Loser-Brackets
                this.staffGame(nextLoser.getGame().nextSlotForWinner(), g.getLoser());
            } else {
                this.staffGame(nextLoser, g.getLoser());
            }
        }
        if (this.allGamesClosed()) {
            this.redoLog.close();
        }
    }

    private boolean allGamesClosed() {
        if (!this.waiting.isEmpty()) {
            return false;
        }
        for (final Game g : this.games.values()) {
            if (!g.isClosed()) {
                return false;
            }
        }
        return true;
    }

    private void writeRedoEntry(final Game g, final int score1, final int score2) throws IOException {
        this.redoLog.write(g.getKey() + ";" + g.getContestant1().getID() + ";" + g.getContestant2().getID()
                + ";" + score1 + ";" + score2 + ";" + g.getVenue() + "\r\n");
        this.redoLog.flush();
    }

    private void staffGame(final SlotKey slot, final Contestant contestantForSlot) throws IOException {
        final Contestant opponent = this.waiting.remove(slot.getGame());
        if (opponent == null) {
            this.waiting.put(slot.getGame(), contestantForSlot);
        } else {
            if (slot.isFirst()) {
                this.createGame(slot.getGame(), contestantForSlot, opponent);
            } else {
                this.createGame(slot.getGame(), opponent, contestantForSlot);
            }
        }
    }

    public synchronized List<Game> getKnownGames() {
        return new ArrayList<Game>(this.games.values());
    }

    public synchronized List<RankingRow> getRanking() {
        final Map<String, RankingRow> rowsForContestants = new LinkedHashMap<String, RankingRow>();
        for (final Game g : this.games.values()) {
            this.ensureRowExists(rowsForContestants, g.getContestant1());
            this.ensureRowExists(rowsForContestants, g.getContestant2());
            if (g.isClosed() && !g.containsDummy()) {
                rowsForContestants.get(g.getWinner().getID()).adjustForWinner(g);
                rowsForContestants.get(g.getLoser().getID()).adjustForLoser(g);
            }
        }
        final List<RankingRow> rows = new ArrayList<RankingRow>(rowsForContestants.values());
        Collections.sort(rows);
        RankingRow prev = null;
        for (int rank = 1; rank <= rows.size(); rank++) {
            final RankingRow cur = rows.get(rank - 1);
            if (prev != null && prev.hasSameScoresAs(cur)) {
                cur.setRank(prev.getRank());
            } else {
                cur.setRank(rank);
            }
            prev = cur;
        }
        return rows;
    }

    private void ensureRowExists(final Map<String, RankingRow> rowsForContestants, final Contestant contestant) {
        if (contestant.isDummy()) {
            return;
        }
        final String id = contestant.getID();
        if (!rowsForContestants.containsKey(id)) {
            rowsForContestants.put(id, new RankingRow(contestant));
        }
    }

    public void clearVenue(final GameKey key) {
        this.games.get(key).setVenue(null);
    }

    public synchronized GameCounts countGames() {
        int closed = 0;
        int running = 0;
        for (final Game g : this.games.values()) {
            if (g.containsDummy()) {
                continue;
            }
            if (g.isClosed()) {
                closed++;
            } else if (!g.isOpen()) {
                running++;
            }
        }
        return new GameCounts(closed, running, 2 * (this.contestantCount - 1));
    }

}
