package net.thinkbase.tunxi.biz.model;

import net.java.ao.Entity;
import net.java.ao.schema.Unique;

/**
 * 产品
 * @author thinkbase.net
 */
public interface Product extends Entity {
	/** 编号 */
	@Unique
	public String getCode();
	public void setCode(String code);
	/** 品名 */
	@Unique
	public String getName();
	public void setName(String name);
	/** 简称 */
	public String getAbs();
	public void setAbs(String abs);
	/** 助记码 */
	public String getHint();
	public void setHint(String hint);
	
	/** 规格 */
	public String getSpec();
	public void setSpec(String spec);
	/** 计量单位 */
	public String getUom();
	public void setUom(String uom);
	
	/** 描述 */
	public String getRemark();
	public void setRemark(String remark);
}
