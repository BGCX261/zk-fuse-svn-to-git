package net.thinkbase.tunxi.ui.biz.process;

import net.java.ao.Query;
import net.thinkbase.tunxi.base.crud.CRUDEditComposer;
import net.thinkbase.tunxi.base.crud.CRUDEditHandler;
import net.thinkbase.tunxi.base.crud.EntityDataCache;
import net.thinkbase.tunxi.base.crud.FieldVisitor;
import net.thinkbase.tunxi.base.ui.ComboboxIntboxBinding;
import net.thinkbase.tunxi.biz.model.BankAccount;
import net.thinkbase.tunxi.biz.model.PO;
import net.thinkbase.tunxi.biz.model.POL;
import net.thinkbase.tunxi.biz.model.Product;
import net.thinkbase.tunxi.data.ActiveObjects;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;

public class POEditComposer extends CRUDEditComposer<PO, POL> {
	private static final String UNKNOWN = "<未知>";
	private Textbox txtSerialNo;
	private Datebox date;
	private Grid detail;

	private EntityDataCache<BankAccount> bankCache
    	= new EntityDataCache<BankAccount>(BankAccount.class, Query.select().order("code"));
	private EntityDataCache<Product> prodCache
	    = new EntityDataCache<Product>(Product.class, Query.select().order("code"));
	
	public POEditComposer(){
		BankAccount b = ActiveObjects.createInMemory(BankAccount.class);
		b.setName(UNKNOWN);
		bankCache.setDefault(b);
		
		Product p = ActiveObjects.createInMemory(Product.class);
		p.setCode(UNKNOWN);
		p.setName(UNKNOWN);
		p.setSpec(UNKNOWN);
		p.setUom(UNKNOWN);
		prodCache.setDefault(p);
	}
	
	@Override
	protected void onContinueCompose(Page page) throws Exception {
		super.onContinueCompose(page);
		
		page.setVariable("prods", new SimpleListModel(prodCache.getData(
				new FieldVisitor<Product>(){
					public Object getFieldValue(Product data) {
						return data.getName();
					}
				})));
		page.setVariable("banks", new SimpleListModel(bankCache.getData(
				new FieldVisitor<BankAccount>(){
					public Object getFieldValue(BankAccount data) {
						return data.getName();
					}
				})));
	}
	
	public void onProdChange(Event e) throws Exception{
		ComboboxIntboxBinding cib = new ComboboxIntboxBinding(prodCache, e);
		int index = cib.getRowNo();
		
		this.getBinder().saveComponent(detail.getCell(index, 1));
		this.getEditHdl().edit_onDetailBinding(this.getDetails().get(index));
		this.getBinder().loadComponent(detail.getCell(index, 2));
	}
	public void onBankChange(Event e) throws Exception{
		ComboboxIntboxBinding cib = new ComboboxIntboxBinding(bankCache, e);
		
		//FIXME: 第一次绑定有时候会失效
		this.getMaster().setAccId(cib.getSelectedId());
	}

	private CRUDEditHandler<PO, POL> handler = new CRUDEditHandler<PO, POL>(){
		public Class<POL> getDetailType() {
			return POL.class;
		}
		public String edit_getMasterBindName() {
			return "po";
		}
		public String edit_getDetailBindName() {
			return "pol";
		}
		public void edit_onMasterBinding(PO po) {
			po.set_Ext_AccName(bankCache.find(po.getAccId()).getName());
			po.set_Ext_Stage(PO.STAGE_DESC_MAP.get(po.getStage()));
			
			if (0 != po.getID()){
				txtSerialNo.setDisabled(true);
				date.focus();
			}else{
				txtSerialNo.focus();
			}
		}
		public void edit_onDetailBinding(POL d) {
			Product p = prodCache.find(d.getProdId());
			d.set_Ext_ProdCode(p.getCode());
			d.set_Ext_ProdName(p.getName());
			d.set_Ext_ProdSpec(p.getSpec());
			if ( (null==d.getProdUom()) ||
					(UNKNOWN.equalsIgnoreCase(d.getProdUom()))){
				d.setProdUom(p.getUom());
			}
		}
		public void edit_prepareNewDetail(POL d, PO t) {
			//Do nothing
		}
		public Grid edit_getDetailGrid() {
			return detail;
		}
		public int edit_getMaxDetailLines() {
			return 5;
		}
	};
	@Override
	protected CRUDEditHandler<PO, POL> getEditHdl() {
		return handler;
	}
}
