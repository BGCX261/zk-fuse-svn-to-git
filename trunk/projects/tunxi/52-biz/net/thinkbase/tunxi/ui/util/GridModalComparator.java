package net.thinkbase.tunxi.ui.util;

import java.util.Comparator;
import java.util.HashMap;

import net.thinkbase.util.ReflectUtil;

import org.apache.log4j.Logger;

/**
 * 如果直接将对象的集合作为模型绑定到 Grid, 那么在排序时传入的就是相应的对象实例, 本比较
 * 器即用于这种情况下的比较
 * @author thinkbase.net
 */
public class GridModalComparator implements Comparator {
	private static final Logger log = Logger.getLogger(GridModalComparator.class);
	
	private boolean _asc = true;
	private String _field = null;
	
	private GridModalComparator root;

	/**
	 * 建立一个正向或者反向排序的比较器(这个构造出来的比较器仅仅是一个模板)
	 */
	public GridModalComparator(){
	}
	
	private GridModalComparator(GridModalComparator root, boolean asc){
		this.root = (null==root.root)?root:root.root;
		this._asc = asc;
	}
	/**
	 * 获得一个与具体字段相关的比较器集合(正向), 这样就可以使用类似 ${cmp.asc['字段名']} 
	 * 或者 ${cmp.desc.字段名}这样的方式来获得一个对应到具体某字段的比较器了.
	 * @return
	 */
	public DynamicMap getAsc(){
		return new DynamicMap(true);
	}
	/**
	 * 获得一个与具体字段相关的比较器集合(反向), 这样就可以使用类似 ${asc.columns['字段名']} 这样
	 * 的方式来获得一个对应到具体某字段的比较器了.
	 * @return
	 */
	public DynamicMap getDesc(){
		return new DynamicMap(false);
	}
	
	@SuppressWarnings("unchecked")
	public int compare(Object o1, Object o2) {
		if (null==_field) return 0;
		root._field = this._field;
		root._asc = this._asc;
		try{
			Comparable f1 = (Comparable)getFieldValueAsBean(o1);
			Comparable f2 = (Comparable)getFieldValueAsBean(o2);

			int i = f1.compareTo(f2);
			return _asc ? i: -i;
		}catch(Exception ex){
			log.error(ex.getClass().getName() + ": " + ex.getMessage());
			return 0;
		}
	}
	private Object getFieldValueAsBean(Object o){
		String f = _field.substring(0, 1).toUpperCase();
		if (_field.length() > 1){
			f += _field.substring(1);
		}
		String pm = "get" + f;
		try {
			return ReflectUtil.callMethod(o, pm, null);
		} catch (Exception e) {
			pm = "is" + f;
			return ReflectUtil.callMethod(o, pm, null);
		}
	}
	
	private class DynamicMap extends HashMap<String, GridModalComparator>{
		private static final long serialVersionUID = 1L;
		private boolean asc;
		
		public DynamicMap(boolean asc){
			this.asc = asc;
		}
		@Override
		public GridModalComparator get(Object key) {
			GridModalComparator c = new GridModalComparator(
					GridModalComparator.this, this.asc);
			c._field = (String)key;
			return c;
		}
	}
}
