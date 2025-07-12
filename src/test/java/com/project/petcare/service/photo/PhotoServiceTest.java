package com.project.petcare.service.photo;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Photo;
import com.project.petcare.model.User;
import com.project.petcare.repository.PhotoRepository;
import com.project.petcare.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void savePhoto_shouldSavePhotoAndUpdateUser() throws IOException, SQLException {
        Long userId = 1L;
        User user = new User();
        Photo photo = new Photo();

        byte[] bytes = new byte[]{1, 2, 3};
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(bytes);
        when(multipartFile.getOriginalFilename()).thenReturn("photo.png");
        when(multipartFile.getContentType()).thenReturn("image/png");
        when(photoRepository.save(any(Photo.class))).thenReturn(photo);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        Photo saved = photoService.savePhoto(multipartFile, userId);

        assertNotNull(saved);
        verify(photoRepository).save(any(Photo.class));
        verify(userRepository).save(user);
        assertEquals(photo, user.getPhoto());
    }

    @Test
    void savePhoto_fileIsNull_shouldStillSavePhoto() throws IOException, SQLException {
        Long userId = 1L;
        User user = new User();
        Photo photo = new Photo();
        when(photoRepository.save(any(Photo.class))).thenReturn(photo);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        Photo saved = photoService.savePhoto(null, userId);

        assertNotNull(saved);
        verify(photoRepository).save(any(Photo.class));
        verify(userRepository).save(user);
    }

    @Test
    void getPhotoById_found_returnsPhoto() {
        Photo photo = new Photo();
        when(photoRepository.findById(5L)).thenReturn(Optional.of(photo));
        Photo result = photoService.getPhotoById(5L);
        assertEquals(photo, result);
    }

    @Test
    void getPhotoById_notFound_throwsException() {
        when(photoRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> photoService.getPhotoById(5L));
    }

    @Test
    void updatePhoto_found_shouldUpdatePhotoAndThrow() throws IOException, SQLException {
        Photo photo = new Photo();
        when(photoRepository.findById(2L)).thenReturn(Optional.of(photo));
        when(multipartFile.getBytes()).thenReturn(new byte[]{1, 2});
        when(multipartFile.getName()).thenReturn("photo.jpg");
        when(multipartFile.getContentType()).thenReturn("image/jpg");
        when(photoRepository.save(photo)).thenReturn(photo);

        assertThrows(ResourceNotFoundException.class, () -> photoService.updatePhoto(2L, multipartFile));
        verify(photoRepository).save(photo);
    }

    @Test
    void updatePhoto_notFound_shouldThrowException() {
        when(photoRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> photoService.updatePhoto(10L, multipartFile));
    }

    @Test
    void deletePhotoById_shouldRemoveUserPhotoAndDeletePhoto() throws SQLException {
        Long photoId = 3L;
        Long userId = 4L;
        User user = mock(User.class);
        Photo photo = new Photo();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        photoService.deletePhotoById(photoId, userId);

        verify(user).removeUserPhoto();
        verify(photoRepository).delete(photo);
    }

    @Test
    void deletePhotoById_userNotFound_shouldThrow() {
        Long userId = 99L, photoId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> photoService.deletePhotoById(photoId, userId));
    }

    @Test
    void deletePhotoById_photoNotFound_shouldThrow() {
        Long photoId = 2L, userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(photoRepository.findById(photoId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> photoService.deletePhotoById(photoId, userId));
    }

    @Test
    void getPhotoData_found_shouldReturnBytes() throws SQLException {
        Long id = 7L;
        Photo photo = new Photo();
        byte[] imageBytes = new byte[]{4, 5, 6};
        Blob blob = new SerialBlob(imageBytes);

        photo.setImage(blob);
        when(photoRepository.findById(id)).thenReturn(Optional.of(photo));

        byte[] result = photoService.getPhotoData(id);
        assertArrayEquals(imageBytes, result);
    }

    @Test
    void getPhotoData_notFound_shouldThrow() {
        when(photoRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> photoService.getPhotoData(10L));
    }
}
