package com.project.model.transfer;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by user on 4/18/2017.
 */
public class FileUploadForm {
    private List<MultipartFile> files;

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

    public Boolean isEmpty(){
        return files == null || files.isEmpty();
    }
}
