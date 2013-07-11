package com.readium.model.epub3;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * The Container class provides an interface for interacting with an EPUB container,
 * i.e. a `.epub` file.
 * <p/>
 * Each Container instance owns all its sub-items. All Packages from a single container,
 * for instance, are kept around as pointers which will be deleted when the container
 * is destroyed.
 *
 * @remarks The Container class holds owning references to the Archive instance used
 * to read from the zip file, the XML document for the OCF file at META-INF/container.xml,
 * all Packages within the container, and all EncryptionInfo instances from
 * META-INF/encryption.xml.
 */
public class Container {

    private static final String TAG = "Container";
	private final int nativePtr;
    private final String path;
    private String version;
    private final List<Package> packages;

    public Container(int nativePtr, String path) {
        this.nativePtr = nativePtr;
        this.path = path;
        this.packages = new ArrayList<Package>();
        Log.i(TAG, "nativePtr: "+nativePtr+", path: "+path);
    }

	public int getNativePtr() {
		return nativePtr;
	}

	public void addPackage(Package pack) {
		packages.add(pack);
	}

    public Package getDefaultPackage() {
        return packages.isEmpty() ? null : packages.get(0);
    }

    public String getVersion() {
    	return version;
    }

	public String getPath() {
		return path;
	}

	public String getName() {
		return new File(path).getName();
	}
}
