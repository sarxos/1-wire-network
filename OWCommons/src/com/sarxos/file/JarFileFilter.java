package com.sarxos.file;

import java.io.File;


public class JarFileFilter extends BasicFileFilter {

	protected String extension = "jar";
	protected String description = "Java Archive"; 
	
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
