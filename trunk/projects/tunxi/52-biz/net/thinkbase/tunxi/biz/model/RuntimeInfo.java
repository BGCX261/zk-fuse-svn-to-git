package net.thinkbase.tunxi.biz.model;

import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

/**
 * 记录本系统的初始运行信息
 * @author thinkbase.net
 */
public interface RuntimeInfo extends Entity {
	/** 数据记录 ID */
	public String getFinalId();
	public void setFinalId(String id);
	/** 系统代码 */
	public String getSystemCode();
	public void setSystemCode(String code);
	/** 产品名称 */
	public String getProductName();
	public void setProductName(String prodName);
	/** 系统的所有者 */
	public String getOwnerName();
	public void setOwnerName(String name);
	/** 系统建立日期 */
	public Date getCreateTime();
	public void setCreateTime(Date time);
	
	public static class Helper{
		private static final String FINAL_ID="FINAL";
		private static RuntimeInfo RUNTIME_INFO;
		
		/***
		 * 初始化系统信息
		 * @param systemCode
		 * @param productName
		 * @param owner
		 * @return true 表明系统是第一次初始化
		 */
		public static final boolean initRuntimeInfo(final String systemCode, final String productName, final String owner){
			Object res = ActiveObjects.doAction(new Action(){
				public Object perform(EntityManager db) throws Exception {
					RuntimeInfo[] ris = db.find(RuntimeInfo.class, "finalId=?", FINAL_ID);
					if (ris.length < 1){
						//初始化 RuntimeInfo 记录
						RuntimeInfo ri = db.create(RuntimeInfo.class);
						ri.setFinalId(FINAL_ID);
						ri.setSystemCode(systemCode);
						ri.setProductName(productName);
						ri.setOwnerName(owner);
						ri.setCreateTime(new Date());
						ri.save();
						
						RUNTIME_INFO = ri;
						return true;
					}else{
						RUNTIME_INFO = ris[0];
						return false;
					}
				}
			});
			return (Boolean)res;
		}
		public static RuntimeInfo getRuntimeInfo(){
			return RUNTIME_INFO;
		}
		
	}
}
