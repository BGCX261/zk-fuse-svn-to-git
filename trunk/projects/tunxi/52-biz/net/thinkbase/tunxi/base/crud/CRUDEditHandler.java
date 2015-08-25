package net.thinkbase.tunxi.base.crud;

import org.zkoss.zul.Grid;

import net.thinkbase.tunxi.data.Entity4Detail;
import net.thinkbase.tunxi.data.EntityWithExt;

public interface CRUDEditHandler<T extends EntityWithExt, D extends Entity4Detail> {
	public Class<D> getDetailType();
	public String edit_getMasterBindName();
	public String edit_getDetailBindName();
	public void edit_onMasterBinding(T t);
	public void edit_onDetailBinding(D d);
	public void edit_prepareNewDetail(D d, T t);
	
	public int edit_getMaxDetailLines();
	
	public Grid edit_getDetailGrid();
}
