package com.ecommerce.project.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    public String uploadImage(String path, MultipartFile file) {
        //File name of current/original file
        //Gives file name with extension
        String originalFileName= file.getOriginalFilename();

        //Generate a unique file/image name, coz it should not override the other file/image name, achieved via RandomUUID
        // and we will name the file as per that UUID and not the original name
        //Universal Unique ID in an in-build class which gives random unique ID
        String randomId= UUID.randomUUID().toString();

        //If our file name is mat.jpg and randomId is 1234, below method converts it to --> 1234.jpg
        String fileName= randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));

        //File.pathSeparator is a constant provided by the Java File class that represents
        // the system-dependent character used to separate individual file paths in a list
        //Windows: The path separator is a semicolon (;)
        //When you have a string that contains multiple paths e.g., "C:\Program Files;C:\Windows\System32"
        String filePathName= path + File.separator + fileName;

        //Check if path exit or create a new path
        File folder= new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }

        //Upload server
        try{
            Files.copy(file.getInputStream(), Paths.get(filePathName));
        }catch(IOException e){
            e.getMessage();
        }

        //Return the file name
        return filePathName;
    }

}
