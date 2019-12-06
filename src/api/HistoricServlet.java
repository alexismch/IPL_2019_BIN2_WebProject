package api;

import api.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
}
