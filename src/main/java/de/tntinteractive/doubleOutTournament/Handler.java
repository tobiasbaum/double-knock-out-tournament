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
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handler implements HttpHandler {

    private final Tournament tournament;
    private final int breakLimit;

    public Handler(final Tournament tournament, final int breakLimit) {
        this.tournament = tournament;
        this.breakLimit = breakLimit;
    }

    @Override
    public void handle(final HttpExchange t) throws IOException {
        System.out.println("request: " + t.getRequestURI());

        try {
            //Parameterparsen für Arme... Nicht schön aber selten...
            System.out.println("query: " + t.getRequestURI().getQuery());
            final Map<String, String> params = this.parseParams(t.getRequestURI().getQuery());
            System.out.println("params: " + params);
            if ("enter".equals(params.get("action"))) {
                this.tournament.setResult(
                        GameKey.parse(params.get("key")),
                        Integer.parseInt(params.get("result1")),
                        Integer.parseInt(params.get("result2")));
            } else if ("clearVenue".equals(params.get("action"))) {
                this.tournament.clearVenue(GameKey.parse(params.get("key")));
            }
            final String response;
            final String path = t.getRequestURI().getPath();
            if (path.contains("/play")) {
                response = this.createPlayPage(t.getRemoteAddress());
            } else if (path.contains("/results")) {
                response = this.createResultsPage();
            } else if (path.contains("/ranking")) {
                response = this.createRankingPage();
            } else {
                response = "vier null vier";
            }

            final byte[] bytes = response.getBytes("UTF-8");
            t.getResponseHeaders().set("Content-Type", "text/html;charset=UTF-8");
            t.sendResponseHeaders(200, bytes.length);
            final OutputStream os = t.getResponseBody();
            os.write(bytes);
            os.close();

            t.close();
        } catch (final Exception e) {
            e.printStackTrace();
            t.sendResponseHeaders(500, 21);
            final OutputStream os = t.getResponseBody();
            os.write("internal server error".getBytes("UTF-8"));
            os.close();
        }
    }

    private Map<String, String> parseParams(final String path) {
        if (path == null) {
            return Collections.emptyMap();
        }
        final Map<String, String> ret = new HashMap<String, String>();
        for (final String split : path.split("&")) {
            final String[] kv = split.split("=");
            ret.put(kv[0], kv[1]);
        }
        return ret;
    }

    private String createPlayPage(final InetSocketAddress remoteAddress) {
        final GameCounts counts = this.tournament.countGames();
        if (counts.getOpen() == this.breakLimit && counts.getRunning() > 0) {
            return this.commonStuff("Double-out tournament - Enter game", "Break");
        }
        final Game game = this.tournament.getOpenGame(remoteAddress.getHostString());
        if (game == null) {
            return this.createRankingPage();
        }
        final String page =
                "<form action=\"play\">"
                + "<small><it>Game " + game.getKey() + "</it></small><br/>"
                + "<input type=\"text\" name=\"result1\" autocomplete=\"off\"></input> " + this.contestantLink(game.getContestant1()) + "<br/>"
                + "<input type=\"text\" name=\"result2\" autocomplete=\"off\"></input> " + this.contestantLink(game.getContestant2()) + "<br/>"
                + "<input type=\"hidden\" name=\"key\" value=\"" + game.getKey() + "\"></input>"
                + "<input type=\"hidden\" name=\"action\" value=\"enter\"></input>"
                + "<button type=\"submit\">Enter</button>"
                + "</form>";
        return this.commonStuff("Double-out tournament - Enter game", page);
    }

    private String contestantLink(final Contestant contestant) {
        return "<a href=\"" + escape(contestant.getLink()) + "\" target=\"blank\">" + escape(contestant.getText()) + "</a>";
    }

    private String createResultsPage() {
        final List<Game> knownGames = this.tournament.getKnownGames();
        final StringBuilder page = new StringBuilder();
        for (final Game g : knownGames) {
            page.append(this.contestantLink(g.getContestant1()))
                .append(" - ")
                .append(this.contestantLink(g.getContestant2()))
                .append(" ");
            if (g.isClosed()) {
                page.append("<b>").append(g.getScore1()).append(" - ").append(g.getScore2()).append("</b>");
            } else if (g.isOpen()) {
                page.append("open");
            } else {
                page.append("running at ").append(g.getVenue()).append(" (").append(g.getKey()).append(")");
            }
            page.append("<br/>");
        }
        return this.commonStuff("Double-out tournament - Results entered so far", page.toString());
    }

    private String createRankingPage() {
        final List<RankingRow> ranking = this.tournament.getRanking();
        final StringBuilder page = new StringBuilder();
        page.append("<table><tr><th>#</th><th>Contestant</th><th>Out in</th><th>Votes</th></tr>");
        for (final RankingRow row : ranking) {
            page.append("<tr>")
                .append("<td>").append(row.getRank()).append("</td>")
                .append("<td>").append(this.contestantLink(row.getContestant())).append("</td>")
                .append("<td>").append(row.isOut() ? row.getOutIn() : "-").append("</td>")
                .append("<td>").append(row.getVotesFor()).append(" : ").append(row.getVotesAgainst()).append("</td>")
                .append("</tr>");
        }
        page.append("</table>\n\n");
        page.append("<!--\n");
        for (final RankingRow row : ranking) {
            page.append(row.getContestant().getID()).append("\n");
        }
        page.append("-->");
        return this.commonStuff("Double-out tournament - Ranking", page.toString());
    }

    private String commonStuff(final String title, final String page) {
        final GameCounts counts = this.tournament.countGames();
        return "<html><head>"
                + "<title>" + escape(title) + "</title>"
                + "<body>"
                + "<h1>" + escape(title) + "</h1>"
                + "<a href=\"play\">Enter game</a> - "
                + "<a href=\"results\">View entered results</a> - "
                + "<a href=\"ranking\">Show ranking</a><br/><br/>"
                + page
                + "<p>Open: " + counts.getOpen() + ", Running: " + counts.getRunning() + ", Closed: " + counts.getClosed() + "</p>"
                + "</body>"
                + "</html>";
    }

    private static String escape(final String title) {
        return title.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;");
    }

}
