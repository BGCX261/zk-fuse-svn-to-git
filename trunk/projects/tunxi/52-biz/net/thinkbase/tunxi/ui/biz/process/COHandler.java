package net.thinkbase.tunxi.ui.biz.process;

import java.sql.SQLException;
import java.util.List;

import javax.sql.RowSet;

import net.java.ao.EntityManager;
import net.thinkbase.tunxi.base.crud.CRUDMainHandler;
import net.thinkbase.tunxi.base.crud.EntityDataCache;
import net.thinkbase.tunxi.base.crud.ValidateException;
import net.thinkbase.tunxi.biz.model.CO;
import net.thinkbase.tunxi.biz.model.COL;
import net.thinkbase.tunxi.biz.model.Customer;
import net.thinkbase.tunxi.biz.model.ProductPrice;
import net.thinkbase.tunxi.data.Action;
import net.thinkbase.tunxi.data.ActiveObjects;

public class COHandler implements CRUDMainHandler<CO, COL> {
	private EntityDataCache<Customer> custCache;
	
	public COHandler(EntityDataCache<Customer> custCache){
		this.custCache = custCache;
	}
	
	public Class<CO> getMasterType() {
		return CO.class;
	}

	public Class<COL> getDetailType() {
		return COL.class;
	}

	public String getMasterAsString(CO t) {
		return t.getSerialNo();
	}

	public void onSaving(CO t, final List<COL> ds) throws ValidateException {
		if (CO.STATUS_CONFIRM == t.getStage()){
			throw new ValidateException("已确认的单据不能修改!");
		}
		if (CO.STATUS_INVALID == t.getStage()){
			throw new ValidateException("已作废的单据不能修改!");
		}
		
		if (null==t.getSerialNo() || t.getSerialNo().trim().length() < 1){
			throw new ValidateException("必须填写单据编号!");
		}
		if (0==custCache.find(t.getCustId()).getID()){
			throw new ValidateException("未填写正确的客户!");
		}
		
		//单据编号不能重复
		try {
			RowSet rs = ActiveObjects.query(
					"SELECT COUNT(*) FROM CO Where SerialNo='"+t.getSerialNo().replace("'", "''")+"'") ;
			rs.next();
			if (rs.getInt(1) > 0 ){
				throw new ValidateException("已经存在编号为 '"+t.getSerialNo()+"' 的单据!");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		//设置价格同时检查
		if (null!=ds){
			ValidateException ve = (ValidateException)ActiveObjects.doAction(new Action(){
				public Object perform(EntityManager db) throws Exception {
					for(int i=0; i<ds.size(); i++){
						int pid = ds.get(i).getPriceId();
						ProductPrice p = db.get(ProductPrice.class, pid);
						if (null==p.getPrice()){
							return new ValidateException("第 "+(i+1)+" 行: 未选择价格!");
						}else{
							ds.get(i).setPrice(p.getPrice());
						}
					}
					return null;
				}
			});
			if (null!=ve){
				throw ve;
			}
		}
	}

}
