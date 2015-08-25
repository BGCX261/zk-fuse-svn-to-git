package net.thinkbase.tunxi.data;

import java.beans.PropertyChangeListener;

import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

/**
 * 一个 Entity 类的抽象实体实现, 方便实现一个基于 Entity 接口的类
 * @author thinkbase.net
 */
public abstract class AbstractEntity implements Entity {
	private Entity e;

	public AbstractEntity(Entity e){
		this.e = e;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		e.addPropertyChangeListener(listener);
	}

	public EntityManager getEntityManager() {
		return e.getEntityManager();
	}

	public Class<? extends RawEntity<Integer>> getEntityType() {
		return e.getEntityType();
	}

	public int getID() {
		return e.getID();
	}

	public void init() {
		e.init();
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		e.removePropertyChangeListener(listener);
	}

	public void save() {
		e.save();
	}
}
