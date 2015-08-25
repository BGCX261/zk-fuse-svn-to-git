package net.thinkbase.tunxi.data;

import net.java.ao.schema.NotNull;

/**
 * 用作明细数据的 Entity 基本定义
 * @author thinkbase.net
 *
 */
public interface Entity4Detail extends EntityWithExt {

	@NotNull
	public int getHeaderId();
	public void setHeaderId(int i);
	
	/** 序号 */
	@NotNull
	public int getSeq();
	public void setSeq(int s);

}
