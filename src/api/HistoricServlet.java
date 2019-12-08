package api;

import api.utils.Utils;
import com.owlike.genson.Genson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HistoricServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Get historic games");
            String token = req.getParameter("token");
            if (!Utils.verifyToken(token, req)) {
                Utils.replyWithWrongTokenError(resp, token);
                return;
            }

            Path path = Paths.get("./data/historicGames.json");
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Post historic games");
            String token = req.getParameter("token");
            if (!Utils.verifyToken(token, req)) {
                Utils.replyWithWrongTokenError(resp, token);
                return;
            }
            String return_json = "";
            boolean isOk = true;

            String gagnant = req.getParameter("gagnant");
            String perdant = req.getParameter("perdant");

            if (gagnant == null || "".equals(gagnant) || perdant == null || "".equals(perdant)) {
                isOk = false;
                return_json = "{\"success\":\"false\", \"message\":\"Paramètres invalides.\"}";
            }

            if (isOk) {
                Genson genson = new Genson();

                String json = new String(Files.readAllBytes(Paths.get("./data/historicGames.json")));
                Map games = genson.deserialize(json, Map.class);
                final int[] max = {0};

                games.keySet().forEach(s -> {
                    Integer val = Integer.parseInt((String) s);
                    max[0] = (max[0] < val) ? val : max[0];
                });

                max[0]++;

                Map newWinner = new HashMap();
                newWinner.put("gagnant", gagnant);
                newWinner.put("perdant", perdant);
                games.put(String.valueOf(max[0]), newWinner);

                json = genson.serialize(games);
                System.out.println(json);
                Files.write(Paths.get("./data/historicGames.json"), json.getBytes());

                return_json = "{\"success\":\"true\"}";
            }

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
