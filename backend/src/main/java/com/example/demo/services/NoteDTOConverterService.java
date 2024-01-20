package com.example.demo.services;

import com.example.demo.dtos.NoteDTO;
import com.example.demo.models.Note;
import com.example.demo.models.User;
import com.example.demo.repositories.NoteRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NoteDTOConverterService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoteDTOConverterService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    public NoteDTO convertToNoteDTO(Note note) {
        return new NoteDTO(note.getTitle(), note.getContent(), null, null);
    }

    public Note convertToNote(NoteDTO noteDTO, Integer ownerId) {
        Optional<User> owner = userRepository.findById(ownerId);
        if(owner.isEmpty()){
            throw new IllegalArgumentException("Error occured");
        }
        Note note = new Note(owner.get(), noteDTO.getTitle(), noteDTO.getContent(), noteDTO.getIsPublic(), noteDTO.getPassword());
        return note;
    }

}
