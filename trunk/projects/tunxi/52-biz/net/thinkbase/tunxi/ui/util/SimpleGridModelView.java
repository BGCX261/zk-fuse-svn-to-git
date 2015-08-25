package net.thinkbase.tunxi.ui.util;

import java.util.List;

import net.java.ao.Entity;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;

/**
 * 针对 Grid 的数据模型和 RowRender
 * @author thinkbase.net
 */
public abstract class SimpleGridModelView<T extends Entity> extends SimpleListModel {

	public SimpleGridModelView(Grid grid, List<T> data){
		super(data);
		grid.setRowRenderer(this.new Render());
	}
	public SimpleGridModelView(Grid grid, T[] data){
		super(data);
		grid.setRowRenderer(this.new Render());
	}
	
	protected abstract Component[] cells(T data) throws Exception;
	
	private class Render implements RowRenderer{
		@SuppressWarnings("unchecked")
		public void render(Row row, Object data) throws Exception {
			Component[] cells = cells((T)data);
			for (int i = 0; i < cells.length; i++) {
				cells[i].setParent(row);
			}
		}
		
	}
}
