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
    private final CryptoService cryptoService;

    @Autowired
    public NoteService(NoteRepository noteRepository, CryptoService cryptoService) {
        this.noteRepository = noteRepository;
        this.cryptoService = cryptoService;
    }

    public ServiceResponse<Note> addNote(Note note) {
        if (note.getId() != null) {
            Optional<Note> noteById = noteRepository.findById(note.getId());
            if (noteById.isPresent()) {
                return new ServiceResponse<Note>(null, false, "Note is already in db");
            }
        }
        try {
            if (note.getIsPublic()){
                noteRepository.save(note);
                return new ServiceResponse<>(note, true, "Note added");
            } else {
                Note newNote = encryptNoteContent(note);
                if (newNote != null){
                    noteRepository.save(newNote);
                    return new ServiceResponse<>(newNote, true, "Note added");
                }
                return new ServiceResponse<>(null, false, "Error during adding note");
            }
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "Error during adding note");
        }
    }

    private Note encryptNoteContent(Note note){
        Note noteWithEncryptedContent = note;

        String encryptedMessage = cryptoService.encrypt(note.getContent(), note.getPassword());
        if (encryptedMessage != null) {
            noteWithEncryptedContent.setContent(encryptedMessage);
            return noteWithEncryptedContent;
        }
        return null;
    }
}
