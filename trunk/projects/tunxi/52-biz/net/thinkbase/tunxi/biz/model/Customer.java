package net.thinkbase.tunxi.biz.model;

import net.java.ao.Entity;
import net.java.ao.schema.Unique;

/**
 * 客户
 * @author thinkbase.net
 */
public interface Customer extends Entity {
	/** 编号 */
	@Unique
	public String getCode();
	public void setCode(String code);
	/** 全称 */
	@Unique
	public String getName();
	public void setName(String name);
	/** 简称 */
	public String getAbs();
	public void setAbs(String abs);
	/** 助记码 */
	public String getHint();
	public void setHint(String hint);
	
	/** 地址 */
	public String getAddress();
	public void setAddress(String addr);
	/** 联系方式 */
	public String getContactInfo();
	public void setContactInfo(String info);
	/** 描述 */
	public String getRemark();
	public void setRemark(String remark);
}
