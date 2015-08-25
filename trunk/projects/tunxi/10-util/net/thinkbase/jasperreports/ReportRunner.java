package net.thinkbase.jasperreports;

import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JasperPrint;
import net.thinkbase.tunxi.biz.model.UserAccount;

public interface ReportRunner {
	public JasperPrint fill(HttpServletRequest req, UserAccount user) throws Exception;
}
