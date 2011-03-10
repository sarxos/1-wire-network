package com.sarxos.ow.sm;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.file.ZipFileFilter;
import com.sarxos.ow.OWCache;
import com.sarxos.ow.Storage;
import com.sarxos.ow.StorageManagerException;
import com.sarxos.ow.device.AbstractDevice;
import com.sarxos.ow.device.Device;
import com.sarxos.ow.xml.XmlDeviceSerializer;

public class DirStorageManager implements Storage {

	private OWCache cache = null;
	private XmlDeviceSerializer serializer = null;
	private String storageDirectory = null; 
	private String fileExtension = "zip";
	
	public DirStorageManager(OWCache cache) {
		
		if(cache == null) {
			throw new IllegalArgumentException(
					OWCache.class.getSimpleName() + " object in " + 
					DirStorageManager.class.getSimpleName() + " can't be null!"
			);
		}
		
		this.cache = cache;
		this.serializer = getSerializer();
		
		readProperties();
		testStorage();
	}

	/**
	 * Wczytuje konieczne ustawienia z pliku konfiguracyjnego.<br>
	 */
	private void readProperties() {
		
		// TODO: odczyt propertisow przeniesc do klasy konfiguracji
		
		// wczytujemy plik propertisow
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("owd.properties"));
		} catch (Exception e) {
			exceptionOccured(e);
		}
		
		// odczytujemy interesujaca nas property, jesli brak to ustawiamy domyslny magazyn
		storageDirectory = p.getProperty("owd.storage.dir");
		if(storageDirectory == null) {
			storageDirectory = "storage";
		}
	}
	
	/**
	 * Test czy katalog na pliki z serializowanymi devices istnieje na dysku.<br>
	 */
	private void testStorage() {
		File storage = new File(storageDirectory + "/.");
		if(!storage.exists()) {
			try {
				storage.mkdir();
			} catch (SecurityException e) {
				exceptionOccured(e);
			}
		}
	}
	
	
	/**
	 * @return Zwraca obiekt <code>{@link OWCache}</code>.<br>
	 */
	public OWCache getCache() {
		return cache;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Storage#save(com.sarxos.onewire.devices.Device)
	 */
	public synchronized void save(Device d) throws IOException {
		
		OneWireContainer container = d.getContainer();
		String addr = container.getAddressAsString().toLowerCase();
		
		File device_dir = new File(storageDirectory + "/" + addr + "/."); 
		if(!device_dir.exists()) {
			device_dir.mkdir();
		}
		
		String d_file = device_dir.getPath() + "/device.xml";
		String s_file = device_dir.getPath() + "/script.js";
		
		// zapisujemy deskryptor urzadzenia
		FileOutputStream fos = new FileOutputStream(d_file);
		getSerializer().toXML(d, fos);
		fos.close();

		fos = new FileOutputStream(s_file);
		if(d.getScript() != null) {
			fos.write(d.getScript().getBytes());
		}
		fos.close();
	}
	
	protected ZipOutputStream createZipStream(Device d) throws FileNotFoundException {
		
		OneWireContainer container = d.getContainer();
		String addr = container.getAddressAsString().toLowerCase();
		String filen = storageDirectory + "/" + addr + "." + fileExtension;
		File file = new File(filen);
		
		FileOutputStream fos = new FileOutputStream(file);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zos.setComment("Device " + addr + " descriptor.");
		
		return zos;
	}
	
	protected ZipEntry createEntry(String name, String comment) {
		ZipEntry entry = new ZipEntry(name);
		entry.setTime(new Date().getTime());
		if(comment != null && comment.length() > 0) {
			entry.setComment(comment);
		}
		return entry;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Storage#loadAll()
	 */
	public synchronized List <Device> loadAll() throws StorageManagerException {

		// wczytujemy wszystkie pliki xml z magazynu
		File storage = new File(storageDirectory + "/.");
		File[] files = storage.listFiles();
		if(files == null) {
			return Collections.emptyList();
		}

		// z kazdego pliku tworzymy Device i dodajemy do listy
		List <Device> devices = new ArrayList<Device>((int) (files.length * 1.25));
		for(File f : files) {
			try {
				if(!f.isDirectory()) {
					continue;
				}
				Device d = loadDeviceFromFolder(f);
				if(d != null) {
					devices.add(d);
				}
			} catch (IOException e) {
				exceptionOccured(e);
			}
		}
		
		return devices;
	}
	
	/* (non-Javadoc)
	 * @see com.sarxos.onewire.Storage#load(com.dalsemi.onewire.container.OneWireContainer)
	 */
	public synchronized Device load(OneWireContainer c) throws StorageManagerException {
		if(c == null) {
			return null;
		}
		try {
			String path = storageDirectory + "/" + c.getAddressAsString() + "." + fileExtension;
			File f = new File(path);
			if(f.exists()) {
				Device device = loadDeviceFromFolder(f);
				return device;
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}
	
	protected synchronized Device loadDeviceFromFolder(File f) throws StorageManagerException, IOException {
	
		AbstractDevice device = null;

		String d_file = f.getPath() + "/device.xml";
		InputStream is = new FileInputStream(d_file);
		device = (AbstractDevice) serializer.fromXML(is);
		is.close();
	
		// wczytujemy skrypt uruchomieniowy
		String s_file = f.getPath() + "/script.js";
		is = new FileInputStream(s_file);
		StringWriter sw = new StringWriter();
		int c = -1;
		while((c = is.read()) != -1) {
			sw.write(c);
		}
		is.close();
		String script = sw.toString();
		if(script != null && script.length() > 0) {
			device.setScript(script);
		}
	
		return device;
	}
	
	/**
	 * Zwraca serializator urzadzen.<br>
	 * @return Zwraca obiekt <code>{@link XmlDeviceSerializer}</code>
	 */
	public XmlDeviceSerializer getSerializer() {
		if(serializer == null) {
			serializer = new XmlDeviceSerializer();
		}
		return serializer;
	}
	
	protected void exceptionOccured(Exception e) {
		getCache().getController().getService().exceptionOccured(e);
	}
}
