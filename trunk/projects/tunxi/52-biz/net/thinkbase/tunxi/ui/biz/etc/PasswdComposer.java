package net.thinkbase.tunxi.ui.biz.etc;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import net.java.ao.EntityManager;
import net.thinkbase.tunxi.base.BaseAuthenticatedComposer;
import net.thinkbase.tunxi.biz.model.UserAccount;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

public class PasswdComposer extends BaseAuthenticatedComposer {
	private Textbox txtPwd, txtPwdNew, txtPwdConfirm;
	
	@Override
	public void onContinueCompose(Page page)throws Exception{
		super.onContinueCompose(page);
		page.setVariable("user", this.currentUser);
	}

	public void onCreate(Event e) throws Exception{
		txtPwd.setFocus(true);
	}

	public void onChangePwd(Event e) throws Exception{
		String oldPwd = txtPwd.getText();
		if (null==oldPwd) oldPwd = "";
		String newPwd = txtPwdNew.getText();
		if (null==newPwd) newPwd = "";

		if (! this.currentUser.getPassword().equals(oldPwd)){
			Messagebox.show(
					"'原有密码' 输入不正确, 不能修改密码.", "修改密码",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		if (! newPwd.equals(txtPwdConfirm.getText())){
			Messagebox.show(
					"两次输入的新密码不一致, 不能修改密码.", "修改密码",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		if (newPwd.length() < 3){
			Messagebox.show(
					"密码长度不能小于 3 个字符, 请重新输入.", "修改密码",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}
		final String npwd = newPwd;
		final int id = this.currentUser.getID();
		ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				UserAccount u = db.get(UserAccount.class, id);
				u.setPassword(npwd);
				u.save();
				Messagebox.show(
						"已成功修改密码, 请重新登录.", "修改密码",
						Messagebox.OK, Messagebox.INFORMATION);
				
				currentUser.setPassword(npwd);

				return null;
			}
			
		});
	}
}
