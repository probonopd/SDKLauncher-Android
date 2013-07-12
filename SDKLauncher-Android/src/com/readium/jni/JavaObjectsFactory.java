package com.readium.jni;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.readium.model.epub3.Container;
import com.readium.model.epub3.Package;
import com.readium.model.epub3.SpineItem;
import com.readium.model.epub3.components.navigation.NavigationElement;
import com.readium.model.epub3.components.navigation.NavigationPoint;
import com.readium.model.epub3.components.navigation.NavigationTable;

/**
 * This class is a helper accessed by the native code. It centralizes all the creations of Java objects in a unique place.
 * It is final+abstract and all its methods are private in order to avoid using it from Java code (JNI does not enforce access
 * control restrictions :-)
 * @author Mantano
 *
 */
public abstract class JavaObjectsFactory {
	
	private static final int BUFFER_SIZE_INCREMENT = 2*1024*1024;

	@SuppressWarnings("unused")
	private static List<SpineItem> createSpineItemList() {
		return new ArrayList<SpineItem>();
	}
	
	@SuppressWarnings("unused")
	private static SpineItem createSpineItem(String idRef, String href,
			String pageSpread, String renditionLayout) {
		return new SpineItem(idRef, href, pageSpread, renditionLayout);
	}
	
	@SuppressWarnings("unused")
	private static void addSpineItemToList(List<SpineItem> list, SpineItem spineItem) {
		list.add(spineItem);
	}
	
	@SuppressWarnings("unused")
	private static Container createContainer(int nativePtr, String path) {
		return new Container(nativePtr, path);
	}
	
	@SuppressWarnings("unused")
	private static void addPackageToContainer(Container container, int packagePtr) {
		container.addPackage(new Package(packagePtr));
	}
	
	@SuppressWarnings("unused")
	private static List<String> createStringList() {
		return new ArrayList<String>();
	}
	
	@SuppressWarnings("unused")
	private static void addStringToList(List<String> list, String str) {
		list.add(str);
	}
	
	@SuppressWarnings("unused")
	private static NavigationTable createNavigationTable(String type, String title, String sourceHref) {
		return new NavigationTable(type, title, sourceHref);
	}
	
	@SuppressWarnings("unused")
	private static NavigationPoint createNavigationPoint(String title, String content) {
		return new NavigationPoint(title, content);
	}
	
	@SuppressWarnings("unused")
	private static void addElementToParent(NavigationElement parent, NavigationElement child) {
		parent.appendChild(child);
	}
	
	@SuppressWarnings("unused")
	private static ByteBuffer createBuffer(int bufferSize) {
		ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
		buffer.position(0);
		buffer.limit(0);
		return buffer;
	}
	
	@SuppressWarnings("unused")
	private static void appendBytesToBuffer(ByteBuffer buffer, byte[] data) {
		int newLimit = buffer.limit() + data.length;
		buffer.limit(newLimit);
		buffer.put(data);
	}
}
