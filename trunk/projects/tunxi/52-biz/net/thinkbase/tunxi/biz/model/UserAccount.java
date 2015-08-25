package net.thinkbase.tunxi.biz.model;

import net.java.ao.Entity;
import net.java.ao.schema.Default;
import net.java.ao.schema.Unique;

/**
 * 用户对象
 * @author thinkbase.net
 */
public interface UserAccount extends Entity {
	/** 登录名 */
	@Unique
	public String getLoginName();
	public void setLoginName(String name);
	/** 登录密码 */
	public String getPassword();
	public void setPassword(String password);
	/** 用户姓名 */
	public String getName();
	public void setName(String name);
	/** 用户描述 */
	public String getDescr();
	public void setDescr(String descr);
	/** 是否管理员 */
	@Default("false")
	public boolean isAdmin();
	public void setAdmin(boolean isAdmin);
}
