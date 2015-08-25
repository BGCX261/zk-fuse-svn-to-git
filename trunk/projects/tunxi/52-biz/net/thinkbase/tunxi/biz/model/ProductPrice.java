package net.thinkbase.tunxi.biz.model;

import java.math.BigDecimal;
import java.sql.Types;

import net.java.ao.Entity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.SQLType;

public interface ProductPrice extends Entity {
	/** 名称(价格类型) */
	@NotNull
	public String getName();
	public void setName(String name);
	/** 产品 ID */
	@NotNull
	public Integer getProdId();
	public void setProdId(Integer id);
	/** 价格 */
	@NotNull
	@SQLType(value=Types.DECIMAL, scale=3, precision=13)
	public BigDecimal getPrice();
	public void setPrice(BigDecimal price);
	/** 备注 */
	public String getRemark();
	public void setRemark(String r);
}
