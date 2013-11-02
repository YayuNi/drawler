package drawler.crawl.queue;

import java.sql.*;
import java.util.*;

import drawler.crawl.ContextualURL;

public class SQLQueue extends LinkedList<ContextualURL> {

	// MySQL Connection Object
	private Connection connection;

	// User Name
	private String user;
	
	// Password
	private String password;
	
	// Database Name
	private String database;
	
	// Table Name
	private String table;
	
	// MySQL Host
	private String host;
	
	// MySQL Port
	private int port = 3306;

	// Collection url
	private String url;

	// Initialize MySQL Connection
	public boolean init(String host, int port, String user, String password, String database, String table){
		this.host = host;
		this.port = port;
		this.user = user;
		this.database = database;
		this.password = password;
		this.table = table;
		this.url =  "jdbc:mysql://" 
			+ this.host + ":" 
			+ this.port 
			+ "/?useUnicode=true&characterEncoding=UTF8";

		return this.open();
	}

	// Get Connection status
	private boolean isValid() {
		try {
			return this.connection != null && !this.connection.isClosed();
		}
		catch (SQLException e) {
			System.err.println(e.toString());
			return false;
		}
	}

	// Connection to MySQL
	private boolean open() {
		boolean result = false;
		if (!isValid()) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				this.connection = DriverManager.getConnection(this.url, this.user, this.password);
				if (isValid()) 
					if (isDBExisted() || createDB())
						if (isTableExisted() || createTable()) 
							result = true;
						else 
							System.err.println("Table failed.");
					else
						System.err.println("Database failed.");
				else
					System.err.println("Connection Failed.");
			}
			catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		return result;
	}

	// Close connection
	private boolean close() {
		if (isValid()) {
			try {
				this.connection.close();
			}
			catch (SQLException e) {
				System.err.println(e.toString());
				return false;
			}
		}
		return true;
	}

	// Check for database's existence
	private boolean isDBExisted() {
		boolean result = false;
		if (isValid()) {
			try {
				Statement statement = this.connection.createStatement();
				
				String sql = "SELECT count(*) FROM INFORMATION_SCHEMA.SCHEMATA " +
					     "WHERE SCHEMA_NAME='" + this.database +"'";
				ResultSet rs = statement.executeQuery(sql);
				if (rs.next() && rs.getInt(1) >= 1) {
					result = true;
				}
				statement.close();
			}
			catch (SQLException e) {
				System.err.println(e.toString());
			}
		}
		return result;
	}

	// Create database
	private boolean createDB() {
		boolean result = false;
		if (isValid()) {
			try {
				Statement statement = this.connection.createStatement();
				statement.execute("CREATE DATABASE " + this.database + " CHARACTER SET `utf8`");
				statement.close();
				result = true;
			}
			catch (SQLException e) {
				System.err.println(e.toString());
			}
		}
		return result;
	}

	// Check for table's existence
	private boolean isTableExisted() {
		boolean result = false;
		if (isValid()) {
			try {
				Statement statement = this.connection.createStatement();
				String sql = "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES " +
					     "WHERE TABLE_SCHEMA='" + this.database + "' AND " + "TABLE_NAME='" + this.table + "'";
				ResultSet rs = statement.executeQuery(sql);
				if (rs.next() && rs.getInt(1) >= 1) {
					result = true;
				}
				statement.close();
			}
			catch (SQLException e) {
				System.err.println(e.toString());
			}
		}
		return result;
	}

	// Create table
	private boolean createTable() {
		boolean result = false;
		if (isValid()) {
			try {
				Statement statement = this.connection.createStatement();
				String sql = "CREATE TABLE " + this.database + "." + this.table + 
					     "(" +
						"ID		BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
						"URL 		VARCHAR(500)," +
						"ScanDepth	INT," +
						"Scannable	BOOLEAN," +
						"Downloadable	BOOLEAN" +
					     ")";
				statement.execute(sql);
				statement.close();
				result = true;
			}
			catch (SQLException e) {
				System.err.println(e.toString());
			}
		}
		return true;
	}

	// Check table fields
	private boolean checkTable() {
		boolean result = true;
		try {
			Statement statement = this.connection.createStatement();
			String sql = "DESC " + this.database + "." + this.table;
			ResultSet rs = statement.executeQuery(sql);
			int size = 5;
			List<String> fields = Arrays.asList(new String[]{"ID", "URL", "ScanDepth", "Scannable", "Downloadable"});
			while (size-->0 && rs.next()) {
				if (!fields.contains(rs.getString("Field"))) {
					result = false;
					break;
				}
			}
			if ( size!= 0)
				result = false;
		}
		catch (SQLException e) {
			System.err.println(e.toString());
			result = false;
		}
		return result;
	}

	@Override
	public boolean add(ContextualURL contextualURL) {
		boolean result = false;
		String sql = "";
		try {
			sql = "INSERT INTO " + this.database + "." + this.table + " (URL, ScanDepth, Scannable, Downloadable) " +
				     "VALUES (" 
					+ "'" + contextualURL.url.toString() + "', "
					+ contextualURL.scanDepth + ", "
					+ ((contextualURL.scannable)? "1":"0") + ", "
					+ ((contextualURL.downloadable)? "1":"0") +
				     ")";
			Statement statement = this.connection.createStatement();
			statement.execute(sql);
			statement.close();
			result = true;
		}
		catch (SQLException e) {
			System.err.println(sql);
			System.err.println(e.toString());
		}
		return result;
	}

	@Override
	public ContextualURL element() {
		ContextualURL contextualURL = null;
		String sql = "";
		try {
			sql = "SELECT URL, ScanDepth, Scannable, Downloadable FROM " + this.database + "." + this.table + " " +
				     "WHERE ID=( SELECT min(ID) FROM " + this.database + "." + this.table + " )";
			Statement statement = this.connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				contextualURL = new ContextualURL(rs.getString("URL"), rs.getInt("ScanDepth"));
				contextualURL.scannable = rs.getBoolean("Scannable");
				contextualURL.downloadable = rs.getBoolean("Downloadable");
			}
		}
		catch (Exception e) {
			System.out.println(sql);
			System.err.println(e.toString());
			contextualURL = null;
		}
		return contextualURL;
	}

	@Override
	public ContextualURL remove() {
		ContextualURL contextualURL = this.element();
		String sql = "";
		try {
			sql = "DELETE FROM " + this.database + "." + this.table + " ORDER BY ID LIMIT 1";
			Statement statement = this.connection.createStatement();
			statement.execute(sql);
			statement.close();
		}
		catch (SQLException e) {
			System.err.println(sql);
			System.err.println(e.toString());
			contextualURL = null;
		}
		return contextualURL;
	}
	
	@Override
	public boolean offer(ContextualURL contextualURL) {
		return this.add(contextualURL);
	}

	@Override
	public ContextualURL peek() {
		return this.element();
	}

	@Override
	public ContextualURL poll() {
		return this.remove();
	}
	
	@Override
	public int size() {
		int size = 0;
		try {
			String sql = "SELECT count(*) FROM " + this.database + "." + this.table;
			Statement statement = this.connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) 
				size = rs.getInt(1);
			statement.close();
		}
		catch (SQLException e) {
			System.err.println(e.toString());
			size = 0;
		}
		return size;
	}

	@Override
	public boolean contains(Object obj) {
		boolean result = false;
		ContextualURL contextualURL = (ContextualURL)obj;
		try {
			String sql = "SELECT count(*) FROM " + this.database + "." + this.table + " " +
				     "WHERE URL='" + contextualURL.url.toString() + "'";
			Statement statement = this.connection.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next() && rs.getInt(1) > 0)
				result = true;
			statement.close();
		}
		catch (SQLException e) {
			System.err.println(e.toString());
		}
		return result;
	}

	@Override
	public void clear() {
		try {
			String sql = "DROP TABLE " + this.database + "." + this.table;
			Statement statement = this.connection.createStatement();
			statement.execute(sql);
			statement.close();
			this.createTable();
		}
		catch (SQLException e) {
			System.err.println(e.toString());
		}
	}
}
