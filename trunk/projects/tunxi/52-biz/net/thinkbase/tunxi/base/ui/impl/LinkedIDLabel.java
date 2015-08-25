package net.thinkbase.tunxi.base.ui.impl;

import net.thinkbase.tunxi.base.ui.ComplexComponent;

import org.zkoss.zhtml.A;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Label;

public class LinkedIDLabel extends ComplexComponent {
	private static final long serialVersionUID = 20090221L;
	
	private int id;

	public LinkedIDLabel(int id, String label){
		this.id = id;
		
		A a = new A();
		a.setDynamicProperty("href", "javascript:void("+id+")");
		
		this.setMainComponent(a);
		
		Label l = new Label(label);
		l.setParent(a);
		
		Label lId = new Label(Integer.toString(id));
		lId.setVisible(false);
		lId.setParent(a);
	}
	
	public LinkedIDLabel(Event event){
		fromEvent(event);
	}
	
	private void fromEvent(final Event event){
		Event e = event;
		if (e instanceof ForwardEvent){
			e = ((ForwardEvent)e).getOrigin();
		}
		A main = (A)e.getTarget();
		
		this.setMainComponent(main);
		
		//Find second label(which stored the id field)
		Label last = (Label)main.getLastChild();
		this.id = Integer.parseInt(last.getValue());
	}

	public int getID() {
		return id;
	}
}
