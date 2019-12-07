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

public class JoinGameServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String token = req.getParameter("token");
            if (!Utils.verifyToken(token, req)) {
                Utils.replyWithWrongTokenError(resp, token);
                return;
            }
            String name = req.getParameter("name");
            String passwd = req.getParameter("passwd");
            String pseudo = req.getParameter("pseudo");

            String return_json = "{\"success\":\"false\", \"message\":\"Partie non existante.\"}";
            boolean isOk = true;
            if (name == null || pseudo == null || "".equals(pseudo))  {
                isOk = false;
                return_json = "{\"success\":\"false\", \"message\":\"Paramètres invalides.\"}";
            }

            if (isOk) {
                System.out.println("USERS POST CALL TO JOIN A GAME :" + name + " " + pseudo + " " + passwd);
                Genson genson = new Genson();

                String json = new String(Files.readAllBytes(Paths.get("./data/temp/waitingGames.json")));
                Map games = genson.deserialize(json, Map.class);
                final Map[] targetGame = new Map[1];

                games.forEach((key, value) -> {
                    if (key.equals(name))
                        targetGame[0] = (Map) value;
                });

                boolean passwdOk = false;
                if (targetGame[0] != null) {
                    System.out.println(targetGame[0]);
                    if ("false".equals(targetGame[0].get("needPasswd")) || targetGame[0].get("passwd").equals(passwd))
                        passwdOk = true;
                }

                if (passwdOk) {
                    return_json = "{\"success\":\"true\"}";
                    games.remove(name);
                    json = genson.serialize(games);
                    System.out.println(json);
                    Files.write(Paths.get("./data/temp/waitingGames.json"), json.getBytes());

                    json = new String(Files.readAllBytes(Paths.get("./data/temp/playingGames.json")));
                    games = genson.deserialize(json, Map.class);
                    games.forEach((key, value) -> {
                        if (key.equals(name))
                            targetGame[0] = (Map) value;
                    });
                    targetGame[0].put("joueur2", pseudo);
                    System.out.println(targetGame[0]);
                    json = genson.serialize(games);
                    System.out.println(json);
                    Files.write(Paths.get("./data/temp/playingGames.json"), json.getBytes());
                } else if (targetGame[0] != null) {
                    return_json = "{\"success\":\"false\", \"message\":\"Mot de passe incorrect.\"}";
                }
            }

            //Envoie de la réponse
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
}
