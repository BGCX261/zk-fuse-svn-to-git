package net.thinkbase.tunxi.ui.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zhtml.A;
import org.zkoss.zhtml.Text;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

/**
 * 一个用于表格列排序的比较器, 目前支持表格单元里面的内容为 Label 或者 A
 * @author thinkbase.net
 */
public class RowLabelComparator implements Comparator {
	private static final Logger log = Logger.getLogger(RowLabelComparator.class);
	
	private boolean asc = true;
	private int column = 0;
	
	/**
	 * 建立一个正向或者反向排序的比较器(一般来说, 这个构造出来的比较器仅仅是一个模板)
	 * @param asc =true: 正向排序; =false: 反向排序
	 */
	public RowLabelComparator(boolean asc){
		this.asc = asc;
	}
	private void setColumn(int column){
		this.column  = column;
	}
	
	/**
	 * 获得一个与具体列相关的比较器列表, 这样就可以使用类似 ${asc.columns[i]} 这样
	 * 的方式来获得一个对应到具体某列的比较器了.
	 * @return
	 */
	public DynamicList getColumns(){
		return new DynamicList();
	}
	
	public int compare(Object o1, Object o2) {
		try{
			Object l1 = ((Row)o1).getChildren().get(this.column);
			Object l2 = ((Row)o2).getChildren().get(this.column);
			String s1 = getValue(l1);
			String s2 = getValue(l2);
			int i = s1.compareTo(s2);
			return asc ? i: -i;
		}catch(Exception ex){
			log.error(ex.getClass().getName() + ": " + ex.getMessage());
			return 0;
		}
	}
	private String getValue(Object o){
		if (null==o) return "";
		if (o instanceof Label){
			return ((Label)o).getValue();
		}else if (o instanceof A){
			//找到第一个 Text 类型的子对象
			A a = (A)o;
			List children = a.getChildren();
			for(int i=0; i<children.size(); i++){
				Object c = children.get(i);
				if (c instanceof Text){
					return ((Text)c).getValue();
				}
			}
			return "";
		}else{
			return o.toString();
		}
	}

	private class DynamicList extends ArrayList<RowLabelComparator>{
		private static final long serialVersionUID = 1L;

		@Override
		public RowLabelComparator get(int index) {
			RowLabelComparator c = new RowLabelComparator(asc);
			c.setColumn(index);
			return c;
		}
	}
}
