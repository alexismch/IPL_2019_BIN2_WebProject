package api;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class AsyncApp {

	public static void main(String[] args) throws Exception {
		// server to listen on specific port 8080
		Server server = new Server(80);
		// create the object to configure the web application
		WebAppContext context = new WebAppContext(); 
		
		System.out.println(context.getContextPath());
		context.setContextPath("/") ;
		
		// this is to be able to make changes to .js files without having to restart everything
		context.setInitParameter("cacheControl","no-store,no-cache,must-revalidate");
		
			
		HttpServlet usersServlet =new UsersServlet();
		// the user servlet deals with the users GET and POST API functions 
		context.addServlet(new ServletHolder(usersServlet), "/users");
		
		HttpServlet loginServlet =new LoginServlet();
		// the servlet deals with the login POST API function 
		context.addServlet(new ServletHolder(loginServlet), "/login");
		
		// create a servlet to control responses to requests at any endpoint URL
		HttpServlet rootServlet =new RootServlet();
		context.addServlet(new ServletHolder(rootServlet), "/");
		
		// handling static content : create the shared folder of your web app
		context.setResourceBase("public"); 
	
		// provide the configuration object to the server
		server.setHandler(context); 
		server.start(); 
	}
}