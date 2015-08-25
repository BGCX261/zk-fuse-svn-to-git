package net.thinkbase.jasperreports;

import java.sql.SQLException;

import javax.sql.RowSet;

/**
 * 基本的数据集, 对 RowSet 的简单封装
 * @author thinkbase.net
 */
public class BaseRowSet {
	protected RowSet data;

	public BaseRowSet(RowSet data){
		this.data = data;
	}
	
	public void beforeFirst() throws SQLException{
		data.beforeFirst();
	}
	public boolean next() throws SQLException {
		return data.next();
	}
	public Object getObject(String columnName) throws SQLException{
		return data.getObject(columnName);
	}
}
