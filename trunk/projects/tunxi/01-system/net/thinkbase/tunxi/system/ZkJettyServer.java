package net.thinkbase.tunxi.system;

import java.net.URL;

import net.thinkbase.jasperreports.AppletViewerServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.resource.Resource;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;

/**
 * 包含了 ZK 支持的 Jetty Servlet 服务器
 * @author thinkbase.net
 */
public class ZkJettyServer {
	
	private ZkJettyServer(){
		//Private constructor to prevent new instance directly
	}
	
	public static ZkJettyServer newInstance(URL baseResource, int port) throws Exception{
		Server server = new Server(port);
		Context root = new Context(server,"/",Context.SESSIONS);
		
		//设置 Web 程序资源的基础位置
		root.setBaseResource(Resource.newResource(baseResource));
		
		//处理静态资源的 Servlet
		ServletHolder def = new ServletHolder(new DefaultServlet());
		def.setInitParameter("gzip", "false");
		root.addServlet(def, "/");
		
		//申明 DHtmlLayoutServlet
		ServletHolder zul = new ServletHolder(new DHtmlLayoutServlet());
		zul.setInitParameter("update-uri", "/zkau");
		zul.setInitOrder(10);
		root.addServlet(zul, "*.zul");
		//申明 DHtmlUpdateServlet
		ServletHolder zkau = new ServletHolder(new DHtmlUpdateServlet());
		zkau.setInitOrder(20);
		root.addServlet(zkau, "/zkau/*");
		//申明 HttpSessionListener
		root.addEventListener(new HttpSessionListener());
		
		//处理 JasperReports 的 Servlet
		ServletHolder rpt = new ServletHolder(new AppletViewerServlet());
		rpt.setInitOrder(30);
		root.addServlet(rpt, "/JRAppletViewer");
		
		server.start();
		return new ZkJettyServer();
	}
}
