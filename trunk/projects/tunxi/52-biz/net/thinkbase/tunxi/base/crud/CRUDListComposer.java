package net.thinkbase.tunxi.base.crud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.base.BaseAuthenticatedComposer;
import net.thinkbase.tunxi.base.crud.CRUDEditComposer.ExchangeStruct;
import net.thinkbase.tunxi.base.ui.impl.LinkedIDLabel;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.tunxi.data.Entity4Detail;
import net.thinkbase.tunxi.data.Entity4Order;
import net.thinkbase.tunxi.data.EntityWithExt;
import net.thinkbase.tunxi.ui.util.GridModalComparator;
import net.thinkbase.tunxi.ui.util.SimpleGridModelView;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 * 面向 Master-Detail 类型数据的 CRUD 操作 Composer 基类
 * @author thinkbase.net
 *
 * @param <T>
 */
public abstract class CRUDListComposer <T extends EntityWithExt, D extends Entity4Detail> extends BaseAuthenticatedComposer {
	public static final String KEY_ARG_QUERY_CONDITION = "ARG_QUERY_CONDITION";
	public static final String KEY_ARG_EDIT_EXCHANGE_STRUCT = "ARG_EDIT_EXCHANGE_STRUCT";
	
	private static final String KEY_CHECKBOX_ATTR_LINE_ID = CRUDListComposer.class.getName() + ":LINE_ID";

	private QueryCondition condition;
	protected abstract CRUDMainHandler<T, D> getMainHdl(); 
	protected abstract CRUDListHandler<T> getListHdl(); 
	private Map<Integer, Checkbox> lineCheckboxes = new HashMap<Integer, Checkbox>();

	protected String getComparatorVarName(){
		return "cmp";
	}
	@Override
	protected void onContinueCompose(final Page page) throws Exception{
		super.onContinueCompose(page);
		
		//设置默认的 Comparator
		page.setVariable(getComparatorVarName(), new GridModalComparator());
	}
	
	private Checkbox initLineCheckbox(int id){
		Checkbox cb = lineCheckboxes.get(id);
		if (null==cb){
			cb = new Checkbox();
			cb.setAttribute(KEY_CHECKBOX_ATTR_LINE_ID, id);
			lineCheckboxes.put(id, cb);
			return cb;
		}else{
			return cb;
		}
	}
	
	//较验当前的 lineCheckboxes 集合, 除去其中已经不显示的那些行
	private void validateLineCheckboxes(){
		List<Integer> detachedIDs = new ArrayList<Integer>();
		
		for(Map.Entry<Integer, Checkbox> line: lineCheckboxes.entrySet()){
			Checkbox cb = line.getValue();
			if ( (null==cb.getParent()) || (null==cb.getParent().getParent()) ){
				detachedIDs.add(line.getKey());
			}
		}
		for(int id: detachedIDs){
			lineCheckboxes.remove(id);
		}
	}
	
	protected List<Integer> getCheckedIDs() {
		validateLineCheckboxes();
		
		List<Integer> ids = new ArrayList<Integer>();
		for(Map.Entry<Integer, Checkbox> line: lineCheckboxes.entrySet()){
			Checkbox cb = line.getValue();
			if (cb.isChecked()){
				int id = (Integer) cb.getAttribute(KEY_CHECKBOX_ATTR_LINE_ID);
				if (! line.getKey().equals(id)){
					throw new RuntimeException("validateLineCheckboxes(): 程序逻辑错误");
				}
				ids.add(id);
			}
		}
		return ids;
	}
	/**
	 * 供子类使用的方法, 用于执行记录状态的变更
	 * @param statusMap 状态的代码~描述
	 * @param acceptStatus 可以执行状态变更的允许记录状态
	 * @param toStatus 变更后的状态
	 * @param title 用于变更过程中各种对话框的标题
	 * @param warnMsg 变更警告信息
	 * @throws Exception
	 */
	protected void doStatusChange(final Map<Integer, String> statusMap, final int[] acceptStatus, final int toStatus, final String title, final String warnMsg) throws Exception{
		final List<Integer> ids = this.getCheckedIDs();
		if (ids.size() < 1){
			Messagebox.show(
					"没有选中的记录, 不能执行此操作.", title,
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		int answer = Messagebox.show(
				"共选中 "+ids.size()+" 条记录, " + warnMsg, title,
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, Messagebox.NO);
		if (Messagebox.NO == answer){
			return;
		}
		Boolean ok = (Boolean)ActiveObjects.doAction(new Action(){
			@SuppressWarnings("unchecked")
			public Object perform(EntityManager db) throws Exception {
				List<Entity4Order> ts = new ArrayList<Entity4Order>();
				for(int i=0; i<ids.size(); i++){
					int id = ids.get(i);
					T t = db.get(getMainHdl().getMasterType(), id);
					Entity4Order o = (Entity4Order)t;
					ts.add(o);
					int stage = o.getStage();
					if (Arrays.binarySearch(acceptStatus, stage) < 0){
						Messagebox.show(
								"选中了处于 '"+statusMap.get(stage)+"' 状态的记录," +
								" 不能执行当前操作, 请检查数据后重新执行此操作.",
								title, Messagebox.OK, Messagebox.EXCLAMATION);
						return false;
					}
				}
				for (int i = 0; i < ts.size(); i++) {
					ts.get(i).setStage(toStatus);
					ts.get(i).save();
				}
				return true;
			}
		});
		
		if (ok){
			this.onCreate(null);
		}
	}


	public void onCreate(Event e) throws Exception{
		//this.lineCheckboxes.clear();
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				if (null==condition){
					condition = getListHdl().list_getDefaultCondition();
				}
				Query q;
				if (null==condition){
					q = Query.select();
				}else{
					q = condition.getQuery();
				}
				
				T[] ts = db.find(getMainHdl().getMasterType(), q);
				for (int i = 0; i < ts.length; i++) {
					getListHdl().list_prepareMaster(ts[i]);
				}
				Grid list = getListHdl().list_getMainGrid();
				list.setModel(new SimpleGridModelView<T>(list, ts){
					private static final long serialVersionUID = 20090221L;
					@Override
					protected Component[] cells(T data) throws Exception {
						Component[] cells = getListHdl().list_buildListRow(data);
						
						Component[] tmp = new Component[cells.length + 1];
						tmp[0] = initLineCheckbox(data.getID());;
						for(int i=0; i<cells.length; i++){
							tmp[i+1] = cells[i];
							if (cells[i] instanceof LinkedIDLabel){
								cells[i].addForward(null, (String)null, "onLineClick");
							}
						}
						
						return tmp;
					}
				});
				return null;
			}	
		});
	}
	@SuppressWarnings("unchecked")
	public void onLineClick(Event e) throws Exception{
		final LinkedIDLabel id = new LinkedIDLabel(e);
		
		T t = (T)ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				return db.get(getMainHdl().getMasterType(), id.getID());
			}
		});
		openEditor(t, null, null, e);
	}
	
	private static int FLAG_VALIDATE_RIGHT = 0;
	private static int FLAG_VALIDATE_RETRY = 1;
	private static int FLAG_VALIDATE_CANCEL = 2;
	private int doSavingValidate(T t, List<D> ds) throws InterruptedException{
		try {
			getMainHdl().onSaving(t, ds);
			return FLAG_VALIDATE_RIGHT;
		} catch (ValidateException e) {
			int answer = Messagebox.show(
					"输入错误: " + e.getMessage(), "输入错误, 是否重试?",
					Messagebox.YES|Messagebox.NO, Messagebox.EXCLAMATION,
					Messagebox.YES);
			if (Messagebox.YES == answer){
				return FLAG_VALIDATE_RETRY;
			}else{
				return FLAG_VALIDATE_CANCEL;
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void openEditor(T t, List<D> ds, List<D> deleted, Event e) throws InterruptedException, Exception {
		Map<String, Object> args = new HashMap<String, Object>();
		ExchangeStruct<T, D> rs = new ExchangeStruct<T, D>();	//准备交换数据
		rs.master = t;
		rs.lines = ds;
		rs.deletedLines = deleted;
		args.put(KEY_ARG_EDIT_EXCHANGE_STRUCT, rs);

		Window win = (Window)execution.createComponents(
				getListHdl().list_getEditWinZul(), null, args);
		win.doModal();
		
		ExchangeStruct<T, D> ret = (ExchangeStruct<T, D>) args.get(KEY_ARG_EDIT_EXCHANGE_STRUCT);
		if (null!=ret){		//null 代表用户取消编辑
			int flag = doSavingValidate(ret.master, ret.lines);
			if (FLAG_VALIDATE_RIGHT==flag){
				doSave(ret);
				Messagebox.show(
						"记录: " + getMainHdl().getMasterAsString(ret.master) + " 已保存", "保存确认",
						Messagebox.OK, Messagebox.INFORMATION);
				onCreate(e);
			}else if(FLAG_VALIDATE_RETRY==flag){
				openEditor(ret.master, ret.lines, ret.deletedLines, e);
			}else{
				//Nothing needed
			}
		}
	}
	private void doSave(final ExchangeStruct<T, D> ret){
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				T master = ret.master;
				List<D> details = ret.lines;
				List<D> deletes = ret.deletedLines;
				
				master = ActiveObjects.saveOrUpdate(getMainHdl().getMasterType(), master);
				int mid = master.getID();
				if (null!=details){
					for(D d: details){
						d.setHeaderId(mid);
						ActiveObjects.saveOrUpdate(getMainHdl().getDetailType(), d);
					}
				}
				if (null!=deletes){
					for (D d: deletes){
						if (d.getID() != 0){
							ActiveObjects.delete(getMainHdl().getDetailType(), d);
						}
					}
				}

				return null;
			}
		});
	}
	public void onFind(Event e) throws Exception{
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(KEY_ARG_QUERY_CONDITION, this.condition);
		Window win = (Window)execution.createComponents(
				getListHdl().list_getFindWinZul(), null, args);
		win.doModal();
		Object cond = args.get(KEY_ARG_QUERY_CONDITION);
		if (null!=cond){	//null 代表用户取消查找
			this.condition = (QueryCondition) cond;
			onCreate(e);
		}
	}
	public void onNew(Event e) throws Exception{
		T t = ActiveObjects.createInMemory(getMainHdl().getMasterType());
		getListHdl().list_prepareNewMaster(t);
		openEditor(t, null, null, e);
	}
	private void doDelete(final int id){
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				ActiveObjects.doAction(new Action(){
					public Object perform(EntityManager db) throws Exception {
						D[] la = db.find(
								getMainHdl().getDetailType(),
								Query.select().where(
										CRUDMainHandler.RELATION_FIELD + "=?", new Object[]{id}));
						for (int i = 0; i < la.length; i++) {
							ActiveObjects.delete(getMainHdl().getDetailType(), la[i]);
						}
						return null;
					}
				});

				ActiveObjects.delete(getMainHdl().getMasterType(), id);

				return null;
			}			
		});
	}
	public void onDel(Event e) throws Exception{
		List<Integer> ids = getCheckedIDs();
		if (ids.size() < 1){
			Messagebox.show(
					"没有选中的记录, 不能执行此操作.", "单据删除",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		int answer = Messagebox.show(
				"确定要删除所选择的 "+ids.size()+" 条记录吗?", "删除确认",
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, Messagebox.NO);
		if (Messagebox.NO == answer){
			return;
		}
		for(int i=0; i<ids.size(); i++){
			doDelete(ids.get(i));
		}
		
		onCreate(e);
	}
}
