package frozork;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// This annotations maps this server to a resource path
// note that using "/" maps it to the default servlet called at EVERY request not matching a servlet
// so if we want to just match the requests at the root we need ""
@WebServlet("")
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
        
		// With request dispatcher I can forward resources (even servlet paths)
		RequestDispatcher view = req.getRequestDispatcher("/rooms.html");
		view.forward(req, resp);
	}
}