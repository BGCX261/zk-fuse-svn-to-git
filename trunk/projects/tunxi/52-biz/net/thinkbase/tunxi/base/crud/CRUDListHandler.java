package net.thinkbase.tunxi.base.crud;

import net.thinkbase.tunxi.data.EntityWithExt;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;

public interface CRUDListHandler<T extends EntityWithExt> {
	public Grid list_getMainGrid();
	public String list_getFindWinZul();
	public String list_getEditWinZul();
	public QueryCondition list_getDefaultCondition();
	public Component[] list_buildListRow(T data);
	public void list_prepareNewMaster(T t);
	public void list_prepareMaster(T t);
}
