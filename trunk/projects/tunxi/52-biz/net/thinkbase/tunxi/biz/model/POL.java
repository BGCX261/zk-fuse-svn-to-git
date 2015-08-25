/**
 * 
 */
package net.thinkbase.tunxi.biz.model;

import java.math.BigDecimal;
import java.sql.Types;

import net.java.ao.schema.Ignore;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.SQLType;
import net.thinkbase.tunxi.data.Entity4Detail;

public interface POL extends Entity4Detail {
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
	public String getProdUom();
	public void setProdUom(String p);
	/** 计划数量 */
	@NotNull
	@SQLType(value=Types.DECIMAL, scale=3, precision=13)
	public BigDecimal getPlanQty();
	public void setPlanQty(BigDecimal q);
	/** 实收数量 */
	@NotNull
	@SQLType(value=Types.DECIMAL, scale=3, precision=13)
	public BigDecimal getQty();
	public void setQty(BigDecimal q);
	/** 车号 */
	@NotNull
	public String getTruckNo();
	public void setTruckNo(String n);
}