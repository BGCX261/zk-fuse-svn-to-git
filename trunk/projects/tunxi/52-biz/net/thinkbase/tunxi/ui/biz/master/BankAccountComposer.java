package net.thinkbase.tunxi.ui.biz.master;

import net.thinkbase.tunxi.base.CRUDSmallMasterComposer;
import net.thinkbase.tunxi.biz.model.BankAccount;

import org.zkoss.zul.Button;

/**
 * 对应银行帐号维护
 * @author thinkbase.net
 */
public class BankAccountComposer extends CRUDSmallMasterComposer<BankAccount> {
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
	protected void prepareNewItem(BankAccount t) {
		t.setCode("新帐户");
		t.setAbs("新帐户");
		t.setHint("新帐户");
		t.setName("新帐户");
	}

	@Override
	protected Button getSaveButton() {
		return btnSave;
	}

	@Override
	protected Class<BankAccount> getType() {
		return BankAccount.class;
	}

	@Override
	protected String toString(BankAccount t) {
		return t.getName();
	}

}
