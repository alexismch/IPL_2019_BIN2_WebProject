package api;

import com.owlike.genson.Genson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CurrentGameServlet extends HttpServlet {
    // this is our GET /users API providing all the users as a JSON object
    private static final String JWTSECRET = "JSWebProjet2019";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            String needPasswd = req.getParameter("needPasswd");
            String passwd = req.getParameter("passwd");

            String return_json = "{\"success\":\"false\", \"message\":\"Partie avec ce nom déjà existante.\"}";
            boolean isOk = true;
            if (name == null || needPasswd == null || ("true".equals(needPasswd) && (passwd == null || passwd.isEmpty())))  {
                isOk = false;
                return_json = "{\"success\":\"false\", \"message\":\"Paramètres invalides.\"}";
            }

            if (isOk) {
                System.out.println("USERS POST CALL TO REGISTER A GAME :" + name + " " + needPasswd + " " + passwd);
                Genson genson = new Genson();

                String json = new String(Files.readAllBytes(Paths.get("./data/temp/currentGames.json")));
                Map games = genson.deserialize(json, Map.class);
                final Map[] targetGame = new Map[1];

                games.forEach((key, value) -> {
                    if (key.equals(name))
                        targetGame[0] = (Map) value;
                });

                if (targetGame[0] == null) {
                    Map<String, String> game_infos = new HashMap<String, String>();
                    game_infos.put("name" , name);
                    game_infos.put("needPasswd" , needPasswd);
                    game_infos.put("passwd" , passwd);

                    games.put(name, game_infos);
                    json = genson.serialize(games);
                    System.out.println(json);
                    Files.write(Paths.get("./data/temp/currentGames.json"), json.getBytes());

                    return_json = genson.serialize(game_infos);
                    return_json = "{\"success\":\"true\", \"data\":" + return_json + "}";
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Get games");

            Path path = Paths.get("./data/temp/currentGames.json");
            String json = "{\"success\":\"true\", \"data\":";

            if (Files.exists(path)) {
                json += new String(Files.readAllBytes(path));
            }
            else {
                json += "\"\"";
            }
            json += "}";

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json);
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
