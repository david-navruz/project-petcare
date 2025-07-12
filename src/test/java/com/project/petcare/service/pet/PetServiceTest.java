package com.project.petcare.service.pet;

import com.project.petcare.exception.ResourceNotFoundException;
import com.project.petcare.model.Appointment;
import com.project.petcare.model.Pet;
import com.project.petcare.repository.AppointmentRepository;
import com.project.petcare.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void savePets_shouldReturnSavedList() {
        List<Pet> pets = Arrays.asList(new Pet(), new Pet());
        when(petRepository.saveAll(pets)).thenReturn(pets);
        List<Pet> result = petService.savePets(pets);
        assertEquals(2, result.size());
    }

    @Test
    void savePetsForAppointment_shouldSetAppointmentAndSaveEach() {
        Long appointmentId = 10L;
        Appointment appointment = new Appointment();
        Pet pet1 = new Pet();
        Pet pet2 = new Pet();
        List<Pet> pets = Arrays.asList(pet1, pet2);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(petRepository.save(pet1)).thenReturn(pet1);
        when(petRepository.save(pet2)).thenReturn(pet2);

        List<Pet> result = petService.savePetsForAppointment(appointmentId, pets);

        assertEquals(2, result.size());
        assertSame(appointment, pet1.getAppointment());
        assertSame(appointment, pet2.getAppointment());
    }

    @Test
    void savePetsForAppointment_appointmentNotFound_shouldThrowNullPointer() {
        Long appointmentId = 99L;
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class,
                () -> petService.savePetsForAppointment(appointmentId, List.of(new Pet())));
    }

    @Test
    void updatePet_shouldUpdateFieldsAndSave() {
        Long petId = 5L;
        Pet existing = new Pet();
        existing.setName("Buddy");
        existing.setAge(5);
        existing.setColor("Brown");
        Pet update = new Pet();
        update.setName("Buddy");
        update.setAge(4);
        update.setColor("Black");
        update.setType("Dog");
        update.setBreed("Bulldog");

        when(petRepository.findById(petId)).thenReturn(Optional.of(existing));
        when(petRepository.save(existing)).thenReturn(existing);

        Pet result = petService.updatePet(petId, update);
        assertEquals("Buddy", existing.getName());
        assertEquals(4, existing.getAge());
        assertEquals("Black", existing.getColor());
        assertEquals("Dog", existing.getType());
        assertEquals("Bulldog", existing.getBreed());
        assertEquals(existing, result);
    }

    @Test
    void updatePet_notFound_shouldThrow() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> petService.updatePet(1L, new Pet()));
    }

    @Test
    void deletePet_found_shouldDelete() {
        Long id = 12L;
        Pet pet = new Pet();
        when(petRepository.findById(id)).thenReturn(Optional.of(pet));

        petService.deletePet(id);
        verify(petRepository).delete(pet);
    }

    @Test
    void deletePet_notFound_shouldThrow() {
        when(petRepository.findById(20L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> petService.deletePet(20L));
    }

    @Test
    void getPetById_found_shouldReturnPet() {
        Pet pet = new Pet();
        when(petRepository.findById(6L)).thenReturn(Optional.of(pet));
        assertEquals(pet, petService.getPetById(6L));
    }

    @Test
    void getPetById_notFound_shouldThrow() {
        when(petRepository.findById(21L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> petService.getPetById(21L));
    }

    @Test
    void getPetTypes_shouldReturnTypes() {
        List<String> types = Arrays.asList("Dog", "Cat");
        when(petRepository.getDistinctPetTypes()).thenReturn(types);
        assertEquals(types, petService.getPetTypes());
    }

    @Test
    void getPetColors_shouldReturnColors() {
        List<String> colors = Arrays.asList("Black", "White");
        when(petRepository.getDistinctPetColors()).thenReturn(colors);
        assertEquals(colors, petService.getPetColors());
    }

    @Test
    void getPetBreeds_shouldReturnBreeds() {
        List<String> breeds = Arrays.asList("Bulldog", "Poodle");
        when(petRepository.getDistinctPetBreedByPetType("Dog")).thenReturn(breeds);
        assertEquals(breeds, petService.getPetBreeds("Dog"));
    }
}
