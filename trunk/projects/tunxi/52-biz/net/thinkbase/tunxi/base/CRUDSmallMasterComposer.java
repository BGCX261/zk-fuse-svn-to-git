package net.thinkbase.tunxi.base;

import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.tunxi.ui.util.GridModalComparator;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

/**
 * 为记录数量较少的基础数据表提供 CRUD 的 Composer 基类
 * @author thinkbase.net
 */
public abstract class CRUDSmallMasterComposer <T extends Entity> extends BaseAuthenticatedComposer {
	private DataBinder annBinder;
	protected T[] listEntities;
	protected T selectedEntity;

	protected abstract Class<T> getType();
	protected abstract String getDefaultOrderBy();
	protected abstract void prepareNewItem(T t);
	protected abstract String toString(T t);
	
	protected abstract Button getDeleteButton();
	protected abstract Button getSaveButton();

	protected DataBinder getBinder(){
		if (null==annBinder){
			annBinder = new AnnotateDataBinder(self);
		}
		return annBinder;
	}
	private String getListVarName(){
		return "list";
	}
	private String getItemVarName(){
		return "item";
	}
	
	@Override
	protected void onContinueCompose(final Page page) throws Exception{
		super.onContinueCompose(page);
		
		//设置默认的 Comparator
		page.setVariable("cmp", new GridModalComparator());
	}
	
	protected void beforeBinding(){
		//供子类监听, 不作任何事情
	}
	
	private void bindAll(){
		beforeBinding();
		
		getBinder().bindBean(getListVarName(), this.listEntities);
		getBinder().bindBean(getItemVarName(), this.selectedEntity);
		getBinder().loadAll();
	}
	private void fetchAll(){
		getBinder().saveAll();
	}

	public void onCreate(Event e) throws Exception{
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				listEntities = db.find(
						getType(), Query.select().order(getDefaultOrderBy()));
				return null;
			}	
		});
		this.selectedEntity = null;
		bindAll();

		getDeleteButton().setDisabled(true);
		getSaveButton().setDisabled(true);
	}
	
	public void onEdit(final Event e) throws Exception{
		Event inner = e;
		if (e instanceof ForwardEvent) {
			ForwardEvent fe = (ForwardEvent) e;
			inner = fe.getOrigin();
		}
		//FIXME: 借用的 Tooltip 的 ID 作为数据的 Key
		final int id = Integer.parseInt(((Label)inner.getTarget()).getTooltip());
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				selectedEntity = db.get(getType(), id);
				return null;
			}
		});

		bindAll();
		
		getDeleteButton().setDisabled(false);
		getSaveButton().setDisabled(false);
	}
	public void onNew(Event e) throws Exception{
		T t = ActiveObjects.createInMemory(getType());
		prepareNewItem(t);
		this.selectedEntity = t;
		bindAll();

		getDeleteButton().setDisabled(true);
		getSaveButton().setDisabled(false);
	}
	public void onSave(Event e) throws Exception{
		fetchAll();

		ActiveObjects.saveOrUpdate(getType(), this.selectedEntity);
		Messagebox.show(
				"记录: '" + toString(this.selectedEntity) + "' 已保存", "保存确认",
				Messagebox.OK, Messagebox.INFORMATION);

		onCreate(e);
	}
	public void onDelete(Event e) throws Exception{
		fetchAll();
		
		int answer = Messagebox.show(
				"确定要删除记录: '" + toString(this.selectedEntity) + "' 吗?", "删除确认",
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, Messagebox.NO);
		if (Messagebox.NO == answer){
			return;
		}
		ActiveObjects.delete(getType(), this.selectedEntity);

		onCreate(e);
	}


}
