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

@WebServlet(name = "SingleMovie", urlPatterns= "/singlemovie")
public class SingleMovie extends HttpServlet {

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
        // Get movieId from the request parameter
        String movieId = request.getParameter("movieId");

        // Log the URI and movieId for debugging
        String requestURI = request.getRequestURI();
        request.getServletContext().log("Request URI: " + requestURI);
        request.getServletContext().log("Received movieId: " + movieId);

        if (movieId == null || movieId.isEmpty()) {
            // Handle the case where movieId is missing
            response.setStatus(400); // Bad request
            response.getWriter().write("{\"error\": \"Missing movieId parameter\"}");
            return;
        }

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try (Connection connection = dataSource.getConnection()) {
            // Prepare SQL query to fetch movie details
            String query = "SELECT m.title, m.year, m.director, r.rating " +
                    "FROM movies AS m " +
                    "JOIN ratings AS r ON m.id = r.movieId " +
                    "WHERE m.id = ?"; // Use prepared statement to prevent SQL injection

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, movieId); // Set the movieId parameter
            ResultSet resultSet = statement.executeQuery();
            Statement genreStatement = connection.createStatement();
            Statement starsStatement = connection.createStatement();
            JsonArray jsonArray = new JsonArray();
            if (resultSet.next()) {
                String genreQuery = "SELECT g.name FROM genres g JOIN genres_in_movies gm ON g.id=gm.genreId WHERE gm.movieId = '" + movieId + "'";
                String starsQuery = "SELECT s.name, s.id FROM stars s JOIN stars_in_movies sm ON s.id=sm.starId WHERE sm.movieId = '" + movieId + "'";
                ResultSet genreSet = genreStatement.executeQuery(genreQuery);
                ResultSet starSet = starsStatement.executeQuery(starsQuery);

                JsonObject jsonObject = new JsonObject();
                String title = resultSet.getString("title");
                String year = resultSet.getString("year");
                String director = resultSet.getString("director");
                String rating = resultSet.getString("rating");
                StringBuilder movieGenres = new StringBuilder();
                while (genreSet.next()) {
                    if (movieGenres.length() > 0) {
                        movieGenres.append(", ");
                    }
                    movieGenres.append(genreSet.getString("name"));
                }

                StringBuilder movieStars = new StringBuilder();
                while(starSet.next()){
                    if(movieStars.length()>0){
                        movieStars.append(", ");
                    }
                    String starId = starSet.getString("id");
                    String name = starSet.getString("name");

                    movieStars.append(String.format("<a href='/2024-fall-cs-122b-coding-cowgirls/singlestar.html?starId=%s'>%s</a>", starId, name));
                }


                jsonObject.addProperty("movie_title", title);
                jsonObject.addProperty("movie_year", year);
                jsonObject.addProperty("movie_director", director);
                jsonObject.addProperty("movie_stars",movieStars.toString());
                jsonObject.addProperty("movie_genres",movieGenres.toString());
                jsonObject.addProperty("movie_rating", rating); // Add rating to JSON
                jsonArray.add(jsonObject);
            }

            out.write(jsonArray.toString());
            response.setStatus(200);

            resultSet.close();
            statement.close();
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
