package net.thinkbase.tunxi.ui.biz.process;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import net.java.ao.Query;
import net.thinkbase.tunxi.base.crud.CRUDListComposer;
import net.thinkbase.tunxi.base.crud.CRUDListHandler;
import net.thinkbase.tunxi.base.crud.CRUDMainHandler;
import net.thinkbase.tunxi.base.crud.EntityDataCache;
import net.thinkbase.tunxi.base.crud.QueryCondition;
import net.thinkbase.tunxi.base.ui.impl.LinkedIDLabel;
import net.thinkbase.tunxi.biz.model.BankAccount;
import net.thinkbase.tunxi.biz.model.PO;
import net.thinkbase.tunxi.biz.model.POL;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.tunxi.data.ActiveObjectsUtil;
import net.thinkbase.tunxi.report.PrintPO;
import net.thinkbase.tunxi.ui.biz.process.GeneralOrderQueryCondition.OrderType;
import net.thinkbase.util.DateUtility;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;

public class POListComposer extends CRUDListComposer<PO, POL> {
	private Grid list;
	private Button btnDel, btnCFM, btnPrint;
	
	private EntityDataCache<BankAccount> bankCache
	    = new EntityDataCache<BankAccount>(BankAccount.class, Query.select().order("code"));
	public POListComposer(){
		BankAccount def = ActiveObjects.createInMemory(BankAccount.class);
		def.setName("<未知>");
		bankCache.setDefault(def);
	}

	public void onCreate(Event e) throws Exception{
		//只有管理员可以删除或者确认单据
		btnDel.setDisabled(! super.currentUser.isAdmin());
		btnCFM.setDisabled(! super.currentUser.isAdmin());
		
		super.onCreate(e);
	}
	public void onConfirm(Event e) throws Exception{
		doStatusChange(PO.STAGE_DESC_MAP, new int[]{PO.STATUS_NORMAL}, PO.STATUS_CONFIRM,
				"单据确认", "确认要设置状态为'确认'吗? 注意! 确认后的记录不能修改.");
	}
	public void onInvalidate(Event e) throws Exception{
		doStatusChange(PO.STAGE_DESC_MAP, new int[]{PO.STATUS_NORMAL}, PO.STATUS_INVALID,
				"单据作废", "确认要设置状态为'作废'吗? 注意! 作废后的记录不能修改.");
	}

	@SuppressWarnings("unchecked")
	public void onPrint(Event e) throws Exception{
		final List<Integer> ids = this.getCheckedIDs();
		if (ids.size() < 1){
			Messagebox.show(
					"没有选中的记录, 不能执行此操作.", "单据打印",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		String url = "/report-applet/report.html?REPORT_RUNNER=" + PrintPO.class.getName();

		StringBuffer IDs = new StringBuffer();
		for(Integer id: ids){
			IDs.append("&ID=").append(id);
		}
		url = url + IDs;
		
		Menupopup mp = new Menupopup();
		mp.setParent(btnPrint.getParent());
		Menuitem item;
		
		item = new Menuitem("单据套打 ...");
		item.setHref(url);
		item.setTarget("_blank");
		mp.getChildren().add(item);
		
		item = new Menuitem("A4 纸打印 ...");
		item.setHref(url + "&P=A4");
		item.setTarget("_blank");
		mp.getChildren().add(item);
		
		mp.open(btnPrint);
	}
	
	private CRUDListHandler<PO> lhandler = new CRUDListHandler<PO>(){
		public String list_getFindWinZul() {
			return "POFind.zul";
		}
		public String list_getEditWinZul() {
			return "POEdit.zul";
		}
		public QueryCondition list_getDefaultCondition(){
			return new GeneralOrderQueryCondition(OrderType.PO);
		}
		public Component[] list_buildListRow(PO data) {
			LinkedIDLabel id = new LinkedIDLabel(data.getID(), data.getSerialNo());
			return new Component[]{
					id,
					new Label(data.get_Ext_AccName()),
					new Label(DateUtility.date2String(data.getDate(),"yyyy/MM/dd")),
					new Label(data.get_Ext_Stage()),
					new Label(data.getRemark()),
				};
		}
		public void list_prepareMaster(PO t) {
			t.set_Ext_AccName(bankCache.find(t.getAccId()).getName());
			t.set_Ext_Stage(PO.STAGE_DESC_MAP.get(t.getStage()));
		}
		public void list_prepareNewMaster(PO t) {
			try {
				String ymd = DateUtility.date2String(new java.util.Date(), "yyMMdd");
				String seq = ActiveObjectsUtil.getSerialNo("PO"+ymd, 3, "PO", "SerialNo");
				t.setSerialNo(seq);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
			t.setDate(new Date(System.currentTimeMillis()));
			t.setStage(PO.STATUS_NORMAL);
			t.set_Ext_Stage(PO.STAGE_DESC_MAP.get(t.getStage()));
		}
		public Grid list_getMainGrid() {
			return list;
		}
	};
	@Override
	protected CRUDListHandler<PO> getListHdl() {
		return lhandler;
	}

	@Override
	protected CRUDMainHandler<PO, POL> getMainHdl() {
		return new POHandler(bankCache);
	}
}
