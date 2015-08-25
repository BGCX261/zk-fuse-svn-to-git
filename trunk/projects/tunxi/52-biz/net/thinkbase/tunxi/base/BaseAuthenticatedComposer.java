package net.thinkbase.tunxi.base;

import org.zkoss.zk.ui.Page;

import net.thinkbase.tunxi.biz.model.UserAccount;

/**
 * 代表一个需要登录认证的 Composer
 * @author thinkbase.net
 */
public class BaseAuthenticatedComposer extends BaseComposer {
	protected UserAccount currentUser;

	@Override
	protected boolean onBeforeCompose(Page page)throws Exception{
		UserAccount user = (UserAccount
				) page.getDesktop().getSession().getAttribute(UserAccount.class.getName());
		if (null!=user){
			currentUser = user;
			return super.onBeforeCompose(page);
		}else{
			page.getDesktop().getExecution().sendRedirect("/login.zul?relogin");
			return false;	//当前组件不再被初始化
		}		
	}
}
