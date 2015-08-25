package net.thinkbase.tunxi.ui.biz.process;

import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

import net.thinkbase.tunxi.base.crud.CRUDFindComposer;

public abstract class GeneralOrderFindComposer extends CRUDFindComposer<GeneralOrderQueryCondition> {
	private Textbox keywords;
	private Datebox dateFrom, dateTo;

	@Override
	protected void applyCondition(GeneralOrderQueryCondition cond) {
		keywords.setValue(cond.keywords);
		dateFrom.setValue(cond.dateFrom);
		dateTo.setValue(cond.dateTo);
	}

	@Override
	protected void rebuildCondition(GeneralOrderQueryCondition cond) {
		cond.keywords = keywords.getValue();
		cond.dateFrom = dateFrom.getValue();
		cond.dateTo = dateTo.getValue();
	}

	@Override
	protected abstract GeneralOrderQueryCondition getDefaultCondition();
}
