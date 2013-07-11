package org.readium.sdklauncher_android;

import com.readium.model.epub3.components.navigation.NavigationTable;

public class TableOfContentsActivity extends NavigationTableActivity {

	protected NavigationTable getNavigationTable() {
		NavigationTable navigationTable = null;
        if (pckg != null) {
        	navigationTable = pckg.getTableOfContents();
        }
		return (navigationTable != null) ? navigationTable : new NavigationTable("toc", "", "");
	}
}
