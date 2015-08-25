package net.thinkbase.tunxi.ui.biz.process;

import java.sql.SQLException;
import java.util.List;

import javax.sql.RowSet;

import net.thinkbase.tunxi.base.crud.CRUDMainHandler;
import net.thinkbase.tunxi.base.crud.EntityDataCache;
import net.thinkbase.tunxi.base.crud.ValidateException;
import net.thinkbase.tunxi.biz.model.BankAccount;
import net.thinkbase.tunxi.biz.model.PO;
import net.thinkbase.tunxi.biz.model.POL;
import net.thinkbase.tunxi.data.ActiveObjects;

public class POHandler implements CRUDMainHandler<PO, POL> {
	private EntityDataCache<BankAccount> bankCache;
	public POHandler(EntityDataCache<BankAccount> bankCache){
		this.bankCache = bankCache;
	}
	
	public Class<PO> getMasterType() {
		return PO.class;
	}
	public Class<POL> getDetailType() {
		return POL.class;
	}
	public void onSaving(PO t, List<POL> lines) throws ValidateException{
		if (PO.STATUS_CONFIRM == t.getStage()){
			throw new ValidateException("已确认的单据不能修改!");
		}
		if (PO.STATUS_INVALID == t.getStage()){
			throw new ValidateException("已作废的单据不能修改!");
		}
		
		if (null==t.getSerialNo() || t.getSerialNo().trim().length() < 1){
			throw new ValidateException("必须填写单据编号!");
		}
		if (0==bankCache.find(t.getAccId()).getID()){
			throw new ValidateException("未填写正确的帐户!");
		}
		
		//单据编号不能重复
		try {
			RowSet rs = ActiveObjects.query(
					"SELECT COUNT(*) FROM PO Where SerialNo='"+t.getSerialNo().replace("'", "''")+"'") ;
			rs.next();
			if (rs.getInt(1) > 0 ){
				throw new ValidateException("已经存在编号为 '"+t.getSerialNo()+"' 的单据!");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public String getMasterAsString(PO t) {
		return t.getSerialNo();
	}
}
