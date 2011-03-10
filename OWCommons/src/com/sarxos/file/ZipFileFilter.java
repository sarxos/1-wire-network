package com.sarxos.file;

import java.io.File;


public class ZipFileFilter extends BasicFileFilter {

	protected String extension = "zip";  
	protected String description = "ZIP File"; 
	
	@Override
	public boolean accept(File f) {
		if(f != null && f.isFile()) {
			if(f.getName().endsWith("." + extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean accept(File dir, String name) {
		if(name != null && name.endsWith("." + extension)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
}
