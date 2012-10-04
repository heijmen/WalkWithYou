package eu.uniek.wwy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.List;

public class ThomasMagEenNaamVerzinnen {

	@SuppressWarnings("unchecked")
	public List<GPSLocation> getBroodkruimels(FileInputStream file) throws StreamCorruptedException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(file);
		Object o = ois.readObject();
		file.close();
		return (List<GPSLocation>) o;
	}
	public void saveBroodKruimels(List<GPSLocation> locations, FileOutputStream fileOutputStream) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
		oos.writeObject(locations);
		fileOutputStream.close();
	}

}
