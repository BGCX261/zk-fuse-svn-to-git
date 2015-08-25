package net.thinkbase.tunxi.biz.model;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.java.ao.schema.Default;
import net.java.ao.schema.Ignore;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.SQLType;
import net.java.ao.schema.Unique;
import net.thinkbase.tunxi.data.Entity4Order;
import net.thinkbase.tunxi.data.EntityWithExt;

/**
 * 要货单
 * @author thinkbase.net
 */
public interface PO extends EntityWithExt, Entity4Order {
	public static int STATUS_NORMAL = 10;
	public static int STATUS_CONFIRM = 90;
	public static int STATUS_INVALID = 91;
	public static final Map<Integer, String> STAGE_DESC_MAP = new HashMap<Integer, String>(){
		private static final long serialVersionUID = 20090313L;
		@Override
		public String get(Object key) {
			if (new Integer(STATUS_NORMAL).equals(key)){
				return "";
			}else if (new Integer(STATUS_CONFIRM).equals(key)){
				return "已确认";
			}else if (new Integer(STATUS_INVALID).equals(key)){
				return "已作废";
			}
			return "未知状态: "+key;
		}
	};
	
	/** 编号 */
	@NotNull
	@Unique
	public String getSerialNo();
	public void setSerialNo(String n);
	/** 帐户 ID */
	@NotNull
	public int getAccId();
	public void setAccId(int a);
	/** 帐户名称 */
	@Ignore
	public String get_Ext_AccName();
	public void set_Ext_AccName(String a);
	/** 日期 */
	@NotNull
	@SQLType(Types.DATE)
	public Date getDate();
	public void setDate(Date d);
	/** 备注 */
	public String getRemark();
	public void setRemark(String r);
	/** 状态 */
	@NotNull
	@Default("10")
	public int getStage();
	public void setStage(int s);
	/** 状态名称 */
	@Ignore
	public String get_Ext_Stage();
	public void set_Ext_Stage(String s);
}
