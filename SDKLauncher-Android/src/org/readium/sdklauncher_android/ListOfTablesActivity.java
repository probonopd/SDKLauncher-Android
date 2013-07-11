package org.readium.sdklauncher_android;

import com.readium.model.epub3.components.navigation.NavigationTable;

public class ListOfTablesActivity extends NavigationTableActivity {

	protected NavigationTable getNavigationTable() {
		NavigationTable navigationTable = null;
        if (pckg != null) {
        	navigationTable = pckg.getListOfTables();
        }
		return (navigationTable != null) ? navigationTable : new NavigationTable("lot", "", "");
	}
}
