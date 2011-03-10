package com.sarxos.ow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.dalsemi.onewire.container.OneWireContainer;
import com.sarxos.ow.device.AbstractDevice;
import com.sarxos.ow.device.Device;

/**
 * Interfejs okreslajacy podstawowe metody magazynu urzadzen.<br>
 * @author Bartosz Firyn (SarXos)
 */
public interface Storage {

	/**
	 * Zapisuje urzadzenie w magazynie.<br>
	 * @param d - urzadzenie do serializacji
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public void save(Device d) throws IOException;

	/**
	 * Wczytuje wszyskie zapisane urzadzenia.<br> 
	 * @return Zwraca liste tych urzadzen
	 * @throws StorageManagerException 
	 */
	public List <Device> loadAll() throws StorageManagerException;

	/**
	 * Wczytuje i zwraca urzadzenie dla okreslonego kontenera OneWire.<br> 
	 * @param c - kontener dla ktorego ma wczytac urzadzenie
	 * @return Zwraca obiekt <code>{@link AbstractDevice}</code>
	 * @throws StorageManagerException 
	 */
	public Device load(OneWireContainer c) throws StorageManagerException;

}