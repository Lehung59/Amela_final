package com.example.demo.utils;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImageUpload {

    private final Cloudinary cloudinary;


    public String upload(MultipartFile file) throws Exception {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }
}