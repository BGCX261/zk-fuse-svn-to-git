package net.thinkbase.tunxi.ui.biz.master;

import java.math.BigDecimal;

import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.base.CRUDSmallMasterComposer;
import net.thinkbase.tunxi.biz.model.Product;
import net.thinkbase.tunxi.biz.model.ProductPrice;
import net.thinkbase.tunxi.data.AbstractEntity;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class ProductPriceComposer extends CRUDSmallMasterComposer<ProductPrice> {
	private Button btnSave, btnDel;
	private Bandbox selProd;
	private Textbox txtProdId;
	private Listbox lstProds;
	
	private Product[] prodList;

	@Override
	protected String getDefaultOrderBy() {
		return "prodId, name";
	}

	@Override
	protected Button getDeleteButton() {
		return btnDel;
	}

	@Override
	protected Button getSaveButton() {
		return btnSave;
	}

	@Override
	protected Class<ProductPrice> getType() {
		return ProductPrice.class;
	}

	@Override
	protected void prepareNewItem(ProductPrice t) {
		t.setName(null);
		t.setProdId(null);
		t.setPrice(new BigDecimal("0.00"));
	}

	@Override
	protected String toString(ProductPrice t) {
		return t.getName() + ": " + t.getPrice();
	}

	@Override
	protected void onContinueCompose(final Page page)throws Exception{
		super.onContinueCompose(page);
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				//获得所有的产品, 供选择
				prodList = db.find(Product.class, Query.select().order("code"));
				page.setVariable("ProdList", prodList);
				return null;
			}
		});
	}
	@Override
	protected void beforeBinding() {
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				//为列表数据填入对应产品的品名等相关信息
				for (int i = 0; i < listEntities.length; i++) {
					ProductPrice p = listEntities[i];
					if (! (p instanceof PriceShell)){
						PriceShell ps = new PriceShell(p, db);
						listEntities[i] = ps;
					}
				}
				return null;
			}
		});
	}
	private void syncProdList() {
		Integer prodId = null;
		if ((null==this.selectedEntity)||(null==this.selectedEntity.getProdId())){
			prodId = null;
		}else{
			prodId = this.selectedEntity.getProdId();
		}
		int size = lstProds.getItemCount();
		for(int i=0; i<size; i++){
			Listitem li = lstProds.getItemAtIndex(i);
			li.setSelected(li.getValue().equals(prodId));
		}
		if (null==prodId){
			selProd.setText(null);
		}else{
			size = this.prodList.length;
			for(int i=0; i<size; i++){
				Product p = this.prodList[i];
				if (prodId == p.getID()){
					selProd.setText(p.getCode() + " - " + p.getName());
				}
			}
		}
	}
	@Override
	public void onEdit(Event e) throws Exception{
		super.onEdit(e);
		//准备好给定的产品列表默认选择
		syncProdList();
	}
	@Override
	public void onNew(Event e) throws Exception {
		super.onNew(e);
		syncProdList();
	}
	@Override
	public void onSave(Event e) throws Exception {
		if (null==txtProdId.getValue()){
			Messagebox.show("未选择产品, 不能保存", "保存确认",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		super.onSave(e);
		syncProdList();
	}

	public void onProdSelect(Event e) throws Exception{
		SelectEvent se = (SelectEvent)((ForwardEvent)e).getOrigin();
		Listitem li = (Listitem)se.getSelectedItems().iterator().next();
		
		this.selectedEntity.setProdId((Integer)li.getValue());
		this.getBinder().loadComponent(txtProdId);
		
		selProd.closeDropdown();
		syncProdList();
	}

	public class PriceShell extends AbstractEntity implements ProductPrice{
		private ProductPrice p;
		private String pName;
		private String pUom;
		private String pSepc;

		public PriceShell(ProductPrice p, EntityManager db){
			super(p);
			this.p = p;

			Product prod = db.get(Product.class, p.getProdId());
			this.pName = prod.getName();
			this.pUom = prod.getUom();
			this.pSepc = prod.getSpec();
		}
		
		public String getProductName(){
			return this.pName;
		}
		public String getUom(){
			return this.pUom;
		}
		public String getSpec(){
			return this.pSepc;
		}

		public String getName() {
			return p.getName();
		}
		public BigDecimal getPrice() {
			return p.getPrice();
		}
		public Integer getProdId() {
			return p.getProdId();
		}
		public String getRemark() {
			return p.getRemark();
		}
		public void setName(String name) {
			p.setName(name);
		}
		public void setPrice(BigDecimal price) {
			p.setPrice(price);
		}
		public void setProdId(Integer id) {
			p.setProdId(id);
		}
		public void setRemark(String r) {
			p.setRemark(r);
		}
	}
}
