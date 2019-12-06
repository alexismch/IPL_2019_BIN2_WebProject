package domain;

import api.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class P4Servlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("AUTH:"+req.getRequestURI());
            String type = req.getParameter("type");
            String token = req.getParameter("token");
            String answer = "";
            if ("json".equals(type)) {
                if (!Utils.verifyToken(token, req)) {
                    Utils.replyWithWrongTokenError(resp, token);
                    return;
                }
                String body = "";
                if (req.getHeader("referer").contains("/login") || req.getHeader("referer").equals("http://" + req.getRemoteHost() + "/"))
                    body = new String(Files.readAllBytes(Paths.get("./views/global/menu.html")));
                body += new String(Files.readAllBytes(Paths.get("./views/puissance4.html")));
                answer = body;
                resp.setContentType("application/json");
            } else {
                String head = new String(Files.readAllBytes(Paths.get("./views/global/head.html")));
                String body = new String(Files.readAllBytes(Paths.get("./views/global/menu.html")));
                body += new String(Files.readAllBytes(Paths.get("./views/puissance4.html")));
                String foot = new String(Files.readAllBytes(Paths.get("./views/global/foot.html")));
                answer = head + body + foot;
                resp.setContentType("text/html");
            }
            resp.setCharacterEncoding("utf-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(answer);
        } catch (Exception e) {
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
