package movies;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

@WebServlet(name = "SingleStar", urlPatterns= "/singlestar")
public class SingleStar extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get starId from the request parameter
        String starId = request.getParameter("starId");

        // Log the URI and movieId for debugging
        String requestURI = request.getRequestURI();
        request.getServletContext().log("Request URI: " + requestURI);
        request.getServletContext().log("Received starId: " + starId);

        if (starId == null || starId.isEmpty()) {
            // Handle the case where starId is missing
            response.setStatus(400); // Bad request
            response.getWriter().write("{\"error\": \"Missing movieId parameter\"}");
            return;
        }

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try (Connection connection = dataSource.getConnection()) {
            // Prepare SQL query to fetch star details
            String starQuery = "SELECT s.name, IFNULL(s.birthYear, 'N/A') AS birthYear " +
                    "FROM stars AS s " +
                    "WHERE s.id = ?";
            // Use prepared statement to prevent SQL injection and allows for ?
            String moviesQuery = "SELECT m.title, m.id " +
                    "FROM movies AS m " +
                    "JOIN stars_in_movies AS sim ON m.id = sim.movieId " +
                    "WHERE sim.starId = ?";


            PreparedStatement starStatement = connection.prepareStatement(starQuery);
            starStatement.setString(1, starId); // Set the movieId parameter
            ResultSet resultSet = starStatement.executeQuery();
            PreparedStatement moviesStatement = connection.prepareStatement(moviesQuery);
            moviesStatement.setString(1, starId);
            JsonArray jsonArray = new JsonArray();
            if (resultSet.next()) {
                ResultSet moviesResultSet = moviesStatement.executeQuery();

                JsonObject jsonObject = new JsonObject();
                String name = resultSet.getString("name");
                String birthYear = resultSet.getString("birthYear");

                StringBuilder movies = new StringBuilder();
                while (moviesResultSet.next()) {
                    if (movies.length() > 0) {
                        movies.append("<br>");
                    }
                    // Get movie ID and title
                    String movieId = moviesResultSet.getString("id");
                    String title = moviesResultSet.getString("title");

                    movies.append(String.format("<a href='/2024-fall-cs-122b-coding-cowgirls/singlemovie.html?movieId=%s'>%s</a>", movieId, title));
                }

                //add all elements to json object
                jsonObject.addProperty("name", name);
                jsonObject.addProperty("birth_year", birthYear);
                jsonObject.addProperty("movies", movies.toString());
                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);

            resultSet.close();
            starStatement.close();
            moviesStatement.close();
        } catch (Exception e) {
            JsonObject errorObject = new JsonObject();
            errorObject.addProperty("errorMessage", e.getMessage());
            out.write(errorObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
