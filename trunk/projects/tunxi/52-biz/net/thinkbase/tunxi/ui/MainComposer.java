package net.thinkbase.tunxi.ui;

import java.util.ArrayList;
import java.util.List;

import net.thinkbase.tunxi.base.BaseAuthenticatedComposer;
import net.thinkbase.tunxi.biz.model.UserAccount;

import org.apache.log4j.Logger;
import org.zkoss.zhtml.impl.AbstractTag;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

/**
 * 主窗口
 * @author thinkbase.net
 */
public class MainComposer extends BaseAuthenticatedComposer{
	private static final Logger log = Logger.getLogger(MainComposer.class);
	
	private AbstractTag currentSelected;
	private Tabbox centerTab;
	
	@Override
	protected String getPageName() {
		return "主窗口";
	}
	@Override
	public void onContinueCompose(Page page)throws Exception{
		super.onContinueCompose(page);
		page.setVariable("user", this.currentUser);
		page.setVariable("menu", this.getMenu(this.currentUser));
	}
	
	public void onLogout(Event e){
		this.sessionScope.remove(UserAccount.class.getName());
		this.execution.sendRedirect("/login.zul");
	}
	
	public void onMenuClick(Event e){
		if (null!=currentSelected){
			currentSelected.setSclass("");
		}

		ForwardEvent fe = (ForwardEvent)e;
		AbstractTag A = (AbstractTag)fe.getOrigin().getTarget();
		A.setSclass("selected");
		
		currentSelected = A;
		
		SimpleMenuItem item = (SimpleMenuItem)e.getData();
		addTab(item);
	}
	@SuppressWarnings("unchecked")
	private void addTab(SimpleMenuItem item){
		log.info("访问菜单: "+item.getCaption()+"/"+item.getHref());
		
		String URL_ATTR = "url";
		
		//检查对应 Tab 是否已经存在
		if (null!=centerTab.getTabs()){
			List<Tab> tabs = (List<Tab>)centerTab.getTabs().getChildren();
			for(int i=0; i<tabs.size(); i++){
				String href = (String)tabs.get(i).getAttribute(URL_ATTR);
				if (item.href.equals(href)){
					tabs.get(i).setSelected(true);
					return;
				}
			}	
		}
		//如果这个 Tab 原来没有打开
		Tab tab = new Tab(item.getCaption());
		tab.setClosable(true);
		tab.setSelected(true);
		tab.setAttribute(URL_ATTR, item.href);
		Tabpanel panel = new Tabpanel();
		Iframe iframe = new Iframe(item.href);
		iframe.setWidth("100%");
		iframe.setHeight("100%");
		panel.appendChild(iframe);
		
		centerTab.getTabs().appendChild(tab);
		centerTab.getTabpanels().appendChild(panel);
	}
	
	private SimpleMenu getMenu(UserAccount user){
		SimpleMenu menu = new SimpleMenu();
		
		if (user.isAdmin()){
			menu.addGroup(new SimpleMenuGroup("系统管理")
				.addMenuItem(new SimpleMenuItem("系统初始化", "", "system.init"))
				.addMenuItem(new SimpleMenuItem("用户管理", "", "biz-system/user.zul"))
			).addGroup(new SimpleMenuGroup("基础数据")
				.addMenuItem(new SimpleMenuItem("账户维护", "", "biz-master/bank.zul"))
				.addMenuItem(new SimpleMenuItem("客户维护", "", "biz-master/customer.zul"))
				.addMenuItem(new SimpleMenuItem("产品维护", "", "biz-master/product.zul"))
				.addMenuItem(new SimpleMenuItem("价格维护", "", "biz-master/price.zul"))
			);
		}
		menu.addGroup(new SimpleMenuGroup("业务处理")
			.addMenuItem(new SimpleMenuItem("要货单", "", "biz-process/POList.zul"))
			.addMenuItem(new SimpleMenuItem("送货单", "", "biz-process/COList.zul"))
		).addGroup(new SimpleMenuGroup("报表")
		    .addMenuItem(new SimpleMenuItem("日发货统计", "", "report.daily"))
		).addGroup(new SimpleMenuGroup("杂项")
		    .addMenuItem(new SimpleMenuItem("修改密码", "", "biz-etc/passwd.zul"))
		);
		
		return menu;
	}
	public static class SimpleMenu{
		private List<SimpleMenuGroup> groups = new ArrayList<SimpleMenuGroup>();
		public SimpleMenu addGroup(SimpleMenuGroup group){
			groups.add(group);
			return this;
		}
		public List<SimpleMenuGroup> getGroups() {
			return groups;
		}
	}
	public static class SimpleMenuGroup{
		private List<SimpleMenuItem> menus = new ArrayList<SimpleMenuItem>();
		private String caption;
		public SimpleMenuGroup(String caption){
			this.caption = caption;
		}
		public SimpleMenuGroup addMenuItem(SimpleMenuItem menu){
			menus.add(menu);
			return this;
		}
		public String getCaption() {
			return caption;
		}
		public List<SimpleMenuItem> getMenuItems() {
			return menus;
		}
	}
	public static class SimpleMenuItem{
		private String iconUrl;
		private String caption;
		private String href;
		//与权限管理相关的令牌, 如果为 null 则代表不作权限限制
		private String securityToken = null;
		public SimpleMenuItem(String caption, String iconUrl, String href){
			this(caption, iconUrl, href, null);
		}
		public SimpleMenuItem(String caption, String iconUrl, String href, String token){
			this.caption = caption;
			this.iconUrl = iconUrl;
			this.href = href;
			this.securityToken = token;
		}
		public String getCaption() {
			return caption;
		}
		public String getHref() {
			return href;
		}
		public String getIconUrl() {
			return iconUrl;
		}
		public String getSecurityToken() {
			return securityToken;
		}
	}
}
