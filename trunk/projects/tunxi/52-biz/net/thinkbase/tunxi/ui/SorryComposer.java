package net.thinkbase.tunxi.ui;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;

import net.thinkbase.tunxi.base.BaseComposer;

/**
 * 对应 Sorry 页面
 * @author thinkbase.net
 */
public class SorryComposer extends BaseComposer {
	private Label sorryMsg;

	public void onCreate(Event event) {
		HttpServletRequest req = (HttpServletRequest)execution.getNativeRequest();
		sorryMsg.setValue(
				(String)req.getSession().getAttribute(SorryComposer.class.getName()));
	}
	
	public static final void saySorry(String sorry, Execution execution){
		HttpServletRequest req = (HttpServletRequest)execution.getNativeRequest();
		req.getSession().setAttribute(SorryComposer.class.getName(), sorry);
		execution.sendRedirect("/sorry.zul");
	}
}
