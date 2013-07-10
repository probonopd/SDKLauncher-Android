package com.readium.model.epub3;

import java.nio.ByteBuffer;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.readium.model.epub3.components.navigation.NavigationTable;

public class Package {

	private static final String TAG = "Package";
	
	private final int nativePtr;
	
	private String title;
	private String subtitle;
	private String shortTitle;
	private String collectionTitle;
	private String editionTitle;
	private String expandedTitle;
	private String fullTitle;
	private String uniqueID;
	private String urlSafeUniqueID;
	private String packageID;
	private String basePath;
	private String type;
	private String version;
	private String isbn;
	private String language;
	private String copyrightOwner;
	private String source;
	private String authors;
	private String modificationDate;
	private List<String> authorList;
	private List<String> subjects;
	private List<SpineItem> spineItems;
	private NavigationTable tableOfContents;
	private NavigationTable listOfFigures;
	private NavigationTable listOfIllustrations;
	private NavigationTable listOfTables;
	private NavigationTable pageList;

	public Package(int nativePtr) {
		this.nativePtr = nativePtr;
//        Log.i(TAG, "package nativePtr: "+nativePtr);
        loadData();
	}

	private void loadData() {
		title = nativeGetTitle(nativePtr);
		subtitle = nativeGetSubtitle(nativePtr);
		shortTitle = nativeGetShortTitle(nativePtr);
		collectionTitle = nativeGetCollectionTitle(nativePtr);
		editionTitle = nativeGetEditionTitle(nativePtr);
		expandedTitle = nativeGetExpandedTitle(nativePtr);
		fullTitle = nativeGetFullTitle(nativePtr);
		uniqueID = nativeGetUniqueID(nativePtr);
		urlSafeUniqueID = nativeGetURLSafeUniqueID(nativePtr);
		packageID = nativeGetPackageID(nativePtr);
		basePath = nativeGetBasePath(nativePtr);
		type = nativeGetType(nativePtr);
		version = nativeGetVersion(nativePtr);
		isbn = nativeGetISBN(nativePtr);
		language = nativeGetLanguage(nativePtr);
		copyrightOwner = nativeGetCopyrightOwner(nativePtr);
		source = nativeGetSource(nativePtr);
		authors = nativeGetAuthors(nativePtr);
		modificationDate = nativeGetModificationDate(nativePtr);
		authorList = nativeGetAuthorList(nativePtr);
		subjects = nativeGetSubjects(nativePtr);
		spineItems = nativeGetSpineItems(nativePtr);
		tableOfContents = nativeGetTableOfContents(nativePtr);
		listOfFigures = nativeGetListOfFigures(nativePtr);
		listOfIllustrations = nativeGetListOfIllustrations(nativePtr);
		listOfTables = nativeGetListOfTables(nativePtr);
		pageList = nativeGetPageList(nativePtr);
		Log.i(TAG, "package nativePtr: "+nativePtr);
		Log.i(TAG, "title: "+title);
		Log.i(TAG, "subtitle: "+subtitle);
		Log.i(TAG, "shortTitle: "+shortTitle);
		Log.i(TAG, "collectionTitle: "+collectionTitle);
		Log.i(TAG, "editionTitle: "+editionTitle);
		Log.i(TAG, "expandedTitle: "+expandedTitle);
		Log.i(TAG, "fullTitle: "+fullTitle);
		Log.i(TAG, "uniqueID: "+uniqueID);
		Log.i(TAG, "urlSafeUniqueID: "+urlSafeUniqueID);
		Log.i(TAG, "packageID: "+packageID);
		Log.i(TAG, "basePath: "+basePath);
		Log.i(TAG, "type: "+type);
		Log.i(TAG, "version: "+version);
		Log.i(TAG, "isbn: "+isbn);
		Log.i(TAG, "language: "+language);
		Log.i(TAG, "copyrightOwner: "+copyrightOwner);
		Log.i(TAG, "source: "+source);
		Log.i(TAG, "authors: "+authors);
		Log.i(TAG, "authorList: "+authorList);
		Log.i(TAG, "modificationDate: "+modificationDate);
		Log.i(TAG, "subjects: "+subjects);
		Log.i(TAG, "spineItems: "+spineItems.size());
		Log.i(TAG, "tableOfContents: "+tableOfContents);
		Log.i(TAG, "listOfFigures: "+listOfFigures);
		Log.i(TAG, "listOfIllustrations: "+listOfIllustrations);
		Log.i(TAG, "listOfTables: "+listOfTables);
		Log.i(TAG, "pageList: "+pageList);
	}

	public int getNativePtr() {
		return nativePtr;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public String getCollectionTitle() {
		return collectionTitle;
	}

	public String getEditionTitle() {
		return editionTitle;
	}

	public String getExpandedTitle() {
		return expandedTitle;
	}

	public String getFullTitle() {
		return fullTitle;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public String getUrlSafeUniqueID() {
		return urlSafeUniqueID;
	}

	public String getPackageID() {
		return packageID;
	}
	
	public String getBasePath() {
		return basePath;
	}

	public String getType() {
		return type;
	}

	public String getVersion() {
		return version;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getLanguage() {
		return language;
	}

	public String getCopyrightOwner() {
		return copyrightOwner;
	}

	public String getSource() {
		return source;
	}

	public String getAuthors() {
		return authors;
	}

	public List<String> getAuthorList() {
		return authorList;
	}

	public String getModificationDate() {
		return modificationDate;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public List<SpineItem> getSpineItems() {
		return spineItems;
	}

	public NavigationTable getTableOfContents() {
		return tableOfContents;
	}

	public NavigationTable getListOfFigures() {
		return listOfFigures;
	}

	public NavigationTable getListOfIllustrations() {
		return listOfIllustrations;
	}

	public NavigationTable getListOfTables() {
		return listOfTables;
	}

	public NavigationTable getPageList() {
		return pageList;
	}

	public byte[] getContent(String relativePath) {
//		Log.i(TAG, "getContent-nativePtr: "+Integer.toHexString(nativePtr));
		ByteBuffer buffer = nativeReadStreamForRelativePath(nativePtr, relativePath);
		if (buffer == null) {
			return new byte[0];
		}
		byte[] content = new byte[buffer.limit()];
		System.arraycopy(buffer.array(), 0, content, 0, content.length);
		return content;
	}
	
	public String toJSON() {
		JSONObject o = new JSONObject();
		try {
			o.put("rootUrl", basePath);
			o.put("rendition_layout", nativeGetProperty(nativePtr, "layout", "rendition"));
			JSONArray spine = new JSONArray();
			for (SpineItem item : spineItems) {
				spine.put(item.toJSON());
			}
			o.put("spine", new JSONObject().put("items", spine));
//			Log.i(TAG, "JSON: " + o.toString(2));
		} catch (JSONException e) {
			Log.e(TAG, "" + e.getMessage(), e);
		}
		return o.toString();
	}

	/*
	 * Miscellaneous book metadata
	 */
	private native String nativeGetTitle(int nativePtr);
	private native String nativeGetSubtitle(int nativePtr);
	private native String nativeGetShortTitle(int nativePtr);
	private native String nativeGetCollectionTitle(int nativePtr);
	private native String nativeGetEditionTitle(int nativePtr);
	private native String nativeGetExpandedTitle(int nativePtr);
	private native String nativeGetFullTitle(int nativePtr);
	private native String nativeGetUniqueID(int nativePtr);
	private native String nativeGetURLSafeUniqueID(int nativePtr);
	private native String nativeGetPackageID(int nativePtr);
	private native String nativeGetBasePath(int nativePtr);
	private native String nativeGetType(int nativePtr);
	private native String nativeGetVersion(int nativePtr);
	private native String nativeGetISBN(int nativePtr);
	private native String nativeGetLanguage(int nativePtr);
	private native String nativeGetCopyrightOwner(int nativePtr);
	private native String nativeGetSource(int nativePtr);
	private native String nativeGetAuthors(int nativePtr);
	private native String nativeGetModificationDate(int nativePtr);
	private native List<String> nativeGetAuthorList(int nativePtr);
	private native List<String> nativeGetSubjects(int nativePtr);
	private native List<SpineItem> nativeGetSpineItems(int nativePtr);
	private native String nativeGetProperty(int nativePtr, String propertyName, String prefix);
	
	/*
	 * Navigation tables
	 */
	private native NavigationTable nativeGetTableOfContents(int nativePtr);
	private native NavigationTable nativeGetListOfFigures(int nativePtr);
	private native NavigationTable nativeGetListOfIllustrations(int nativePtr);
	private native NavigationTable nativeGetListOfTables(int nativePtr);
	private native NavigationTable nativeGetPageList(int nativePtr);
	
	/*
	 * Content 
	 */
	private native ByteBuffer nativeReadStreamForRelativePath(int nativePtr, String relativePath);

}
