package edu.cs328;

import com.badlogic.ashley.core.Component;

public class SelectableComponent implements Component {
	boolean friendly = true;	
	boolean selected = false;
	boolean selectable = true;
	
	public SelectableComponent(boolean friendly) {
		this.friendly = friendly;
	}
}
