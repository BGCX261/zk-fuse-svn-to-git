package net.thinkbase.tunxi.ui.biz.master;

import net.thinkbase.tunxi.base.CRUDSmallMasterComposer;
import net.thinkbase.tunxi.biz.model.Product;

import org.zkoss.zul.Button;

public class ProductComposer extends CRUDSmallMasterComposer<Product> {
	private Button btnSave, btnDel;

	@Override
	protected String getDefaultOrderBy() {
		return "code";
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
	protected Class<Product> getType() {
		return Product.class;
	}

	@Override
	protected void prepareNewItem(Product t) {
		t.setName("新产品");
		t.setAbs("新产品");
		t.setCode("新产品");
		t.setHint("新产品");
	}

	@Override
	protected String toString(Product t) {
		return t.getName();
	}

}
