package net.thinkbase.tunxi;

import java.io.File;
import java.util.logging.LogManager;

import net.java.ao.DBParam;
import net.java.ao.EntityManager;
import net.java.ao.types.BigDecimalType;
import net.java.ao.types.TypeManager;
import net.thinkbase.tunxi.biz.model.BankAccount;
import net.thinkbase.tunxi.biz.model.CO;
import net.thinkbase.tunxi.biz.model.COL;
import net.thinkbase.tunxi.biz.model.Customer;
import net.thinkbase.tunxi.biz.model.POL;
import net.thinkbase.tunxi.biz.model.PO;
import net.thinkbase.tunxi.biz.model.Product;
import net.thinkbase.tunxi.biz.model.ProductPrice;
import net.thinkbase.tunxi.biz.model.RuntimeInfo;
import net.thinkbase.tunxi.biz.model.UserAccount;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.tunxi.data.ActiveObjectsUtil;
import net.thinkbase.tunxi.system.EmbeddedDerbyDatabase;
import net.thinkbase.tunxi.system.ZkJettyServer;
import net.thinkbase.util.NumberUtility;

/**
 * 启动服务器
 * @author thinkbase.net
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		LogManager.getLogManager().readConfiguration(
				Main.class.getResourceAsStream("/logger.properties"));
		
		initDb("./tunxi.data", "3");
		buildDefaultData("TUNXI", "KZ MIS", "thinkbase.net");
		initWeb("/net/thinkbase/tunxi/Pages", "8080");
	}
	
	private static void initDb(String dataFile, String maxSize) throws Exception{
		int max = NumberUtility.string2Num(maxSize).intValue();
		File dir = new File(dataFile);
		EmbeddedDerbyDatabase srv = EmbeddedDerbyDatabase.newInstance(dir, max);
		ActiveObjects.registerServer(srv);
	}
	private static void initWeb(String baseResource, String port) throws Exception{
		int portNo = NumberUtility.string2Num(port).intValue();
		ZkJettyServer.newInstance(Main.class.getResource(baseResource), portNo);
	}
	private static void buildDefaultData(
			final String systemCode, final String productName, final String owner) throws Exception{
		TypeManager.getInstance().addType(new BigDecimalType());
		
		ActiveObjects.doAction(new Action(){
			@SuppressWarnings("unchecked")
			public Object perform(EntityManager db) throws Exception {
				db.migrate(ActiveObjectsUtil.SeqModel.class,
						   RuntimeInfo.class,
						   UserAccount.class, 
						   BankAccount.class, Customer.class, Product.class,
						   ProductPrice.class,
						   PO.class, POL.class,
						   CO.class, COL.class);
				
				//检查是否是第一次运行
				if (RuntimeInfo.Helper.initRuntimeInfo(systemCode, productName, owner)){
					//初始化默认登录用户
					UserAccount admin = db.create(UserAccount.class, new DBParam[]{
						new DBParam("loginName", "admin")
					});
					admin.setLoginName("admin");
					admin.setPassword("admin");
					admin.setName("系统管理员");
					admin.setDescr("系统默认创建的管理员用户");
					admin.setAdmin(true);
					admin.save();
				}
				return null;
			}
		});
	}

}
