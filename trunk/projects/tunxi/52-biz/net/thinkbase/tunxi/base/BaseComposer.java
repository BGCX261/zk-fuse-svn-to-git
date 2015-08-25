package net.thinkbase.tunxi.base;

import net.thinkbase.tunxi.biz.model.RuntimeInfo;
import net.thinkbase.util.ExUtility;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zul.Window;

/**
 * 所有窗口 Composer 的基类
 * @author thinkbase.net
 *
 */
public abstract class BaseComposer extends GenericAutowireComposer implements ComposerExt {
	/** 默认情况下系统引用 RuntimeInfo 对象的变量名称 */
	private static final String RUNTIME_VAR_NAME = "RUNTIME";

	/**
	 * 必须要每个子类实现的方法: 返回当前页面的名称;
	 * @return 如果返回 null 则表明使用当前基类对应 Window 组件的 title
	 */
	protected String getPageName(){
		return null;	//默认返回 null
	}

	/**
	 * beforeCompose 事件, 注意这时当前 Composer 的各个变量均未完成初始化, 只能通过 page 变量访问
	 * @return false 代表当前 Component 不会被实例化(the component won't be instantiated), 默认为 true
	 */
	protected boolean onBeforeCompose(Page page) throws Exception{
		return true;
	}
	
	/**
	 * continueCompose, 紧接在 beforeCompose 事件后触发, 注意这时已经不能控
	 * 制当前 Component 是否被实例化
	 * @param page
	 */
	protected void onContinueCompose(Page page) throws Exception{
		//默认不做任何事情
	}

	/** afterCompose 事件, 这时当前 Composer 的各个变量均已初始化完成 */
	protected void onAfterCompose() throws Exception{
		//默认不做任何事情
	}
	
	//父类的方法重载 =================================================================
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);	//不执行这一句则无法初始化 this 的各个变量
		onAfterCompose();

		//设置默认的页面标题
		String name = this.getPageName();
		if (null==name){
			if (this.self instanceof Window){
				name = ((Window)this.self).getTitle();
			}
		}
		if (null!=name){
			this.page.setTitle(
					RuntimeInfo.Helper.getRuntimeInfo().getProductName() + ": " + name);
		}
	}

	// ComposerExt 接口方法的默认实现 ==================================================
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo){
		//在 Page 范围设置变量 "RUNTIME"
		page.setVariable(RUNTIME_VAR_NAME, RuntimeInfo.Helper.getRuntimeInfo());
		
		try {
			boolean cont = onBeforeCompose(page);	//Continue?
			if (cont){
				onContinueCompose(page);
				return compInfo;
			}else{
				return null;
			}
		} catch (Exception e) {
			ExUtility.throwRuntimeException(e);
			return null;
		}
	}
	public void doBeforeComposeChildren(Component comp) throws Exception {
	}
	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}
	public void doFinally() throws Exception {
	}
}
