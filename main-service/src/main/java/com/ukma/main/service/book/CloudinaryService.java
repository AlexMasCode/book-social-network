package com.ukma.main.service.book;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@Service
public class CloudinaryService {

    @Resource
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String upload(MultipartFile file, String folder) throws IOException {
        var options = new HashMap<>();
        options.put("folder", folder);
        var uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
        var publicId = (String) uploadedFile.get("public_id");

        return cloudinary.url().secure(true).generate(publicId);
    }

    public String remove(String publicId, String folder) throws IOException {
        var options = new HashMap<>();
        options.put("folder", folder);
        cloudinary.uploader().destroy(publicId, options);
        return publicId;
    }
}
