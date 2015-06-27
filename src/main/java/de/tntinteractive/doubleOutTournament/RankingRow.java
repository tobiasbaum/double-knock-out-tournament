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

public class RankingRow implements Comparable<RankingRow> {

    private int rank;
    private final Contestant contestant;
    private int outIn;
    private int votesFor;
    private int votesAgainst;

    public RankingRow(final Contestant contestant) {
        this.contestant = contestant;
        this.outIn = Integer.MAX_VALUE;
    }

    public int getRank() {
        return this.rank;
    }

    public Contestant getContestant() {
        return this.contestant;
    }

    public int getOutIn() {
        return this.outIn;
    }

    public int getVotesFor() {
        return this.votesFor;
    }

    public int getVotesAgainst() {
        return this.votesAgainst;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public boolean hasSameScoresAs(final RankingRow cur) {
        return this.outIn == cur.outIn
            && this.votesFor == cur.votesFor
            && this.votesAgainst == cur.votesAgainst;
    }

    public void adjustForWinner(final Game game) {
        assert this.contestant == game.getWinner();

        if (game.getKey().isFinal()) {
            this.outIn = 1;
        }
        this.votesFor += game.getWinnerScore();
        this.votesAgainst += game.getLoserScore();
    }

    public void adjustForLoser(final Game game) {
        assert this.contestant == game.getLoser();

        if (game.getKey().isFinal()) {
            this.outIn = 2;
        } else if (game.getKey().isLoserBracket()) {
            this.outIn = Math.min(this.outIn, game.getKey().getLevel() + 3);
        }
        this.votesFor += game.getLoserScore();
        this.votesAgainst += game.getWinnerScore();
    }

    @Override
    public int compareTo(final RankingRow o) {
        if (this.outIn != o.outIn) {
            if (this.outIn == Integer.MAX_VALUE) {
                return -1;
            } else if (o.outIn == Integer.MAX_VALUE) {
                return 1;
            } else {
                return Integer.compare(this.outIn, o.outIn);
            }
        }
        final int diff1 = this.votesFor - this.votesAgainst;
        final int diff2 = o.votesFor - o.votesAgainst;
        if (diff1 != diff2) {
            return Integer.compare(diff2, diff1);
        }
        return Integer.compare(o.votesFor, this.votesFor);
    }

    @Override
    public int hashCode() {
        return this.outIn;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RankingRow)) {
            return false;
        }
        return this.compareTo((RankingRow) o) == 0;
    }

    public boolean isOut() {
        return this.outIn < Integer.MAX_VALUE;
    }

}
