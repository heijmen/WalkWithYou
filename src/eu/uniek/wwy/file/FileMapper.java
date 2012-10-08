package eu.uniek.wwy.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.uniek.wwy.location.GPSLocation;

public class FileMapper {

	@SuppressWarnings("unchecked")
	public List<GPSLocation> getAllBreadcrumbsFromFile(FileInputStream fileInputStream) throws IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		Object object = objectInputStream.readObject();
		fileInputStream.close();
		return (List<GPSLocation>) object;
	}
	public void saveBreadCrumbsToFile(List<GPSLocation> locations, FileOutputStream fileOutputStream) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(locations);
		fileOutputStream.close();
	}
	
	public void resetBreadCrumbsInFile(FileOutputStream fileOutputStream) throws IOException {
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(new ArrayList<GPSLocation>());
		fileOutputStream.close();
	}

}
