package net.thinkbase.tunxi.base.crud;

import net.java.ao.Query;

/**
 * 代表查询条件的接口
 * @author thinkbase.net
 */
public interface QueryCondition {
	public Query getQuery();
}
