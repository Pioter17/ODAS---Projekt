package com.example.demo.services;

import com.example.demo.models.Note;
import com.example.demo.other.ServiceResponse;
import com.example.demo.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public ServiceResponse<Note> addNote(Note note) {
        if (note.getId() != null) {
            Optional<Note> noteById = noteRepository.findById(note.getId());
            if (noteById.isPresent()) {
                return new ServiceResponse<Note>(null, false, "Note is already in db");
            }
        }
        try {
            noteRepository.save(note);
            return new ServiceResponse<Note>(note, true, "Note added");
        } catch (Exception e) {
            return new ServiceResponse<Note>(null, false, "Error during adding movie");
        }
    }
}
