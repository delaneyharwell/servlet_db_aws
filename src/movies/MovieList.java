package movies;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import jakarta.servlet.ServletConfig;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MovieList", urlPatterns= "/movies") //this gives a URL mapping to webpage
public class MovieList extends HttpServlet{

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

        // this is letting browser know it is dealing with html
        response.setContentType("application/json");

        // Get the PrintWriter for writing response
        PrintWriter out = response.getWriter();

//        out.println("<html>");
//        out.println("<head><title>Fabflix</title></head>");
        try (Connection connection = dataSource.getConnection()) {
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
//            Connection connection = DriverManager.getConnection(loginURL, loginUser, loginPassword);
            Statement statement = connection.createStatement();
            Statement genreStatement = connection.createStatement();
            Statement starsStatement = connection.createStatement();

            String query = "SELECT * FROM movies AS m JOIN ratings as R ON m.id = R.movieId ORDER by rating DESC LIMIT 20";
            ResultSet resultSet = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();
            while (resultSet.next()){
                String movieId = resultSet.getString("id");

                String genreQuery = "SELECT g.name FROM genres g JOIN genres_in_movies gm ON g.id=gm.genreId WHERE gm.movieId = '" + movieId + "' LIMIT 3";
                String starsQuery = "SELECT s.name, s.id FROM stars s JOIN stars_in_movies sm ON s.id=sm.starId WHERE sm.movieId = '" + movieId + "' LIMIT 3";

                ResultSet genreSet = genreStatement.executeQuery(genreQuery);
                ResultSet starSet = starsStatement.executeQuery(starsQuery);


                String movieTitle = resultSet.getString("title");
                String movieYear = resultSet.getString("year");
                String movieDirector = resultSet.getString("director");
                String movieRating = resultSet.getString("rating");
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

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year",movieYear);
                jsonObject.addProperty("movie_director",movieDirector);
                jsonObject.addProperty("movie_stars",movieStars.toString());
                jsonObject.addProperty("movie_genres",movieGenres.toString());
                jsonObject.addProperty("movie_rating",movieRating);
                //TODO add more
                jsonArray.add(jsonObject);
            }
//            out.println("</table>");
//            out.println("</body>");
            resultSet.close();
            statement.close();
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
            //connection.close();

        }catch(Exception e){
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }finally{
            out.close();
        }
    }
}
