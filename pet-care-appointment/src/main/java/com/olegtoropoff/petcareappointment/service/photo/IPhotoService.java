package com.olegtoropoff.petcareappointment.service.photo;

import com.olegtoropoff.petcareappointment.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

public interface IPhotoService {
    Long savePhoto(MultipartFile file, Long userId) throws IOException, SQLException;

    Photo getPhotoById(Long id);

    void deletePhoto(Long id, Long userId) throws SQLException;

    void updatePhoto(Long id, MultipartFile file) throws SQLException, IOException;

    byte[] getImageData(Long id) throws SQLException;
}
