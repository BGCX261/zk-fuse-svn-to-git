package net.thinkbase.tunxi.data;

import net.java.ao.Entity;

/**
 * 适用于单据的 Entity
 * @author thinkbase.net
 */
public interface Entity4Order extends Entity {
	public int getStage();
	public void setStage(int s);
}
