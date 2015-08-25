package net.thinkbase.tunxi.ui.biz.process;

import net.thinkbase.tunxi.ui.biz.process.GeneralOrderQueryCondition.OrderType;

/**
 * CO 查找对话框
 * @author thinkbase.net
 */
public class COFindComposer extends GeneralOrderFindComposer {
	@Override
	protected GeneralOrderQueryCondition getDefaultCondition() {
		return new GeneralOrderQueryCondition(OrderType.CO);
	}

}
