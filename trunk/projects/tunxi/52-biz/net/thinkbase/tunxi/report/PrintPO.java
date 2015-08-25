package net.thinkbase.tunxi.report;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.sql.RowSet;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.thinkbase.jasperreports.AlignedByFieldRowSet;
import net.thinkbase.jasperreports.ReportRunner;
import net.thinkbase.jasperreports.RowSetDataSource;
import net.thinkbase.tunxi.biz.model.UserAccount;
import net.thinkbase.tunxi.data.ActiveObjects;
import net.thinkbase.util.ReflectUtil;
import net.thinkbase.util.StringUtility;
import net.thinkbase.util.TextFileUtil;

public class PrintPO implements ReportRunner {
	private static int LINES_PER_PAGE = 5;

	public JasperPrint fill(HttpServletRequest req, UserAccount user) throws Exception {
		String[] ids = req.getParameterValues("ID");
		String inIds = StringUtility.joinArray(ids, ",");
		String sql = TextFileUtil.readAsText(
				this.getClass().getResourceAsStream("PrintPO.sql"));
		sql = sql.replaceAll("\\$\\{IDs\\}", inIds);
		RowSet rs = ActiveObjects.query(sql);
		
		JRDataSource ds = new RowSetDataSource(
				new AlignedByFieldRowSet(rs, LINES_PER_PAGE, "SERIALNO"));
		
		InputStream in;
		if ("A4".equalsIgnoreCase(req.getParameter("P"))){	//A4 纸打印
			in = this.getClass().getResourceAsStream("PO_A4.jasper");
		}else{
			in = this.getClass().getResourceAsStream("PO.jasper");
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CUR_USER", user.getName());
		try{
			JasperPrint prt = JasperFillManager.fillReport(in, params, ds);
			
			//将第一条记录的备注分配到整个 Detail 区域
			int pages = prt.getPages().size();
			for(int i=0; i<pages; i++){
				JRBasePrintPage p = (JRBasePrintPage) prt.getPages().get(i);
				int remarkFieldCount = 0;
				int ec = p.getElements().size();
				for(int k=0; k<ec; k++){
					JRPrintElement elm = (JRPrintElement) p.getElements().get(k);
					if (elm instanceof JRTemplatePrintText){
						JRTemplatePrintText te = (JRTemplatePrintText)elm;
						if ("txtRemark".equals(te.getTemplate().getKey())){
							remarkFieldCount ++;
							if (remarkFieldCount % LINES_PER_PAGE == 1){
								String s = te.getText();
								char[] chars = (char[]) ReflectUtil.getFieldValue(s, "value");
								s = new String(chars);
								float blankSpace = te.getHeight() - te.getTextHeight();
								te.setHeight(LINES_PER_PAGE * te.getHeight());
								te.setTextHeight(te.getHeight() - blankSpace);
								te.setText(s);
							}else{
								te.setY(-1 * prt.getPageHeight());
							}
						}
					}
				}
			}
			return prt;
		}finally{
			in.close();
		}
	}

}
