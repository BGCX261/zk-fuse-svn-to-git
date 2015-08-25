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
import net.thinkbase.tunxi.biz.model.CO;
import net.thinkbase.tunxi.biz.model.COL;
import net.thinkbase.tunxi.biz.model.Customer;
import net.thinkbase.tunxi.data.ActiveObjectsUtil;
import net.thinkbase.tunxi.report.PrintCO;
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

public class COListComposer extends CRUDListComposer<CO, COL> {
	private Grid list;
	private Button btnDel, btnCFM, btnPrint;

	private EntityDataCache<Customer> custCache
    	= new EntityDataCache<Customer>(Customer.class, Query.select().order("code"));

	public void onConfirm(Event e) throws Exception{
		doStatusChange(CO.STAGE_DESC_MAP, new int[]{CO.STATUS_NORMAL}, CO.STATUS_CONFIRM,
				"单据确认", "确认要设置状态为'确认'吗? 注意! 确认后的记录不能修改.");
	}
	public void onInvalidate(Event e) throws Exception{
		doStatusChange(CO.STAGE_DESC_MAP, new int[]{CO.STATUS_NORMAL}, CO.STATUS_INVALID,
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

		String url = "/report-applet/report.html?REPORT_RUNNER=" + PrintCO.class.getName();

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

	public void onCreate(Event e) throws Exception{
		//只有管理员可以删除或者确认单据
		btnDel.setDisabled(! super.currentUser.isAdmin());
		btnCFM.setDisabled(! super.currentUser.isAdmin());

		super.onCreate(e);
	}

	@Override
	protected CRUDListHandler<CO> getListHdl() {
		return new CRUDListHandler<CO>(){
			public Component[] list_buildListRow(CO data) {
				LinkedIDLabel id = new LinkedIDLabel(data.getID(), data.getSerialNo());
				return new Component[]{
						id,
						new Label(data.get_Ext_CustName()),
						new Label(DateUtility.date2String(data.getDate(),"yyyy/MM/dd")),
						new Label(data.get_Ext_Stage()),
						new Label(data.getRemark()),
					};
			}
			public QueryCondition list_getDefaultCondition() {
				return new GeneralOrderQueryCondition(OrderType.CO);
			}
			public String list_getEditWinZul() {
				return "COEdit.zul";
			}
			public String list_getFindWinZul() {
				return "COFind.zul";
			}
			public Grid list_getMainGrid() {
				return list;
			}
			public void list_prepareMaster(CO t) {
				t.set_Ext_CustName(custCache.find(t.getCustId()).getName());
				t.set_Ext_Stage(CO.STAGE_DESC_MAP.get(t.getStage()));
			}
			public void list_prepareNewMaster(CO t) {
				try {
					String ymd = DateUtility.date2String(new java.util.Date(), "yyMMdd");
					String seq = ActiveObjectsUtil.getSerialNo("CO"+ymd, 3, "CO", "SerialNo");
					t.setSerialNo(seq);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				t.setDate(new Date(System.currentTimeMillis()));
				t.setStage(CO.STATUS_NORMAL);
				t.set_Ext_Stage(CO.STAGE_DESC_MAP.get(t.getStage()));
			}
		};
	}

	@Override
	protected CRUDMainHandler<CO, COL> getMainHdl() {
		return new COHandler(custCache);
	}

}
