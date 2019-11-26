package api;

import com.owlike.genson.Genson;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // Get the parameters sent by the POST form
            String email = req.getParameter("email");
            String pseudo = req.getParameter("pseudo");
            String fullname = req.getParameter("fullname");
            String passwd = req.getParameter("passwd");
            System.out.println("USERS POST CALL TO REGISTER A USER:" + email + " " + pseudo + " " + fullname + " " + passwd + " ");

            Genson genson = new Genson();

            // 1: Regarder si l'adresse mail n'est pas déjà pris
            //si pas présent --> créer un compte
            //sinon envoié succes : false

            String json = new String(Files.readAllBytes(Paths.get("./data/users.json")));

            Map users = genson.deserialize(json, Map.class);
            final Map[] targetUser = new Map[1];

            users.forEach((key, value) -> {
                if (key.equals(email))
                    targetUser[0] = (Map) value;
            });

            String return_json = "{\"success\":\"false\", \"message\":\"Utilisateur déjà Existant.\"}";

            // Le compte n'existe pas, du coup on le crée
            if (targetUser[0] == null) {
                Map<String, String> user_infos = new HashMap<String, String>();
                user_infos.put("fullname", fullname);
                user_infos.put("email" , email);
                user_infos.put("pseudo", pseudo);
                user_infos.put("passwd", passwd);

                // Ajout de l'utilisateur dans le json
                users.put(email, user_infos);
                json = genson.serialize(users);
                System.out.println(json);
                //ecrit dans le fichier json
                Files.write(Paths.get("./data/users.json"), json.getBytes());
                return_json = "{\"success\":\"false\", \"message\":\"Vous êtes bien inscrit, veuillez vous identifier.\"}";
            }

            //Reponse au client
            System.out.println("JSON returned :" + return_json);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(return_json);
        } catch (IOException e) {
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