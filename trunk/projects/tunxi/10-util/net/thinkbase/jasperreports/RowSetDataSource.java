package net.thinkbase.jasperreports;

import java.sql.SQLException;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class RowSetDataSource implements JRDataSource {
	private BaseRowSet rs;
	boolean firstNext = true;

	public RowSetDataSource(BaseRowSet rs){
		this.rs = rs;
	}

	public Object getFieldValue(JRField jrField) throws JRException {
		try {
			return rs.getObject(jrField.getName());
		} catch (SQLException e) {
			throw new JRException(e);
		}
	}

	public boolean next() throws JRException {
		try {
			if (firstNext){
				rs.beforeFirst();
				firstNext = false;
			}
			return rs.next();
		} catch (SQLException e) {
			throw new JRException(e);
		}
	}

}
