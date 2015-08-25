package net.thinkbase.tunxi.base.crud;

import java.util.HashMap;
import java.util.Map;

import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

/**
 * 用于存放基础数据, 方便查找的缓存对象
 * @author thinkbase.net
 */
public class EntityDataCache<T extends Entity> {
	private T[] data;
	private Map<Integer, T> searchMap;
	private T defaultData;

	public EntityDataCache(T[] cacheData){
		this.data = cacheData;
	}
	public EntityDataCache(final Class<T> type){
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				EntityDataCache.this.data = db.find(type);
				return null;
			}
		});
	}
	public EntityDataCache(final Class<T> type, final Query query){
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				EntityDataCache.this.data = db.find(type, query);
				return null;
			}
		});
	}
	
	public void setDefault(T t){
		this.defaultData = t;
	}
	
	@SuppressWarnings("unchecked")
	public T find(int id){
		if (null==searchMap){
			searchMap = new HashMap<Integer, T>();
			T[] data = this.data;
			for (int i = 0; i < data.length; i++) {
				searchMap.put(data[i].getID(), data[i]);
			}
		}
		T t = (T)searchMap.get(id);
		if (null==t){
			return this.defaultData;
		}else{
			return t;
		}
	}
	public T[] getData() {
		return data;
	}
	public Object[] getData(FieldVisitor<T> fv){
		Object[] res = new Object[data.length];
		for(int i=0; i<data.length; i++){
			res[i] = fv.getFieldValue(data[i]);
		}
		return res;
	}
}
