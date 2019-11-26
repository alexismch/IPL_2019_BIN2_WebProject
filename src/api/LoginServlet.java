package api;

import com.auth0.jwt.JWTSigner;
import com.owlike.genson.Genson;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends HttpServlet {
    // this is our GET /users API providing all the users as a JSON object
    private static final String JWTSECRET = "JSWebProjet2019";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            //Récupération des paramètres et vérification de leur existence
            String email = req.getParameter("email");
            String passwd = req.getParameter("passwd");
            System.out.println("USERS POST CALL TO LOG A USER:" + email + " " + passwd);

            Genson genson = new Genson();

            //Récupération du compte cible
            String json = new String(Files.readAllBytes(Paths.get("./data/users.json")));
            Map users = genson.deserialize(json, Map.class);
            final Map[] targetUser = new Map[1];
            users.forEach((key, value) -> {
                if (key.equals(email))
                    targetUser[0] = (Map) value;
            });

            String return_json = "{\"success\":\"false\", \"message\":\"Pas d'utilisateur correspondant.\"}";

            //Vérification si l'utilisateur existe
            if (targetUser[0] != null) {
                String password = (String) targetUser[0].get("passwd");
                //Vérification si le mot de passe est correct
                if (BCrypt.checkpw(passwd, password)) {
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("pseudo", targetUser[0]);
                    claims.put("ip", req.getRemoteAddr());
                    String token = new JWTSigner(JWTSECRET).sign(claims);
                    //Renvoie du succès avec le token
                    return_json = "{\"success\":\"true\", \"token\":\""+ token + "\"}";
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
