package net.thinkbase.tunxi.data;

import java.sql.SQLException;

import net.java.ao.DBParam;
import net.java.ao.Entity;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;
import net.java.ao.schema.Unique;
import net.thinkbase.util.NumberUtility;

/**
 * Some database util
 * @author thinkbase.net
 *
 */
public class ActiveObjectsUtil {
	@Table("THINKBASE_SEQ")
	public static interface SeqModel extends Entity{
		/** 一般使用 <表名>:<字段名>:<前缀> */
		@NotNull
		@Unique
		public String getSeqKey();
		public void setSeqKey(String n);
		/** 最后使用的序号 */
		@NotNull
		public int getLastId();
		public void setLastId(int l);

	}
	public static final String getSerialNo(
			final String prefix, final int seqLength, String table, String field) throws SQLException{
		final String key = table.toUpperCase() + ":" + field.toUpperCase() + ":" + prefix;
		return (String)ActiveObjects.doAction(new Action(){
			public Object perform(EntityManager db) throws Exception {
				SeqModel[] m = db.find(SeqModel.class,
						Query.select().where("SeqKey=?", new Object[]{key}).order("ID"));
				SeqModel current;
				if (m.length < 1){
					current = db.create(SeqModel.class,
							new DBParam[]{new DBParam("SeqKey", key), new DBParam("LastId", 1)});
				}else{
					current = m[0];
					int last = current.getLastId();
					current.setLastId(last + 1);
					current.save();
				}
				int seq = current.getLastId();
				StringBuffer buf = new StringBuffer();
				for(int i=0; i<seqLength; i++){
					buf.append('0');
				}
				return prefix + NumberUtility.num2String(seq, buf.toString());
			}
		});
	}
}
