package org.readium.sdklauncher_android;

import java.util.HashMap;
import java.util.Map;

import com.readium.model.epub3.Container;

public class ContainerHolder {

	private static final ContainerHolder INSTANCE = new ContainerHolder();
	
	private final Map<Integer, Container> containers = new HashMap<Integer, Container>();
	
	public static ContainerHolder getInstance() {
		return INSTANCE;
	}

	public Container get(Object arg0) {
		return containers.get(arg0);
	}

	public Container remove(Object arg0) {
		return containers.remove(arg0);
	}

	public Container put(Integer key, Container value) {
		return containers.put(key, value);
	}
	
	
}
