package net.thinkbase.tunxi.ui;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.java.ao.EntityManager;
import net.thinkbase.tunxi.base.BaseComposer;
import net.thinkbase.tunxi.biz.model.UserAccount;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

/**
 * Login MVC 模式的事件处理对象
 * @author thinkbase.net
 *
 */
public class LoginComposer extends BaseComposer {
	private Textbox txtName;
	private Textbox txtPwd;
	private Label warningMsg;
	
	private static String COOKIE_LAST_LOGIN_USER =
		LoginComposer.class.getName()+"_last-login-user";
	private static int COOKIE_MAX_AGE = 30 * 24 * 60 * 60;	//30 天, 折算到秒

	/**
	 * 对应窗口的 create 事件, 这是不需要显式在 zul 中声明的
	 * @param event
	 */
	public void onCreate(Event event){
		txtName.setValue("");
		txtName.focus();
		
		HttpServletRequest req = (HttpServletRequest)this.execution.getNativeRequest();
		Cookie[] cookies = req.getCookies();
		if (null==cookies) cookies = new Cookie[0];
		for (int i = 0; i < cookies.length; i++) {
			if (COOKIE_LAST_LOGIN_USER.equals((cookies[i].getName()))){
				String lastUser = cookies[i].getValue();
				if ((null!=lastUser)&&(lastUser.trim().length() > 0)){
					txtName.setValue(lastUser.trim());
					txtPwd.focus();
				}
			}
		}
		
		if (this.execution.getParameterMap().containsKey("relogin")){
			warningMsg.setValue("未登录, 或者会话超时, 请重新登录");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onLogin(Event event) throws InterruptedException{
		String name = txtName.getValue();
		String pwd = txtPwd.getValue();
		
		Cookie cookie = new Cookie(COOKIE_LAST_LOGIN_USER, name);
		cookie.setMaxAge(COOKIE_MAX_AGE);
		HttpServletResponse resp = (HttpServletResponse)this.execution.getNativeResponse();
		resp.addCookie(cookie);
		
		UserAccount user = doLogin(name, pwd);
		if (null!=user){
			this.sessionScope.put(UserAccount.class.getName(), user);
			this.execution.sendRedirect("/main.zul");
		}else{
			Messagebox.show(
					"登录失败: 用户名或者密码错误", "登录失败",
					Messagebox.OK, Messagebox.EXCLAMATION);
			txtName.select();
			txtName.focus();
		}
	}
	@SuppressWarnings("unchecked")
	public void onCancel(Event event){
		SorryComposer.saySorry("用户选择取消登录", execution);
	}
	
	private UserAccount doLogin(final String name, final String pwd){
		Object res = ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception{
				UserAccount[] users = db.find(
						UserAccount.class, "loginName=? AND password=?", name, pwd);
				if (users.length < 1){
					return null;
				}else{
					return users[0];
				}
			}
		});
		return (UserAccount)res;
	}
}
