package com.project.petcare.service.photo;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Photo;
import com.project.petcare.model.User;
import com.project.petcare.repository.PhotoRepository;
import com.project.petcare.repository.UserRepository;
import com.project.petcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PhotoService implements IPhotoService {

    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;


    @Override
    public Photo savePhoto(MultipartFile file, Long userId) throws IOException, SQLException {
        Optional<User> theUser = userRepository.findById(userId);
        Photo photo = new Photo();
        if (file != null && !file.isEmpty()) {
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            photo.setImage(photoBlob);
            photo.setFileName(file.getOriginalFilename());
            photo.setFileType(file.getContentType());
        }
        Photo savedPhoto = photoRepository.save(photo);
        theUser.ifPresent(user -> {
            user.setPhoto(savedPhoto);
        });
        userRepository.save(theUser.get());
        return savedPhoto;
    }


    @Override
    public Photo getPhotoById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.RESOURCE_FOUND));
    }


    @Override
    public Photo updatePhoto(Long id, MultipartFile file) throws IOException, SQLException {
        Photo existingPhoto = getPhotoById(id);
        if (existingPhoto != null) {
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            existingPhoto.setImage(photoBlob);
            existingPhoto.setFileName(file.getName());
            existingPhoto.setFileType(file.getContentType());
            photoRepository.save(existingPhoto);
        }
        throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
    }


    @Override
    public void deletePhotoById(Long id, Long userId) throws SQLException {
        // First, we need to find the related user and set its photo = null
        userRepository.findById(userId).ifPresentOrElse(User::removeUserPhoto, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
        });
        // Secondly, we find the photo and delete it
        photoRepository.findById(id).ifPresentOrElse(photoRepository::delete, () -> {
            throw new ResourceNotFoundException(FeedBackMessage.RESOURCE_NOT_FOUND);
        });
    }


    @Override
    public byte[] getPhotoData(Long id) throws SQLException {
        Photo thePhoto = getPhotoById(id);
        if (thePhoto != null) {
            Blob photoBlob = thePhoto.getImage();
            int theBlobLength = (int) photoBlob.length();
            return photoBlob.getBytes(1, theBlobLength); // Blob index starts at 1
        }
        return new byte[0];
    }

}
