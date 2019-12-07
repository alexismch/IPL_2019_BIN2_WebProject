package domain;

import api.CurrentGameServlet;
import api.HistoricServlet;
import api.JoinGameServlet;
import api.PlayingGameServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.http.HttpServlet;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AsyncApp {

	public static void main(String[] args) throws Exception {
		//Clear temp files
		Files.write(Paths.get("./data/temp/playingGames.json"), "{}".getBytes());
		Files.write(Paths.get("./data/temp/waitingGames.json"), "{}".getBytes());

		// server to listen on specific port 8080
		Server server = new Server(80);
		// create the object to configure the web application
		WebAppContext context = new WebAppContext(); 
		
		System.out.println(context.getContextPath());
		context.setContextPath("/") ;
		
		// this is to be able to make changes to .js files without having to restart everything
		context.setInitParameter("cacheControl","no-store,no-cache,must-revalidate");
		

		HttpServlet apiLogin = new api.LoginServlet();
		context.addServlet(new ServletHolder(apiLogin), "/api/login");

		HttpServlet apiRegister = new api.RegisterServlet();
		context.addServlet(new ServletHolder(apiRegister), "/api/register");

		HttpServlet currentGameServlet = new CurrentGameServlet();
		context.addServlet(new ServletHolder(currentGameServlet), "/api/game");

		HttpServlet historicGameServlet = new HistoricServlet();
		context.addServlet(new ServletHolder(historicGameServlet), "/api/historic");

		HttpServlet gameServlet = new PlayingGameServlet();
		context.addServlet(new ServletHolder(gameServlet), "/api/playingGame");

		HttpServlet joinGameServlet = new JoinGameServlet();
		context.addServlet(new ServletHolder(joinGameServlet), "/api/joinGame");

		HttpServlet loginServlet = new LoginServlet();
		context.addServlet(new ServletHolder(loginServlet), "/login");

		HttpServlet dashBoardServlet = new DashBoardServlet();
		context.addServlet(new ServletHolder(dashBoardServlet), "/dashboard");

		HttpServlet p4Servlet = new P4Servlet();
		context.addServlet(new ServletHolder(p4Servlet), "/p4");

		// create a servlet to control responses to requests at any endpoint URL
		HttpServlet rootServlet = new RootServlet();
		context.addServlet(new ServletHolder(rootServlet), "/");
		
		// handling static content : create the shared folder of your web app
		context.setResourceBase("public"); 
	
		// provide the configuration object to the server
		server.setHandler(context); 
		server.start(); 
	}
}