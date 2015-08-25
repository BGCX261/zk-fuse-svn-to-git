package net.thinkbase.tunxi.base.ui;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;

public class ComplexComponent implements Component{
	private static final long serialVersionUID = 20090221L;
	
	private Component mainComp;
	
	public Component getMainComponent(){
		return mainComp;
	}
	public void setMainComponent(Component mainComp){
		this.mainComp = mainComp;
	}

	public boolean addEventListener(String evtnm, EventListener listener) {
		return mainComp.addEventListener(evtnm, listener);
	}

	public boolean addForward(String originalEvent, Component target, String targetEvent, Object eventData) {
		return mainComp.addForward(originalEvent, target, targetEvent, eventData);
	}

	public boolean addForward(String originalEvent, Component target, String targetEvent) {
		return mainComp.addForward(originalEvent, target, targetEvent);
	}

	public boolean addForward(String originalEvent, String targetPath, String targetEvent, Object eventData) {
		return mainComp.addForward(originalEvent, targetPath, targetEvent, eventData);
	}

	public boolean addForward(String originalEvent, String targetPath, String targetEvent) {
		return mainComp.addForward(originalEvent, targetPath, targetEvent);
	}

	public boolean appendChild(Component child) {
		return mainComp.appendChild(child);
	}

	public void applyProperties() {
		mainComp.applyProperties();
	}

	public Object clone() {
		return mainComp.clone();
	}

	public boolean containsVariable(String name, boolean local) {
		return mainComp.containsVariable(name, local);
	}

	public void detach() {
		mainComp.detach();
	}

	public Object getAttribute(String name, int scope) {
		return mainComp.getAttribute(name, scope);
	}

	public Object getAttribute(String name) {
		return mainComp.getAttribute(name);
	}

	public Map getAttributes() {
		return mainComp.getAttributes();
	}

	public Map getAttributes(int scope) {
		return mainComp.getAttributes(scope);
	}

	public List getChildren() {
		return mainComp.getChildren();
	}

	public ComponentDefinition getDefinition() {
		return mainComp.getDefinition();
	}

	public Desktop getDesktop() {
		return mainComp.getDesktop();
	}

	public Component getFellow(String id) {
		return mainComp.getFellow(id);
	}

	public Component getFellowIfAny(String id) {
		return mainComp.getFellowIfAny(id);
	}

	public Collection getFellows() {
		return mainComp.getFellows();
	}

	public Component getFirstChild() {
		return mainComp.getFirstChild();
	}

	public String getId() {
		return mainComp.getId();
	}

	public Component getLastChild() {
		return mainComp.getLastChild();
	}

	public Iterator getListenerIterator(String evtnm) {
		return mainComp.getListenerIterator(evtnm);
	}

	public String getMold() {
		return mainComp.getMold();
	}

	public Namespace getNamespace() {
		return mainComp.getNamespace();
	}

	public Component getNextSibling() {
		return mainComp.getNextSibling();
	}

	public Page getPage() {
		return mainComp.getPage();
	}

	public Component getParent() {
		return mainComp.getParent();
	}

	public Component getPreviousSibling() {
		return mainComp.getPreviousSibling();
	}

	public Component getRoot() {
		return mainComp.getRoot();
	}

	public IdSpace getSpaceOwner() {
		return mainComp.getSpaceOwner();
	}

	public String getUuid() {
		return mainComp.getUuid();
	}

	public Object getVariable(String name, boolean local) {
		return mainComp.getVariable(name, local);
	}

	public boolean insertBefore(Component newChild, Component refChild) {
		return mainComp.insertBefore(newChild, refChild);
	}

	public void invalidate() {
		mainComp.invalidate();
	}

	public boolean isChildable() {
		return mainComp.isChildable();
	}

	public boolean isInvalidated() {
		return mainComp.isInvalidated();
	}

	public boolean isListenerAvailable(String evtnm, boolean asap) {
		return mainComp.isListenerAvailable(evtnm, asap);
	}

	public boolean isVisible() {
		return mainComp.isVisible();
	}

	public void redraw(Writer out) throws IOException {
		mainComp.redraw(out);
	}

	public Object removeAttribute(String name, int scope) {
		return mainComp.removeAttribute(name, scope);
	}

	public Object removeAttribute(String name) {
		return mainComp.removeAttribute(name);
	}

	public boolean removeChild(Component child) {
		return mainComp.removeChild(child);
	}

	public boolean removeEventListener(String evtnm, EventListener listener) {
		return mainComp.removeEventListener(evtnm, listener);
	}

	public boolean removeForward(String originalEvent, Component target, String targetEvent) {
		return mainComp.removeForward(originalEvent, target, targetEvent);
	}

	public boolean removeForward(String originalEvent, String targetPath, String targetEvent) {
		return mainComp.removeForward(originalEvent, targetPath, targetEvent);
	}

	public Object setAttribute(String name, Object value, int scope) {
		return mainComp.setAttribute(name, value, scope);
	}

	public Object setAttribute(String name, Object value) {
		return mainComp.setAttribute(name, value);
	}

	public void setId(String id) {
		mainComp.setId(id);
	}

	public void setMold(String mold) {
		mainComp.setMold(mold);
	}

	public void setPage(Page page) {
		mainComp.setPage(page);
	}

	public void setPageBefore(Page page, Component refRoot) {
		mainComp.setPageBefore(page, refRoot);
	}

	public void setParent(Component parent) {
		mainComp.setParent(parent);
	}

	public void setVariable(String name, Object val, boolean local) {
		mainComp.setVariable(name, val, local);
	}

	public boolean setVisible(boolean visible) {
		return mainComp.setVisible(visible);
	}

	public void unsetVariable(String name, boolean local) {
		mainComp.unsetVariable(name, local);
	}
}
