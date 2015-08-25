package net.thinkbase.tunxi.base.ui;

import net.thinkbase.tunxi.base.crud.EntityDataCache;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;

/**
 * 通过组合使用一个下拉框和一个整数输入框实现的数据选择组件的事件响应
 * @author thinkbase.net
 */
public class ComboboxIntboxBinding {
	private Combobox combobox;
	private Intbox intbox;
	private int selectedId;

	public ComboboxIntboxBinding(EntityDataCache cache, Event e){
		Event oe = e;
		if (e instanceof ForwardEvent){
			ForwardEvent fe = (ForwardEvent)e;
			oe = fe.getOrigin();
		}
		Combobox cbx = (Combobox) oe.getTarget();
		int selIndex = cbx.getSelectedIndex();
		
		Intbox hidden = (Intbox) cbx.getNextSibling();
		int id = cache.getData()[selIndex].getID();
		hidden.setValue(id);
		
		this.combobox = cbx;
		this.intbox = hidden;
		this.selectedId = id;
	}
	
	public Combobox getCombobox(){
		return this.combobox;
	}
	public Intbox getIntbox(){
		return this.intbox;
	}
	public int getSelectedId(){
		return this.selectedId;
	}
	public int getRowNo(){
		return findRowIndex(this.combobox.getParent());
	}

	public static int findRowIndex(Component p){
		if (null==p){
			return -1;
		}else if (p instanceof Row){
			int i = 0;
			Component pp = p.getPreviousSibling();;
			while(null!=pp){
				pp = pp.getPreviousSibling();
				i++;
			}
			return i;
		}else{
			return findRowIndex(p.getParent());
		}
	}
}
