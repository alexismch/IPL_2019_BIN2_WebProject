import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWTVerifier;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;

@SuppressWarnings("serial")
public class UsersServlet extends HttpServlet {
	// this is our GET /users API providing all the users as a JSON object
	private static final String JWTSECRET = "mybigsecrete123";
	// the route has been secured by a token that shall be provided in order to get some results
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// super.doGet(req, resp);
		try {
			//System.out.println("USERS GET CALL TO GET THE USER LIST");

			// check if a valid token is provided in the API call
			String token = req.getHeader("Authorization");			
			if(token!=null) {
				token = token.replace("Bearer ","");
				Object userID = null;
				Object ip = null;
				Map<String, Object> decodedPayload = new JWTVerifier(JWTSECRET).verify(token);
				userID = decodedPayload.get("id");
				ip = decodedPayload.get("ip");
				if (userID != null) {
					//System.out.println("Valid token provided in JWT.");
					// the users DB is a json file
					// read the file
					// get the content of the file to be sent back to the client
					String json = "{\"success\":\"true\", \"data\":";
					json += new String(Files.readAllBytes(Paths.get("./data/users.json")));
					json += "}";
					resp.setContentType("application/json");
					resp.setCharacterEncoding("UTF-8");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().write(json);
				}				
			}
			else {
				System.out.println("Unvalid or no token provided.");
				String json = "{\"success\":\"false\", \"error\":\"Unauthorized: this ressource can only be accessed with a valid token.\"";
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				resp.getWriter().write(json);
			}		
		}

		catch (Exception e) {
			e.printStackTrace();
			// resp.setStatus(500);
			String json = "{\"success\":\"false\", \"error\":";
			json += e.getMessage();
			json += "}";
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write(json);
		}
	}

	// this is our POST /users API providing all the users as a JSON object
	// the route has been secured by a token that shall be provided in order to get some results
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			//System.out.println("USERS POST CALL TO ADD A USER");
			// check if a valid token is provided in the API call
			String token = req.getHeader("Authorization");
			
			if(token!=null) {
				token = token.replace("Bearer ","");
				Object userID = null;
				Object ip = null;
				Map<String, Object> decodedPayload = new JWTVerifier(JWTSECRET).verify(token);
				userID = decodedPayload.get("id");
				ip = decodedPayload.get("ip");
				if (userID != null) {
					System.out.println("POST /users : valid token provided in cookie.");
					// get the parameters sent by the POST form
					// Get the POST parameters sent as JSON
					// NB : req.getParameter(...) can only be used if the data was sent
					// as 'application/x-www-form-urlencoded; charset=UTF-8'
					
					// read the JSON data			
					StringBuffer jb = new StringBuffer();
				      String line = null;
				      try {
				        BufferedReader reader = req.getReader();
				        while ((line = reader.readLine()) != null)
				          jb.append(line);
				        System.out.println("READER:" + jb.toString());
				      } catch (Exception e) { 
				          e.printStackTrace();        
				      }
				   
					//deserialize the data
				      Genson genson2 = new Genson();
				      Map<String, Object> map = genson2.deserialize(jb.toString(), Map.class);
				      String email = map.get("email").toString() ;
					  String fullname = map.get("fullname").toString();
					  
					//String email = req.getParameter("email");
					//String fullname = req.getParameter("fullname");

					// read the file
					// get the content of the file
					String json = new String(Files.readAllBytes(Paths.get("./data/users.json")));
					// get the JSON object from the string (content of the file): deserialized to a
					// list of map
					Genson genson = new Genson();
					List<User> users = genson.deserialize(json, new GenericType<List<User>>() {
					});

					users.add(new User(fullname, email));

					// rewrite the ./data/users.json file from the List
					// 1. serialize the collection
					json = genson.serialize(users);
					System.out.println(json);
					// write the file (even if it exists)
					Files.write(Paths.get("./data/users.json"), json.getBytes());

					// send simply a success info
					json = "{\"success\":\"true\"}";
					resp.setContentType("application/json");
					resp.setCharacterEncoding("UTF-8");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().write(json);
				}				
			}
			else {
				System.out.println("Unvalid or no token provided.");
				String json = "{\"success\":\"false\", \"error\":\"Unauthorized: this ressource can only be accessed with a valid token.\"";
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				resp.getWriter().write(json);
			}			
		}

		catch (Exception e) {
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

class User {
	public String email;
	public String fullname;

	// there shall be a constructor with no parameters to set the collection !
	public User() {
	};

	public User(String fullname, String email) {
		this.email = email;
		this.fullname = fullname;
	}

}
// this POST method shall provide a personal message by redirecting to /welcome
