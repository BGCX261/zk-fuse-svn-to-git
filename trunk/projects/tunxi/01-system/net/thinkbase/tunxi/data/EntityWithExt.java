package net.thinkbase.tunxi.data;

import net.java.ao.Entity;

/**
 * Entity 接口的扩展, 增加了设置和读取扩展属性的功能
 * @author thinkbase.net
 */
public interface EntityWithExt extends Entity {
	public static final String EXT_FIELD_Prefix = "_Ext_";
}
