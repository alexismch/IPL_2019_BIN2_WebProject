package domain;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
					if(path.toString().contains(".json"))
						mimeType = "application/json";
                    else if(path.toString().contains(".js"))
                           mimeType = "application/javascript";
                    else if(path.toString().contains(".html"))
                           mimeType = "text/html";
                    else if(path.toString().contains(".css"))
                           mimeType = "text/css";
                    else if(path.toString().contains(".ico"))
                           mimeType = "image/x-icon";
					else if(path.toString().contains(".jpg") || path.toString().contains(".jpeg") || path.toString().contains(".JPG") || path.toString().contains(".JPEG"))
						mimeType = "image/jpeg";
					else if (path.toString().contains(".png") || path.toString().contains(".PNG"))
					mimeType = "image/png";

					if (mimeType.contains("image")) {
						InputStream is = getServletContext().getResourceAsStream(req.getRequestURI());
						// it is the responsibility of the container to close output stream
						OutputStream os = resp.getOutputStream();
						if (is == null) {
							resp.setContentType("text/plain");
							os.write("Failed to send image".getBytes());
						} else {
							byte[] buffer = new byte[1024];
							int bytesRead;
							resp.setContentType("image/png");
							while ((bytesRead = is.read(buffer)) != -1)
								os.write(buffer, 0, bytesRead);
						}
					} else {
						System.out.println("FILENAME:"+ file.getName() + "URI:"+req.getRequestURI()+" Content type:"+mimeType);
						String fileContent = new String(Files.readAllBytes(path));
						resp.setContentType(mimeType);
						resp.setCharacterEncoding("utf-8");
						resp.setStatus(HttpServletResponse.SC_OK);
						resp.getWriter().write(fileContent);
					}
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
