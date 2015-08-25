package net.thinkbase.tunxi.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;

import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;
import net.java.ao.Transaction;
import net.sf.navel.beans.ProxyFactory;
import net.thinkbase.tunxi.system.EmbeddedDerbyDatabase;
import net.thinkbase.util.ExUtility;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

import com.sun.rowset.CachedRowSetImpl;

/**
 * ActiveObjects 数据库访问对象
 * @author thinkbase.net
 */
public class ActiveObjects {
	/** 在保存时需要被忽略的 ActiveObjects 实体对象的属性字段名称 */
	private static final String[] SKIPPED_ENTITY_FIELDS = new String[]{
		"ID",
		"class", "entityManager", "entityType"
	};

	private static ThreadLocal<EntityManager> threadClient = new ThreadLocal<EntityManager>();
	
	private static EmbeddedDerbyDatabase server;
	public static void registerServer(EmbeddedDerbyDatabase server){
		ActiveObjects.server = server;
	}
	
	public static Object doAction(final Action action){
		try {
			return _doAction(action);
		} catch (Exception e) {
			if (e instanceof RuntimeException){
				throw (RuntimeException)e;
			}
			throw new RuntimeException(e);
		}
	}
	private static Object _doAction(final Action action) throws Exception{
		EntityManager client = null;
		Object result;

		client = threadClient.get();
		if (null==client){
			client = new EntityManager(ActiveObjects.server.getProvider());
			threadClient.set(client);

			try{
				Transaction<Object> t = new Transaction<Object>(client){
					@Override
					protected Object run() throws SQLException {
						try {
							return action.perform(getEntityManager());
						} catch (Exception e) {
							Throwable init = ExUtility.getCause(e);
							try{
								throw init;
							}catch(SQLException se){
								throw se;
							}catch(Throwable ex){
								SQLException se = new SQLException(ex.getMessage());
								se.initCause(ex);
								throw se;
							}
						}
					}
				};
				result = t.execute();
			}finally{
				threadClient.remove();
			}
		}else{
			result = action.perform(client);
		}
		return result;
	}
	
	public static <T extends RawEntity> T createInMemory(Class<T> entityType){
		T t = ProxyFactory.createAs(entityType);
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> T saveOrUpdate(final Class<T> type, final T entity){
		return (T) doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				int id = entity.getID();
				int exist = db.count(type, "id=?", id);
				if (exist > 0){
					//Update
					Map<String, Object> values = entity2map(entity);
					T t = db.get(type, id);
					BeanUtils.populate(t, values);
					t.save();
					id = t.getID();
				}else{
					//Create
					Map<String, Object> values = entity2map(entity);
					//在数据库中创建记录
					T t = db.create(type, values);
					id = t.getID();
				}
				return db.get(type, id);
			}
		});
	}
	@SuppressWarnings("unchecked")
	private static Map<String, Object> entity2map(Entity entity){
		Map<String, Object> values;
		try {
			values = BeanUtils.describe(entity);
		} catch (Exception e) {
			ExUtility.throwRuntimeException(e);
			return null;
		}
		//除去空值和扩展属性
		List<String> removeKeys = new ArrayList<String>();
		boolean isExt = (entity instanceof EntityWithExt);
		for (Map.Entry<String, Object> en : values.entrySet()) {
			if (null==en.getValue()){
				removeKeys.add(en.getKey());
			}else if ( (isExt)&&(en.getKey().startsWith(EntityWithExt.EXT_FIELD_Prefix)) ){
				removeKeys.add(en.getKey());
			}
		}
		for (String key: removeKeys){
			values.remove(key);
		}
		//除去不应被保存的属性
		String[] skipped = SKIPPED_ENTITY_FIELDS;
		for (int i = 0; i < skipped.length; i++) {
			values.remove(skipped[i]);
		}
		//复制真实的数据类型(BeanUtils.describe() 得到的值都是字符串)
		Map<String, Object> template = new BeanMap(entity);
		for (Map.Entry<String, Object> en : values.entrySet()) {
			en.setValue(template.get(en.getKey()));
		}

		return values;
	}
	
	public static <T extends Entity> void delete(final Class<T> type, final T entity){
		doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				int id = entity.getID();
				int exist = db.count(type, "id=?", id);
				if (exist > 0){
					//Update
					T t = db.get(type, id);
					db.delete(t);
				}else{
					//Not exists
					//Do nothing
				}
				return null;
			}
			
		});
	}
	
	public static <T extends Entity> void delete(final Class<T> type, final int id){
		doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				int exist = db.count(type, "id=?", id);
				if (exist > 0){
					//Update
					T t = db.get(type, id);
					db.delete(t);
				}else{
					//Not exists
					//Do nothing
				}
				return null;
			}
			
		});
	}
	
	public static RowSet query(final String sql){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rest = null;
		
		CachedRowSet rs = null;
		try {
			conn = ActiveObjects.server.getProvider().getConnection();
			stmt = conn.createStatement();
			rest = stmt.executeQuery(sql);
			
			rs = new CachedRowSetImpl();
			rs.populate(rest);
		} catch (Throwable e) {
			ExUtility.throwRuntimeException(e);
		} finally{
			try{if (null!=rest){rest.close();}}catch(Throwable t){/*Ignore it*/};
			try{if (null!=stmt){stmt.close();}}catch(Throwable t){/*Ignore it*/};
			try{if (null!=conn){conn.close();}}catch(Throwable t){/*Ignore it*/};
		}
		return rs;
	}
}
