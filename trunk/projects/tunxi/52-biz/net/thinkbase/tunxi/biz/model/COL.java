package net.thinkbase.tunxi.biz.model;

import java.math.BigDecimal;
import java.sql.Types;

import net.java.ao.schema.Ignore;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.SQLType;
import net.thinkbase.tunxi.data.Entity4Detail;

public interface COL extends Entity4Detail {
	/** 产品 ID */
	@NotNull
	public int getProdId();
	public void setProdId(int p);
	/** 产品编号 */
	@Ignore
	public String get_Ext_ProdCode();
	public void set_Ext_ProdCode(String p);
	/** 产品名称 */
	@Ignore
	public String get_Ext_ProdName();
	public void set_Ext_ProdName(String p);
	/** 产品规格 */
	@Ignore
	public String get_Ext_ProdSpec();
	public void set_Ext_ProdSpec(String p);
	/** 产品计量单位 */
	@Ignore
	public String get_Ext_ProdUom();
	public void set_Ext_ProdUom(String p);
	/** 数量 */
	@NotNull
	@SQLType(value=Types.DECIMAL, scale=3, precision=13)
	public BigDecimal getQty();
	public void setQty(BigDecimal q);
	/** 价格 ID */
	@NotNull
	public int getPriceId();
	public void setPriceId(int p);
	/** 价格名称 */
	@NotNull
	public String getPriceName();
	public void setPriceName(String p);
	/** 价格 */
	@NotNull
	@SQLType(value=Types.DECIMAL, scale=3, precision=13)
	public BigDecimal getPrice();
	public void setPrice(BigDecimal p);
	/** 包装件数 */
	public Integer getPkgs();
	public void setPkgs(Integer n);
}
