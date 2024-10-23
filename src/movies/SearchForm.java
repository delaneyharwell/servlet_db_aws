package movies;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "SearchForm", urlPatterns = "/searchform")
public class SearchForm extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    //HTTP GET
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //let page know what type of text will be ret
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        //head, title
        out.println("<html><head><title>FabFlix: Search Results</title></head>");
        //body
        out.println("<body><h1>FabFlix: Search Results</h1>");
        try{
            //connect to db
            Connection connection = dataSource.getConnection();
            //new statement
            Statement statement = connection.createStatement();
            //retrieve params
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");
            //sql query to search for items
            //TODO continue code here, for now going to print all search items

            if (star != null && !star.trim().isEmpty()) {
                out.println("<li><strong>Star's Name:</strong> " + star + "</li>");
            }
            if (title != null && !title.trim().isEmpty()) {
                out.println("<li><strong>Title:</strong> " + title + "</li>");
            }
            if (year != null && !year.trim().isEmpty()) {
                out.println("<li><strong>Year:</strong> " + year + "</li>");
            }
            if (director != null && !director.trim().isEmpty()) {
                out.println("<li><strong>Director:</strong> " + director + "</li>");
            }

            //close statements
            statement.close();
            connection.close();

        }catch(Exception e){
            request.getServletContext().log("Error: ", e);

            // Output Error Message to html
            out.println(String.format("<html><head><title>Fabflix: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", e.getMessage()));
            return;
        }
    }
}