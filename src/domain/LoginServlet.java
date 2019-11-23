package domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.auth0.jwt.JWTSigner;
import com.owlike.genson.Genson;


@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	// this is our GET /users API providing all the users as a JSON object
	private static final String JWTSECRET = "mybigsecrete123";
	
	// this is our POST /login API providing a JSON object with a token when authentication is successful
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
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
		      Genson genson = new Genson();
		      Map<String, Object> map = genson.deserialize(jb.toString(), Map.class);
		      String email = map.get("email").toString() ;
			  String password = map.get("password").toString();
		   
			// get the parameters sent by the POST form
			//String email = req.getParameter("email");
			//String password = req.getParameter("password");
			System.out.println("USERS POST CALL TO LOG A USER:" + email + " " + password + "Header:"+req.getHeader("Authorization"));
			if (password.equals("Logme")) {
				// user is authenticated, create a JWT
				Map<String, Object> claims = new HashMap<String, Object>(); 
				claims.put("id", UUID.randomUUID().toString()); 
				claims.put("ip", req.getRemoteAddr()); 
				String ltoken = new JWTSigner(JWTSECRET).sign(claims);
				
			
				String json = "{\"success\":\"true\", \"token\":\""+ ltoken + "\"}";
				System.out.println("JSON generated :"+json);
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write(json);
				
			}
			else {
				String json = "{\"success\":\"false\", \"error\":\"Wrong email or password.\"}";
				System.out.println("Authentication error:"+json);
				resp.setContentType("application/json");
				resp.setCharacterEncoding("UTF-8");
				resp.setStatus(HttpServletResponse.SC_OK);
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
