package net.thinkbase.tunxi.base.crud;

import java.util.List;

import net.thinkbase.tunxi.data.Entity4Detail;
import net.thinkbase.tunxi.data.EntityWithExt;

public interface CRUDMainHandler<T extends EntityWithExt, D extends Entity4Detail> {
	public static final String RELATION_FIELD = "headerid";
	public static final String SEQUENCE_FIELD = "seq";
	
	public Class<T> getMasterType();
	public Class<D> getDetailType();
	public String getMasterAsString(T t);
	public void onSaving(T t, List<D> ds) throws ValidateException;
}
