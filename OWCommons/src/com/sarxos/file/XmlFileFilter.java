package com.sarxos.file;

import java.io.File;


public class XmlFileFilter extends BasicFileFilter {

	@Override
	public boolean accept(File f) {
		if(f != null && f.isFile()) {
			if(f.getName().endsWith(".xml")) {
				return true;
			}
		}
		return false;
	}

	public boolean accept(File dir, String name) {
		if(name != null && name.endsWith(".xml")) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return "XML File";
	}
}
