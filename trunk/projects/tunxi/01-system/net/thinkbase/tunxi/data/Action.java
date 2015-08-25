package net.thinkbase.tunxi.data;

import net.java.ao.EntityManager;

/**
 * ActiveObjects database Action
 * @author thinkbase.net
 */
public interface Action {
	public Object perform(EntityManager db) throws Exception;
}
