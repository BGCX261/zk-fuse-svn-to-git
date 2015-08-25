package net.thinkbase.tunxi.base.crud;

import java.util.ArrayList;
import java.util.List;

import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.base.BaseAuthenticatedComposer;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.tunxi.data.Entity4Detail;
import net.thinkbase.tunxi.data.EntityWithExt;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.DataBinder;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;

/**
 * Master-Detail 编辑窗口的通用基类
 */
public abstract class CRUDEditComposer<T extends EntityWithExt, D extends Entity4Detail> extends BaseAuthenticatedComposer {
	private DataBinder annBinder;
	private T entity;
	private List<D> lines;
	private List<D> delLines;
	private boolean exchanegInited = false;
	
	protected abstract CRUDEditHandler<T, D> getEditHdl();
	
	@SuppressWarnings({ "unchecked" })
	private void initExchange(){
		if (!exchanegInited){
			ExchangeStruct<T, D> es = (ExchangeStruct<T, D>)
				this.arg.get(CRUDListComposer.KEY_ARG_EDIT_EXCHANGE_STRUCT);
			this.entity = es.master;
			this.lines = es.lines;
			this.delLines = es.deletedLines;
			
			exchanegInited = true;
		}
	}
	protected T getMaster(){
		initExchange();
		return entity;
	}
	protected List<D> getDetails(){
		initExchange();
		if (null==lines){
			lines = new ArrayList<D>();
			
			ActiveObjects.doAction(new Action(){
				public Object perform(EntityManager db) throws Exception {
					int mid = getMaster().getID();
					D[] la = db.find(
							getEditHdl().getDetailType(),
							Query.select().where(
									CRUDMainHandler.RELATION_FIELD + "=?", new Object[]{mid})
									.order(CRUDMainHandler.SEQUENCE_FIELD));
					for (int i = 0; i < la.length; i++) {
						lines.add(la[i]);
					}
					return null;
				}
			});
		}
		return lines;
	}
	private List<D> getDelLines(){
		initExchange();
		if (null==delLines){
			delLines = new ArrayList<D>();
		}
		return delLines;
	}
	protected DataBinder getBinder(){
		if (null==annBinder){
			annBinder = new AnnotateDataBinder(self);
		}
		return annBinder;
	}

	public void onCreate(Event e) throws Exception{
		T t = this.getMaster();
		this.getEditHdl().edit_onMasterBinding(t);
		
		List<D> ds = this.getDetails();
		for(int i=0; i<ds.size(); i++){
			this.getEditHdl().edit_onDetailBinding(ds.get(i));
		}
		reBindData();
	}
	protected void reBindData() {
		T t = this.getMaster();
		List<D> ds = this.getDetails();
		getBinder().bindBean(this.getEditHdl().edit_getMasterBindName(), t);
		getBinder().bindBean(this.getEditHdl().edit_getDetailBindName(), ds);
		getBinder().loadAll();
	}
	@SuppressWarnings("unchecked")
	public void onSubmit(Event e) throws Exception{
		getBinder().saveAll();
		
		//填充返回值
		ExchangeStruct<T, D> rs = (ExchangeStruct<T, D>)
			this.arg.get(CRUDListComposer.KEY_ARG_EDIT_EXCHANGE_STRUCT); ;
		rs.master = this.getMaster();
		rs.lines = this.getDetails();
		rs.deletedLines = this.getDelLines();
		
		this.self.detach();
	}
	public void onCancel(Event e) throws Exception{
		int answer = Messagebox.show(
				"确认要放弃当前编辑的数据并关闭窗口吗?", "确认放弃编辑",
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, Messagebox.NO);
		if (Messagebox.NO == answer){
			return;
		}
		this.arg.remove(CRUDListComposer.KEY_ARG_EDIT_EXCHANGE_STRUCT);
		this.self.detach();
	}
	public void onNewLine(Event e) throws Exception{
		int maxLines = getEditHdl().edit_getMaxDetailLines();
		if ( (maxLines > 0) && (lines.size() >= maxLines) ){
			Messagebox.show(
					"单据中明细行数不能多于 "+maxLines+" 条.", "新增明细",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		
		D newLine = ActiveObjects.createInMemory(getEditHdl().getDetailType());
		newLine.setHeaderId(this.getMaster().getID());
		newLine.setSeq(this.getCurrMaxSeq() + 1);
		this.getEditHdl().edit_prepareNewDetail(newLine, this.getMaster());
		
		lines.add(newLine);
		this.getEditHdl().edit_onDetailBinding(newLine);
		reBindData();
	}
	private int getCurrMaxSeq(){
		List<D> ds = this.getDetails();
		int max = 0;
		for(int i=0; i<ds.size(); i++){
			int seq = ds.get(i).getSeq();
			if (max < seq){
				max = seq;
			}
		}
		return max;
	}
	@SuppressWarnings("unchecked")
	public void onDelLine(Event e) throws Exception{
		List<Integer> delLns = new ArrayList<Integer>();
		
		Grid grd = this.getEditHdl().edit_getDetailGrid();
		List<Row> rows = grd.getRows().getChildren();
		for(int i=0; i<rows.size(); i++){
			Checkbox ck = (Checkbox) rows.get(i).getFirstChild();
			if (ck.isChecked()){
				delLns.add(i);
			}
		}
		
		if (delLns.size() < 1){	//说明没有选择删除记录
			return;
		}
		int answer = Messagebox.show(
				"确定要删除所选择的 "+delLns.size()+" 条明细记录吗?", "删除确认",
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, Messagebox.NO);
		if (Messagebox.NO == answer){
			return;
		}
		//移走被删除的数据, 并且将需要在数据库中删除的数据保存起来
		List<D> dtls = this.getDetails();
		List<D> dels = this.getDelLines();
		for(int i=delLns.size()-1; i>=0; i--){
			int index = delLns.get(i);
			D d = dtls.get(index);
			dtls.remove(index);
			if (0!=d.getID()){	//==0 说明是新增未保存的
				dels.add(d);
			}
		}
		//重算顺序
		for(int i=0; i<dtls.size(); i++){
			dtls.get(i).setSeq(i+1);
		}
		getBinder().bindBean(this.getEditHdl().edit_getDetailBindName(), dtls);
		getBinder().loadComponent(grd);
	}
	
	protected static class ExchangeStruct<T extends EntityWithExt, D extends Entity4Detail>{
		protected T master;
		protected List<D> lines;
		protected List<D> deletedLines;
	}
}
