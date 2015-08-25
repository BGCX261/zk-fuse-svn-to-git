package net.thinkbase.tunxi.base.crud;

import net.thinkbase.tunxi.base.BaseAuthenticatedComposer;

import org.zkoss.zk.ui.event.Event;

/**
 * 查找窗口的通用基类
 * @author thinkbase.net
 */
public abstract class CRUDFindComposer<T extends QueryCondition> extends BaseAuthenticatedComposer {
	private T condition;
	
	protected abstract T getDefaultCondition();
	protected abstract void applyCondition(T cond);
	protected abstract void rebuildCondition(T cond);

	@SuppressWarnings("unchecked")
	protected T getCondition(){
		if (null==this.condition){
			this.condition = (T) this.arg.get(CRUDListComposer.KEY_ARG_QUERY_CONDITION);
		}
		return this.condition;
	}

	public void onCreate(Event e) throws Exception{
		T cond = getCondition();
		applyCondition(cond);
	}
	
	@SuppressWarnings("unchecked")
	public void onSubmit(Event e) throws Exception{
		T cond = getCondition();
		rebuildCondition(cond);
		this.self.detach();
	}
	
	public void onReset(Event e) throws Exception{
		T def = getDefaultCondition();
		applyCondition(def);
	}
	
	public void onClose(Event e) throws Exception{
		this.arg.remove(CRUDListComposer.KEY_ARG_QUERY_CONDITION);
	}

}
