package net.thinkbase.jasperreports;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSet;

/**
 * 按照某个字段分组之后, 保证每个组中的数据为固定行(及其倍数)的数据集
 * @author thinkbase.net
 */
public class AlignedByFieldRowSet extends BaseRowSet{
	private int lineCount;
	private String groupField;
	
	private List<Integer> allPoints = null;		//每行数据实际对应的内部 RowSet 位置
	private List<Integer> blankLineNos = null;	//哪些数据行是空行
	private int lineNo = -1;

	/**
	 * 包装简单的 RowSet
	 * @param data 实际数据
	 * @param lineCount 每组的固定行数
	 * @param groupField 用于分组的字段
	 */
	public AlignedByFieldRowSet(RowSet data, int lineCount, String groupField){
		super(data);
		this.lineCount = lineCount;
		this.groupField = groupField;
	}
	
	public void beforeFirst() throws SQLException{
		sync4WaitPoints();

		data.beforeFirst();
	}
	public boolean next() throws SQLException {
		sync4WaitPoints();
		
		lineNo ++;
		if (lineNo >= allPoints.size()){
			return false;
		}
		int pos = allPoints.get(lineNo);
		boolean next = data.absolute(pos+1);
		return next;
	}
	public Object getObject(String columnName) throws SQLException{
		sync4WaitPoints();

		if (blankLineNos.contains(lineNo)){
			if (columnName.equals(groupField)){
				return data.getObject(columnName);
			}else{
				return null;
			}
		}else{
			return data.getObject(columnName);
		}
	}
	
	private void sync4WaitPoints() throws SQLException{
		if (null!=allPoints){
			return;
		}
		allPoints = new ArrayList<Integer>();
		blankLineNos = new ArrayList<Integer>();
		
		Object prevGroup = null;
		int curLine = 0;
		int lineInGroup = 0;
		boolean first = true;
		boolean isSameGroup = false;

		data.beforeFirst();
		while(this.data.next()){
			if (first){
				first = false;

				isSameGroup = false;
				curLine = 0;
				lineInGroup = 0;
				allPoints.add(curLine);
				prevGroup = data.getObject(this.groupField);
			}else{
				isSameGroup = isSame(prevGroup, data.getObject(this.groupField));

				if (isSameGroup){
					lineInGroup ++;
				}else{
					int blankLines = this.lineCount - (lineInGroup+1)%this.lineCount;
					blankLines = blankLines % this.lineCount;
					for(int i = 0; i< blankLines; i++){
						allPoints.add(curLine);	//curLine-1
						blankLineNos.add(allPoints.size()-1);	//存放为空的行号
					}
					//到了下一个组
					lineInGroup = 0;
				}
				
				curLine ++;
				allPoints.add(curLine);
				prevGroup = data.getObject(this.groupField);
			}
		}
		int blankLines = (this.lineCount -1 - (allPoints.size()-1))%this.lineCount;
		if (blankLines < 0) blankLines = this.lineCount + blankLines;
		for(int i = 0; i< blankLines; i++){
			allPoints.add(curLine);
			blankLineNos.add(allPoints.size()-1);
		}
	}
	private boolean isSame(Object o1, Object o2){
		if (null!=o1){
			return o1.equals(o2);
		}else if (null!=o2){
			return o2.equals(o1);
		}else{
			return true;	//Both null
		}
	}
}
