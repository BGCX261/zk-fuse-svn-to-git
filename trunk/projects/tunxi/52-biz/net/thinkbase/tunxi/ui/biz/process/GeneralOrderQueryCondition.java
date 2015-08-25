package net.thinkbase.tunxi.ui.biz.process;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import net.java.ao.Query;
import net.thinkbase.tunxi.base.crud.QueryCondition;
import net.thinkbase.util.StringUtility;

/**
 * 针对一般单据的查找条件类
 * @author thinkbase.net
 */
public class GeneralOrderQueryCondition implements QueryCondition{
	private static final long msPerDay = 24L * 60 * 60 * 1000;
	
	protected String keywords = null;
	
	//默认查找的时间条件为前一天到当天
	protected java.util.Date dateFrom = new java.util.Date(System.currentTimeMillis() - msPerDay);
	protected java.util.Date dateTo = new java.util.Date();

	private OrderType orderType;
	
	public GeneralOrderQueryCondition(OrderType orderType){
		this.orderType = orderType;
	}
	
	public Query getQuery() {
		List<String> andClauses = new ArrayList<String>();
		List<Object> whereParams = new ArrayList<Object>();
		
		if (null!=keywords && keywords.trim().length() > 0){
			if (this.orderType == OrderType.PO){
				andClauses.add(
						"(serialNo LIKE '%'||?||'%' OR remark LIKE '%'||?||'%'" +
						" OR accId in (SELECT ID From BankAccount Where name LIKE '%'||?||'%') )");
				whereParams.add(keywords);
				whereParams.add(keywords);
				whereParams.add(keywords);
			}
			if (this.orderType == OrderType.CO){
				andClauses.add(
						"(serialNo LIKE '%'||?||'%' OR remark LIKE '%'||?||'%'" +
						" OR custId in (SELECT ID From Customer Where name LIKE '%'||?||'%') )");
				whereParams.add(keywords);
				whereParams.add(keywords);
				whereParams.add(keywords);
			}
		}
		if (null!=dateFrom){
			andClauses.add("date >= ?");
			whereParams.add(new Date(dateFrom.getTime()));
		}
		if (null!=dateTo){
			andClauses.add("date < ?");
			whereParams.add(new Date(dateTo.getTime()+msPerDay));
		}
		
		if (andClauses.size() > 0){
			String where = StringUtility.joinArray(andClauses.toArray(new String[0]), " AND ");
			Object[] params = whereParams.toArray();
			return Query.select().where(where, params).order("serialNo");
		}else{
			return Query.select().order("serialNo");
		}
	}
	
	public static enum OrderType {
		PO, CO
	}
}