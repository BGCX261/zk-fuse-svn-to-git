package net.thinkbase.tunxi.system;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.java.ao.DatabaseProvider;
import net.java.ao.PoolProvider;
import net.java.ao.db.EmbeddedDerbyDatabaseProvider;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/**
 * 嵌入式的 Derby 数据源
 * @author thinkbase.net
 */
public class EmbeddedDerbyDatabase {
	private static final Logger logger = Logger.getLogger(EmbeddedDerbyDatabase.class);
	
	private static final String DEFAULT_USER = "default";
	private static final String DEFAULT_PWD = "6743528196119291326L";
	
	private DatabaseProvider provider;
	
	private EmbeddedDerbyDatabase(DatabaseProvider dp){
		//Private constructor to prevent new instance directly
		this.provider = dp;
	}
	
	public static EmbeddedDerbyDatabase newInstance(File dataDir, int maxPoolSize) throws PropertyVetoException, IOException{
		String url = "jdbc:derby:" + dataDir.getCanonicalPath();
		if (! dataDir.exists()){
			url += ";create=true";
		}
		logger.info("Build datasource from url '"+url+"' ...");
		
		DatabaseProvider dp = new EmbeddedDerbyDatabaseProvider(url, DEFAULT_USER, DEFAULT_PWD);
		dp = new C3P0Provider(dp, maxPoolSize);
		EmbeddedDerbyDatabase db = new EmbeddedDerbyDatabase(dp);

		return db;
	}
	
	public DatabaseProvider getProvider(){
		return this.provider;
	}
	
	private static class C3P0Provider extends PoolProvider{
		private ComboPooledDataSource cpds;
		public C3P0Provider(DatabaseProvider delegate, int maxPoolSize){
			super(delegate);
			
			cpds = new ComboPooledDataSource();
			try {
				cpds.setDriverClass(delegate.getDriverClass().getCanonicalName());
			} catch (PropertyVetoException e) {
			} catch (ClassNotFoundException e) {
			}
			cpds.setJdbcUrl(getURI());
			cpds.setUser(getUsername());
			cpds.setPassword(getPassword());
			
			cpds.setMinPoolSize(1);
			cpds.setMaxPoolSize(maxPoolSize);
			cpds.setMaxStatements(180);
		}
		
		@Override
		protected Connection getConnectionImpl() throws SQLException {
			return cpds.getConnection();
		}
		@Override
		public void dispose() {
			super.dispose();
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
			}
		}
	}
}
