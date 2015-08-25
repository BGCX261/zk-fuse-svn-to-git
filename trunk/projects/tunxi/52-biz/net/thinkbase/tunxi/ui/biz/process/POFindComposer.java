package net.thinkbase.tunxi.ui.biz.process;

import net.thinkbase.tunxi.ui.biz.process.GeneralOrderQueryCondition.OrderType;

/**
 * PO 查找对话框
 * @author thinkbase.net
 */
public class POFindComposer extends GeneralOrderFindComposer {	
	@Override
	protected GeneralOrderQueryCondition getDefaultCondition() {
		return new GeneralOrderQueryCondition(OrderType.PO);
	}
}
