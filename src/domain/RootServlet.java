package domain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//import java.nio.charset.Charset;
//import java.util.Random;

@SuppressWarnings("serial")
public class RootServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			if (req.getRequestURI().contentEquals("/")) {
				System.out.println("AUTH:"+req.getRequestURI());
				String head = new String(Files.readAllBytes(Paths.get("./views/global/head.html")));
				String foot = new String(Files.readAllBytes(Paths.get("./views/global/foot.html")));
				resp.setContentType("text/html");
				resp.setCharacterEncoding("utf-8");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().write(head + foot);
			} else
			//// else return the required file (there is a based folder named "public")
			{
				try {
					Path path = Paths.get("public" + req.getRequestURI());
					File file = new File(path.toString());
					// Set the MIME type (mandatory if we want to use modules in our frontend application)
					String mimeType = "";
                    if(path.toString().contains(".js"))
                           mimeType = "application/javascript";
                    else if(path.toString().contains(".html"))
                           mimeType = "text/html";
                    else if(path.toString().contains(".css"))
                           mimeType = "text/css";
                    else if(path.toString().contains(".ico"))
                           mimeType = "image/x-icon";

				
					System.out.println("FILENAME:"+ file.getName() + "URI:"+req.getRequestURI()+" Content type:"+mimeType);
					String fileContent = new String(Files.readAllBytes(path));
					resp.setContentType(mimeType);
					resp.setCharacterEncoding("utf-8");
					
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().write(fileContent);
				} catch (IOException e) {
					resp.getWriter().write("This resource does not exist");
				}
			}			
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
