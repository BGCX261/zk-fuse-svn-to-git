package net.thinkbase.tunxi.ui.biz.system;

import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.thinkbase.tunxi.base.BaseAuthenticatedComposer;
import net.thinkbase.tunxi.biz.model.UserAccount;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.tunxi.ui.util.GridModalComparator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zkplus.databind.TypeConverter;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 * 对应用户维护
 * @author thinkbase.net
 */
public class UserComposer extends BaseAuthenticatedComposer {
	private Textbox loginName;
	private Button btnDel, btnSave;
	
	private GridModalComparator rootCmp;
	private AnnotateDataBinder annBinder;
	private UserAccount[] allUsers;
	private UserAccount currUser;
	
	@Override
	protected void onContinueCompose(final Page page)throws Exception{
		super.onContinueCompose(page);
		
		//设置默认的 Comparator
		this.rootCmp = new GridModalComparator();
		page.setVariable("cmp", this.rootCmp);
	}
	private AnnotateDataBinder binder(){
		if (null==annBinder){
			annBinder = new AnnotateDataBinder(self);
		}
		return annBinder;
	}
	private void bindAll(){
		binder().bindBean("users", allUsers);
		binder().bindBean("editing", currUser);
		binder().loadAll();
	}
	private void fetchAll(){
		binder().saveAll();
	}
	private void findAllUsers(){
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				allUsers = db.find(
						UserAccount.class, Query.select().order("loginName"));
				return null;
			}
		});
	}
	private void loadCurrentUser(final String loginName){
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				UserAccount[] us = db.find(UserAccount.class, "loginName = ?", loginName);
				if (us.length > 0){
					currUser = us[0];
				}
				return null;
			}
		});		
	}
	private void newCurrentUser(){
		UserAccount u = ActiveObjects.createInMemory(UserAccount.class);
		u.setName("新用户");
		u.setLoginName("新用户");
		u.setPassword("password");
		this.currUser = u;
	}

	public void onCreate(Event e){
		findAllUsers();
		currUser = null;
		bindAll();

		btnDel.setDisabled(true);
		btnSave.setDisabled(true);
		loginName.setReadonly(true);
	}

	public void onUserSelect(final Event e){
		final String _loginName = ((Label)((ForwardEvent)e).getOrigin().getTarget()).getValue();
		
		loadCurrentUser(_loginName);
		bindAll();
		
		btnDel.setDisabled(false);
		btnSave.setDisabled(false);
		loginName.setReadonly(true);
	}
	public void onAddUser(Event e){
		newCurrentUser();
		bindAll();

		btnDel.setDisabled(true);
		btnSave.setDisabled(false);
		loginName.setReadonly(false);
	}
	public void onSaveUser(Event e){
		fetchAll();

		ActiveObjects.saveOrUpdate(UserAccount.class, currUser);

		onCreate(e);
	}
	public void onDelUser(Event e) throws Exception{
		fetchAll();
		
		int answer = Messagebox.show(
				"确定要删除记录: '" + currUser.getLoginName() + "' 吗?", "删除确认",
				Messagebox.YES|Messagebox.NO, Messagebox.QUESTION, Messagebox.NO);
		if (Messagebox.NO == answer){
			return;
		}
		ActiveObjects.delete(UserAccount.class, currUser);

		onCreate(e);
	}
	
	public static class AdminTypeConverter implements TypeConverter{
		public Object coerceToBean(Object val, Component comp) {
			return null;
		}
		public Object coerceToUi(Object val, Component comp) {
			boolean isAdmin = (Boolean)val;
			return isAdmin?"系统管理员":"";
		}
	}
}
