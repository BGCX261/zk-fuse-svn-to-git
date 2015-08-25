package net.thinkbase.tunxi.base.crud;

import net.java.ao.Entity;

public interface FieldVisitor<T extends Entity>{
	public Object getFieldValue(T data);
}
