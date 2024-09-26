package com.orderon.commons;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
	
	public static void moveFile(String oldPath, String newPath) {
	
		Path temp;
		try {
			temp = Files.move(Paths.get(oldPath), Paths.get(newPath));
			  
	        if(temp != null) 
	        { 
	            System.out.println("File renamed and moved successfully"); 
	        } 
	        else
	        { 
	            System.out.println("Failed to move the file"); 
	        } 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deleteFile(String filePath) {
		File fileToDelete = new File(filePath);
		fileToDelete.delete();
	}
}
