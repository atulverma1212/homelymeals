package org.bits.pilani.homely.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploadService {

    String uploadFile(MultipartFile image) throws IOException;

}
