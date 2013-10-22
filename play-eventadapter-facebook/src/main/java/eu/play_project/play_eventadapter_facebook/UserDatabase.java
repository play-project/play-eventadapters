package eu.play_project.play_eventadapter_facebook;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

public class UserDatabase {
	public String user = "tomcat";
	public String password = "";
	public String url = "jdbc:postgresql:";
	public String tableName = "token";
	public String db_file_name = "play-eventadapter-facebook";
	PreparedStatement insertToken;
	private boolean databaseInitialized = false;
	private Connection dbcon;
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(UserDatabase.class);

	public String getServletInfo() {
		return "Servlet connects to PostgreSQL database and displays result of a SELECT";
	}
	
	public void doStart() throws SQLException, ClassNotFoundException {

		Class.forName("org.postgresql.Driver");
		dbcon = DriverManager.getConnection(url + db_file_name, user, password);
		dbcon.setAutoCommit(true);

		Logger.getAnonymousLogger().info("Creating tables...");
		try {
			// create table when not exist and is ignored when table exist
			update("CREATE TABLE "
					+ tableName
					+ " (id VARCHAR(100), token VARCHAR(1024), PRIMARY KEY (id) )");
		} catch (SQLException e) {
			Logger.getAnonymousLogger().info(
					"The table for Facebook already exists, trying to reuse. "
							+ e);
		}

	}

    // "init" sets up a database connection
	public void init() {

		if (databaseInitialized == false) {
			String loginUser = "tomcat";
			String loginPasswd = "";
			String loginUrl = "jdbc:postgresql:play-eventadapter-facebook";

			// Load the PostgreSQL driver
			try {
				Class.forName("org.postgresql.Driver");
				dbcon = DriverManager.getConnection(loginUrl, loginUser,
						loginPasswd);
				databaseInitialized = true;
			} catch (ClassNotFoundException e) {
				Logger.getAnonymousLogger().log(Level.SEVERE,
						"Class not found Error", e);
			} catch (SQLException e) {
				System.err.println("SQLException: " + e.getMessage());
			}
		}
	}
    
	public void selecDB(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		response.setContentType("text/html");

		// Show output stream from token Database
		PrintWriter out = response.getWriter();

		out.println("<HTML><Head><Title>Database for Token</Title></Head>");
		out.println("<Body><H1>Token</H1>");
		try {
			init();
			// Declare our statement
			Statement statement = dbcon.createStatement();
			String query = "SELECT id, token FROM " + tableName;
			// Perform the query
			ResultSet rs = statement.executeQuery(query);
			out.println("<table border>");
			// Iterate through each row of rs
			while (rs.next()) {
				String id = rs.getString("id");
				String token = rs.getString("token");
				out.println("<tr>" + "<td>" + id + "</td>" + "<td>" + token
						+ "</td>" + "</tr>");
			}
			out.println("</table></body></html>");
			statement.close();
		} catch (Exception ex) {
			out.println("<HTML>" + "<Head><Title>" + "Database: Error"
					+ "</Title></Head>\n<Body>" + "<P>SQL error in doGet: "
					+ ex.getMessage() + "</P></Body></HTML>");
			return;
		}
		out.close();
	}
    
	public void insertDB(String id, String token) {

		try {
			init();
			Logger.getAnonymousLogger().info(
					"preparing to insert ID and Token to Database...");
			insertToken = dbcon.prepareStatement("INSERT INTO " + tableName
					+ " VALUES (?, ?);");

			insertToken.setString(1, id);
			insertToken.setString(2, token);
			insertToken.execute();
		} catch (SQLException e) {
			Logger.getAnonymousLogger().info(
					"User has already subcribed, trying to update Database");
			e.printStackTrace();
		}

	}
    
	public void updateDB(String id, String token) {
		try {
			init();
			Logger.getAnonymousLogger().info(
					"preparing to update Token to Database...");
			insertToken = dbcon.prepareStatement("UPDATE " + tableName
					+ " SET token = '" + token + "' WHERE id = '" + id + "';");
			insertToken.execute();
		} catch (SQLException e) {
			Logger.getAnonymousLogger().info("Exception with update Database");
			e.printStackTrace();
		}

	}
    
	public boolean checkID(String id) {
		try {
			init();
			Statement statement = dbcon.createStatement();
			String query = "SELECT id FROM " + tableName;
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String id_token = rs.getString("id");
				if (id.equals(id_token)) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger .info("User has already subcribed to Application");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
    
	public String getTokenbyID(String ID) {
		String token = null;
		try {
			init();
			Statement statement = dbcon.createStatement();
			String query = "SELECT token FROM " + tableName + " WHERE id = "
					+ ID;
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				token = rs.getString("token");

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}

	public void update(String expression) throws SQLException {

		init();
		Statement st = null;
		st = dbcon.createStatement(); // statements
		int i = st.executeUpdate(expression); // run the query
		if (i == -1) {
			logger.info("docbuilder error : " + expression);
		}
		st.close();
	}
}
