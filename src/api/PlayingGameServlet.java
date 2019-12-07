package api;

import api.utils.Utils;
import com.owlike.genson.Genson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class PlayingGameServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Post playing game");
            String token = req.getParameter("token");
            if (!Utils.verifyToken(token, req)) {
                Utils.replyWithWrongTokenError(resp, token);
                return;
            }
            String nomPartie = req.getParameter("nomPartie");
            String colonne = req.getParameter("colonne");
            String pseudo = req.getParameter("pseudo");

            if (nomPartie == null || "".equals(nomPartie)) {
                Utils.replyWithError(resp, 404, "Wrong game name", "Game not found : " + nomPartie);
                return;
            }
            if (colonne == null || "".equals(colonne)) {
                Utils.replyWithError(resp, 404, "Wrong column", "Wrong column : " + colonne);
                return;
            }
            if (pseudo == null || "".equals(pseudo)) {
                Utils.replyWithError(resp, 404, "Wrong pseudo", "Wrong pseudo : " + pseudo);
                return;
            }

            Genson genson = new Genson();
            String json = new String(Files.readAllBytes(Paths.get("./data/temp/playingGames.json")));
            Map games = genson.deserialize(json, Map.class);
            final Map[] targetGame = new Map[1];
            games.forEach((key, value) -> {
                if (key.equals(nomPartie))
                    targetGame[0] = (Map) value;
            });

            System.out.println(targetGame[0]);
            targetGame[0].put("colonne", colonne);
            targetGame[0].put("dernierJoueur", pseudo);
            System.out.println(targetGame[0]);
            games.put(nomPartie, targetGame[0]);
            json = genson.serialize(games);
            System.out.println(json);
            Files.write(Paths.get("./data/temp/playingGames.json"), json.getBytes());

            String return_json = "{\"success\":\"true\"}";
            System.out.println("JSON returned :" + return_json);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(return_json);

        } catch (Exception e) {
            e.printStackTrace();
            String json = "{\"success\":\"false\", \"error\":";
            json += e.getMessage();
            json += "}";
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(json);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Get playing game");
            String token = req.getParameter("token");
            if (!Utils.verifyToken(token, req)) {
                Utils.replyWithWrongTokenError(resp, token);
                return;
            }
            String nomPartie = req.getParameter("nomPartie");

            if (nomPartie == null || "".equals(nomPartie)) {
                Utils.replyWithError(resp, 404, "Wrong game name", "Game not found : " + nomPartie);
                return;
            }

            String return_json = "{\"success\":\"true\", \"data\":{";

            Genson genson = new Genson();
            String json = new String(Files.readAllBytes(Paths.get("./data/temp/playingGames.json")));
            Map games = genson.deserialize(json, Map.class);
            final Map[] targetGame = new Map[1];
            games.forEach((key, value) -> {
                if (key.equals(nomPartie))
                    targetGame[0] = (Map) value;
            });

            if (targetGame[0] == null) {
                Utils.replyWithError(resp, 404, "Wrong game name", "Game not found : " + nomPartie);
                return;
            }

            return_json += "\"nomPartie\":\"" + targetGame[0].get("nomPartie") + "\", ";
            return_json += "\"joueur1\":\"" + targetGame[0].get("joueur1") + "\", ";
            return_json += "\"joueur2\":\"" + targetGame[0].get("joueur2") + "\", ";
            return_json += "\"dernierJoueur\":\"" + targetGame[0].get("dernierJoueur") + "\", ";
            return_json += "\"colonne\":\"" + targetGame[0].get("colonne") + "\"}";
            return_json += "}";

            System.out.println(return_json);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(return_json);
        }
        catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            resp.setContentType("text/html");
            byte[] msgBytes = e.getMessage().getBytes("UTF-8");
            resp.setContentLength(msgBytes.length);
            resp.setCharacterEncoding("utf-8");
            resp.getOutputStream().write(msgBytes);
        }
    }
}
