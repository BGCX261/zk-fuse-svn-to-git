package net.java.ao.types;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import net.java.ao.EntityManager;

public class BigDecimalType extends DatabaseType<BigDecimal> {

	public BigDecimalType() {
		super(Types.DECIMAL, -1, BigDecimal.class);
	}

	@Override
	public BigDecimal defaultParseValue(String value) {
		return new BigDecimal(value);
	}

	@Override
	public String getDefaultName() {
		return "decimal";
	}

	@Override
	public BigDecimal pullFromDatabase(EntityManager manager, ResultSet res, Class<? extends BigDecimal> type, String field) throws SQLException {
		return res.getBigDecimal(field);
	}

	@Override
	public void putToDatabase(int index, PreparedStatement stmt, BigDecimal value) throws SQLException {
		stmt.setBigDecimal(index, value);
	}
}
