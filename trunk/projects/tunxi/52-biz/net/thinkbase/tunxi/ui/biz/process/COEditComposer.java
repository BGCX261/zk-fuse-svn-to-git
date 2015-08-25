package net.thinkbase.tunxi.ui.biz.process;

import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.base.crud.CRUDEditComposer;
import net.thinkbase.tunxi.base.crud.CRUDEditHandler;
import net.thinkbase.tunxi.base.crud.EntityDataCache;
import net.thinkbase.tunxi.base.crud.FieldVisitor;
import net.thinkbase.tunxi.base.ui.ComboboxIntboxBinding;
import net.thinkbase.tunxi.biz.model.CO;
import net.thinkbase.tunxi.biz.model.COL;
import net.thinkbase.tunxi.biz.model.Customer;
import net.thinkbase.tunxi.biz.model.Product;
import net.thinkbase.tunxi.biz.model.ProductPrice;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Combobox;
import org.zkoss.zul.api.Comboitem;

public class COEditComposer extends CRUDEditComposer<CO, COL> {
	private Textbox txtSerialNo;
	private Datebox date;
	private Grid detail;
	private EntityDataCache<Customer> custCache
		= new EntityDataCache<Customer>(Customer.class, Query.select().order("code"));
	private EntityDataCache<Product> prodCache
    	= new EntityDataCache<Product>(Product.class, Query.select().order("code"));

	public COEditComposer(){
		Customer c = ActiveObjects.createInMemory(Customer.class);
		c.setName("<未知>");
		custCache.setDefault(c);
		
		Product p = ActiveObjects.createInMemory(Product.class);
		p.setCode("<未知>");
		p.setName("<未知>");
		p.setSpec("<未知>");
		p.setUom("<未知>");
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
		page.setVariable("custs", new SimpleListModel(custCache.getData(
				new FieldVisitor<Customer>(){
					public Object getFieldValue(Customer data) {
						return data.getName();
					}
				})));
	}

	public void onProdChange(Event e) throws Exception{
		final ComboboxIntboxBinding cib = new ComboboxIntboxBinding(prodCache, e);
		int index = cib.getRowNo();
		final int prodId = cib.getSelectedId();
		
		//初始化选择价格的下拉框
		Component row = (Component)detail.getRows().getChildren().get(index);
		final Combobox priceSel = (Combobox
				)((Component)row.getChildren().get(5)).getChildren().get(0);

		initPriceCombobox(prodId, priceSel);
		priceSel.setSelectedIndex(-1);
		Intbox priceId = (Intbox) priceSel.getNextSibling();
		priceId.setValue(null);

		//进行粒度比较细的绑定(loadAll会带来一些数据显示上的问题)
		this.getBinder().saveComponent(detail.getCell(index, 1));
		this.getBinder().saveComponent(detail.getCell(index, 5));
		this.getEditHdl().edit_onDetailBinding(this.getDetails().get(index));
		this.getBinder().loadComponent(detail.getCell(index, 2));
	}
	private void initPriceCombobox(final int prodId, final Combobox priceSel) {
		priceSel.getItems().clear();
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				ProductPrice[] prices = db.find(ProductPrice.class,
						Query.select(
							).where("prodId=?", new Object[]{prodId}
							).order("name"));
				for (int i = 0; i < prices.length; i++) {
					Comboitem item =  priceSel.appendItemApi(prices[i].getName());
					item.setValue(prices[i].getID());
				}
				
				return null;
			}			
		});
	}
	public void onCustChange(Event e) throws Exception{
		ComboboxIntboxBinding cib = new ComboboxIntboxBinding(custCache, e);
		
		//FIXME: 第一次绑定有时候会失效
		this.getMaster().setCustId(cib.getSelectedId());
	}
	public void onPriceChange(Event e) throws Exception{
		Event oe = e;
		if (e instanceof ForwardEvent){
			ForwardEvent fe = (ForwardEvent)e;
			oe = fe.getOrigin();
		}
		Combobox cbx = (Combobox) oe.getTarget();
		

		Intbox priceId = (Intbox) cbx.getNextSibling();		
		priceId.setValue((Integer)cbx.getSelectedItemApi().getValue());

		//进行粒度比较细的绑定(loadAll会带来一些数据显示上的问题)
		int rowNo = ComboboxIntboxBinding.findRowIndex(cbx.getParent());
		this.getBinder().saveComponent(detail.getCell(rowNo, 5));
	}
	public void onPriceOpen(Event e) throws Exception{
		Event oe = e;
		if (e instanceof ForwardEvent){
			ForwardEvent fe = (ForwardEvent)e;
			oe = fe.getOrigin();
		}
		Combobox cbx = (Combobox) oe.getTarget();

		if (cbx.getItemCount() <= 0){
			Intbox prodId = (Intbox
					)((Component)cbx.getParent().getParent().getChildren().get(1)
					).getLastChild();
			initPriceCombobox(prodId.getValue(), cbx);
		}
	}

	@Override
	protected CRUDEditHandler<CO, COL> getEditHdl() {
		return new CRUDEditHandler<CO, COL>(){
			public Class<COL> getDetailType() {
				return COL.class;
			}
			public String edit_getMasterBindName() {
				return "co";
			}
			public String edit_getDetailBindName() {
				return "col";
			}
			public void edit_onMasterBinding(CO co) {
				co.set_Ext_CustName(custCache.find(co.getCustId()).getName());
				co.set_Ext_Stage(CO.STAGE_DESC_MAP.get(co.getStage()));
				
				if (0 != co.getID()){
					txtSerialNo.setDisabled(true);
					date.focus();
				}else{
					txtSerialNo.focus();
				}
			}
			public void edit_onDetailBinding(COL d) {
				Product p = prodCache.find(d.getProdId());
				d.set_Ext_ProdCode(p.getCode());
				d.set_Ext_ProdName(p.getName());
				d.set_Ext_ProdSpec(p.getSpec());
				d.set_Ext_ProdUom(p.getUom());
			}
			public void edit_prepareNewDetail(COL d, CO t) {
				//Do nothing
			}
			public Grid edit_getDetailGrid() {
				return detail;
			}
			public int edit_getMaxDetailLines() {
				return 4;
			}
		};
	}

}
