package com.sarxos.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileFilter;


public class CompoundFileFilter extends FileFilter implements FilenameFilter {

	private List <BasicFileFilter> filters = new ArrayList<BasicFileFilter>(); 
	
	public CompoundFileFilter(List <BasicFileFilter> filters) {
		this.filters.addAll(filters);
	}

	public CompoundFileFilter(BasicFileFilter[] filters) {
		List <BasicFileFilter> list = Arrays.asList(filters); 
		this.filters.addAll(list);
	}
	
	public CompoundFileFilter() {
		// do nothing
	}

	@Override
	public boolean accept(File f) {
		boolean accept = false;
		if(f != null && f.isFile()) {
			// synchronize to prevent concurent comodification
			synchronized (filters) { 
				int n = filters.size();
				for (int i = 0; i < n; i++) {
					if (accept |= filters.get(i).accept(f)) {
						break;
					}
				}
			}
		}
		return accept;
	}

	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		// synchronize to prevent concurent comodification
		synchronized (filters) { 
			int n = filters.size();
			for (int i = 0; i < n; i++) {
				sb.append(filters.get(i).getDescription());
				if (i < n - 1) {
					sb.append(',');
				}
			}
		}
		return sb.toString();
	}

	public boolean accept(File dir, String name) {
		boolean accept = false;
		if(dir != null && name != null && dir.isDirectory()) {
			// synchronize to prevent concurent comodification
			synchronized (filters) { 
				int n = filters.size();
				for (int i = 0; i < n; i++) {
					if (accept |= filters.get(i).accept(dir, name)) {
						break;
					}
				}
			}
		}
		return accept;
	}

	public void addFilter(BasicFileFilter filter) {
		// synchronize to prevent concurent comodification
		synchronized (filters) {
			filters.add(filter);
		}
	}
	
	public boolean removeAllFilters() {
		// synchronize to prevent concurent comodification
		synchronized (filters) {
			if (filters.size() > 0) {
				filters.clear();
				return true;
			}
			return false;
		}
	}
}
