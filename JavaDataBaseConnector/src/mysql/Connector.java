package mysql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetMetaData;

/**
 * This class should serve as adapter to MySQL database
 * 
 * @version 1.1
 * @author Vladi - 8:09 PM 9/12/2013
 * @see <a href="http://apekshit.com/t/51/Steps-to-connect-Database-using-JAVA">MySQL Tutorial</a> <pre>
 * // Create connector.
 * Connector connector = new Connector(&quot;url&quot;, &quot;user&quot;, &quot;password&quot;);
 * 
 * // Create query.
 * Query query = new Query();
 * query.setQueryString(&quot;select * from table where id = ? and name = ? ;&quot;);
 * query.addIntParameter(120);
 * query.addTextParameter(&quot;testName&quot;);
 * 
 * // Use connector in order to send query to MySQL database.
 * Result result = null;
 * try
 * {
 * 	result = connector.handleSelectQuery(query);
 * 
 * } catch (SQLException | IlegalQueryException e)
 * {
 * 	e.printStackTrace();
 * }
 * 
 * // Handle result from MySQL database.
 * for (int rowNumber = 0; rowNumber &lt; result.size(); rowNumber++)
 * {
 * 	// goes through rows
 * 
 * 	for (int columnNumber = 0; columnNumber &lt; result.getRow(rowNumber).size(); columnNumber++)
 * 	{
 * 		// goes through columns
 * 	}
 * }
 * </pre>
 */
public class Connector
{
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet result = null;

	private String url;
	private String user;
	private String password;

	/**
	 * @param url
	 * @param user
	 * @param password
	 */
	public Connector(String url, String user, String password)
	{
		this.url = url;
		this.user = user;
		this.password = password;
	}

	/**
	 * Reading settings file and creating Connector according to it
	 * 
	 * <p>
	 * <b>Settings file [example]:</b>
	 * <p>
	 * jdbc:mysql://localhost:3306/DatabaseName <br>
	 * UserName<br>
	 * Password
	 * </p>
	 * </p>
	 * 
	 * @param settingsFileName
	 * @throws IOException
	 */
	public Connector(String settingsFileName) throws IOException
	{
		setConnectorSettingsFromFile(settingsFileName);
	}

	/**
	 * This function read user, password, url from settings file for DB
	 * 
	 * @param settingsFileName
	 * @throws IOException
	 */
	protected void setConnectorSettingsFromFile(String settingsFileName) throws IOException
	{
		// read password, user, url from file.
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(settingsFileName));
			this.url = in.readLine();
			this.user = in.readLine();
			this.password = in.readLine();

		} finally
		{
			in.close();
		}
	}

	/**
	 * Opens connection to MySQL database
	 * 
	 * @throws SQLException
	 */
	public void openConnection() throws SQLException
	{
		connection = (Connection) DriverManager.getConnection(url, user, password);
	}

	/**
	 * Closing connection to MySQL database
	 * 
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException
	{
		if (connection != null)
		{
			connection.close();
		}
	}

	/**
	 * Checks if connection to Database is open
	 * 
	 * @return true if connected else false
	 * @throws SQLException
	 */
	public boolean isConnected() throws SQLException
	{
		boolean isConnected = false;

		if (connection != null)
		{
			if (connection.isClosed())
			{
				isConnected = false;
			} else
			{
				isConnected = true;
			}
		}

		return isConnected;
	}

	/**
	 * Insert, Update, Drop, Delete, Create, Alter query to MySQL database.
	 * 
	 * @param query
	 * @throws SQLException
	 * @throws IlegalQueryException
	 */
	public void handleQuery(Query query) throws SQLException, IlegalQueryException
	{
		if (query.isQueryValid())
		{
			try
			{
				if (!isConnected())
				{
					openConnection();
				}

				preparedStatement = (PreparedStatement) connection.prepareStatement(query.toString());
				updatePreparedStatementWithParameters(preparedStatement, query);
				preparedStatement.executeUpdate();

			} finally
			{
				if (preparedStatement != null)
				{
					preparedStatement.close();
				}
				if (connection != null)
				{
					connection.close();
				}
			}
		} else
		{
			throw new IlegalQueryException("Ilegal query!");
		}
	}

	/**
	 * Select query to MySQL database.
	 * 
	 * @param query
	 * @throws SQLException
	 * @throws IlegalQueryException
	 */
	public Result handleSelectQuery(Query query) throws SQLException, IlegalQueryException
	{
		Result mySQLResult = new Result();

		if (query.isQueryValid())
		{
			try
			{
				if (!isConnected())
				{
					openConnection();
				}

				preparedStatement = (PreparedStatement) connection.prepareStatement(query.toString());
				updatePreparedStatementWithParameters(preparedStatement, query);
				result = preparedStatement.executeQuery();
				ResultSetMetaData resultMetaData = (ResultSetMetaData) result.getMetaData();

				int maxNumberOfColums = resultMetaData.getColumnCount();

				while (result.next())
				{
					DataBaseRow row = new DataBaseRow();
					for (int columnNumber = 1; columnNumber <= maxNumberOfColums; columnNumber++)
					{
						DataBaseColumn column = new DataBaseColumn();

						column.setColumnName(resultMetaData.getColumnName(columnNumber));
						column.setColumnValue(result.getString(columnNumber));

						row.addColumn(column);
					}

					mySQLResult.addRow(row);
				}

			} finally
			{
				if (result != null)
				{
					result.close();
				}
				if (preparedStatement != null)
				{
					preparedStatement.close();
				}
				if (connection != null)
				{
					connection.close();
				}
			}
		} else
		{
			throw new IlegalQueryException("Ilegal query!");
		}

		return mySQLResult;
	}

	/**
	 * Creating prepared statement in order to avoid SQL injections
	 * 
	 * @param preparedStatement
	 * @param query
	 * @throws SQLException
	 * @throws IlegalQueryException
	 */
	private void updatePreparedStatementWithParameters(PreparedStatement preparedStatement, Query query) throws SQLException, IlegalQueryException
	{
		// Databases counts from 1 and not 0 thats why there is (i + 1).
		for (int i = 0; i < query.getNumberOfParameters(); i++)
		{
			if (query.getParameterType(i) == Query.ParameterType.text)
			{
				preparedStatement.setString(i + 1, query.getParameterValue(i));

			} else if (query.getParameterType(i) == Query.ParameterType.intNumber)
			{
				preparedStatement.setInt(i + 1, Integer.parseInt(query.getParameterValue(i)));

			} else if (query.getParameterType(i) == Query.ParameterType.doubleNumber)
			{
				preparedStatement.setDouble(i + 1, Double.parseDouble(query.getParameterValue(i)));

			} else if (query.getParameterType(i) == Query.ParameterType.floatNumber)
			{
				preparedStatement.setFloat(i + 1, Float.parseFloat(query.getParameterValue(i)));

			} else
			{
				throw new IlegalQueryException("Invalid type of Query.ParameterType!");
			}
		}
	}
}
