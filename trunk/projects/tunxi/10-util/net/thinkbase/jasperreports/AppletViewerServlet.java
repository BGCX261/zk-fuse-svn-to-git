package net.thinkbase.jasperreports;

import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRAlignment;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.thinkbase.tunxi.biz.model.UserAccount;
import net.thinkbase.util.ExUtility;

/**
 * Export jasperreports result to servlet
 * @author thinkbase.net
 *
 */
public class AppletViewerServlet extends HttpServlet {
	private static final long serialVersionUID = 20090316L;
	
	/**
	 * 必须从 HTTP 参数中指定的 ReportRunner 实现类名称
	 */
	public static String REQ_KEY_RUNNER = "REPORT_RUNNER";

	public void service(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
		String runnerClass = request.getParameter(REQ_KEY_RUNNER);
		JasperPrint jasperPrint = null;
		try {
			UserAccount user = (UserAccount
    		    ) request.getSession().getAttribute(UserAccount.class.getName());
			if (null==user){
				throw new RuntimeException("用户没有登录");
			}

			ReportRunner runner = (ReportRunner
					)Class.forName(runnerClass).newInstance();

			jasperPrint = runner.fill(request, user);
		} catch (Exception e) {
			jasperPrint = errorMsg(e);
		}
		
		//为 Applet 生成报表
        response.setContentType("application/octet-stream");
        ServletOutputStream sos = response.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(sos);
        oos.writeObject(jasperPrint);
        oos.flush();
        oos.close();
        sos.flush();
        sos.close();
    }
	
	private JasperPrint errorMsg(Exception e){
		String msg = ExUtility.getStackTrace(e);
		
		JasperPrint jasperPrint = new JasperPrint();
		jasperPrint.setName("Error Report");
		jasperPrint.setPageWidth(595);
		jasperPrint.setPageHeight(842);
		
		JRPrintPage page = new JRBasePrintPage();

		JRPrintText text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(40);
		text.setY(50);
		text.setWidth(515);
		text.setHeight(30);
		text.setTextHeight(text.getHeight());
		text.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_JUSTIFIED);
		text.setLineSpacingFactor(1.329241f);
		text.setLeadingOffset(-4.076172f);
		text.setFontSize(16);
		text.setText("报表运行错误");
		page.addElement(text);

		text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(40);
		text.setY(100);
		text.setWidth(515);
		text.setHeight(642);
		text.setTextHeight(text.getHeight());
		text.setHorizontalAlignment(JRAlignment.HORIZONTAL_ALIGN_JUSTIFIED);
		text.setLineSpacingFactor(1.329241f);
		text.setLeadingOffset(-4.076172f);
		text.setFontSize(11);
		text.setText(msg);
		page.addElement(text);

		jasperPrint.addPage(page);

		return jasperPrint;
	}

}
