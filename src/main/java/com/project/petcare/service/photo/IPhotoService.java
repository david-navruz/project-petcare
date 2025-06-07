package com.project.petcare.service.photo;

import com.project.petcare.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public interface IPhotoService {

    Photo savePhoto(MultipartFile file, Long userId) throws IOException, SQLException;;

    Photo getPhotoById(Long id);

    Photo updatePhoto(Long id, MultipartFile file) throws IOException, SQLException;

    void deletePhotoById(Long id, Long userId) throws SQLException;

    byte[]  getPhotoData(Long id) throws SQLException;

}
