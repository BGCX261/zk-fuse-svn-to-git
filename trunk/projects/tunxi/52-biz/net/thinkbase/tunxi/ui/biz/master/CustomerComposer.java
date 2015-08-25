package net.thinkbase.tunxi.ui.biz.master;

import net.thinkbase.tunxi.base.CRUDSmallMasterComposer;
import net.thinkbase.tunxi.biz.model.Customer;

import org.zkoss.zul.Button;

public class CustomerComposer extends CRUDSmallMasterComposer<Customer> {
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
	protected Class<Customer> getType() {
		return Customer.class;
	}

	@Override
	protected void prepareNewItem(Customer t) {
		t.setName("新客户");
		t.setAbs("新客户");
		t.setCode("新客户");
		t.setHint("新客户");
	}

	@Override
	protected String toString(Customer t) {
		return t.getName();
	}

}
