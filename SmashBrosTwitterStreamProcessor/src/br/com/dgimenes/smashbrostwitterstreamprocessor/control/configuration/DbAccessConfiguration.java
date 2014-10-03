package br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration;

public class DbAccessConfiguration {
	private String connectionString;
	private String user;
	private String password;

	public DbAccessConfiguration(String connectionString, String user, String password) {
		super();
		this.connectionString = connectionString;
		this.user = user;
		this.password = password;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionString == null) ? 0 : connectionString.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DbAccessConfiguration other = (DbAccessConfiguration) obj;
		if (connectionString == null) {
			if (other.connectionString != null)
				return false;
		} else if (!connectionString.equals(other.connectionString))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DbAccessConfiguration [connectionString=" + connectionString + ", user=" + user + ", password="
				+ password + "]";
	}
}
