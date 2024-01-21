package com.example.demo.controllers;

import com.example.demo.dtos.NoteDTO;
import com.example.demo.models.Note;
import com.example.demo.models.User;
import com.example.demo.other.ServiceResponse;
import com.example.demo.repositories.NoteRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/notes")
@CrossOrigin(origins = "http://localhost:4200")
public class NoteController {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteService noteService;
    private final NoteDTOConverterService noteDTOConverterService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public NoteController(
            NoteRepository noteRepository,
            UserRepository userRepository,
            NoteService noteService,
            NoteDTOConverterService noteDTOConverterService,
            JwtService jwtservice,
            UserDetailsService userDetailsService
    ) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.noteService = noteService;
        this.noteDTOConverterService = noteDTOConverterService;
        this.jwtService = jwtservice;
        this.userDetailsService = userDetailsService;
    }

    // Endpoint do pobierania wszystkich notatek (publicznych)
    @GetMapping
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<List<NoteDTO>> getPublicNotes() {
         List<Note> list = noteRepository.findByIsPublicTrue();
         List<NoteDTO> finalList = new ArrayList<>();
         list.forEach(note -> finalList.add(noteDTOConverterService.convertToNoteDTO(note)));
         return new ServiceResponse<>(finalList, true, "All public notes");
    }

    @GetMapping("/user")
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<List<Note>> getUserNotes(HttpServletRequest request){
        Optional<User> owner;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    owner = userRepository.findByName(userName);
                    if (owner.isEmpty()){
                        return new ServiceResponse<>(null, false, "Error during getting user notes occured");
                    }
                    List<Note> userNotes = owner.get().getNotes();
                    return new ServiceResponse<>(userNotes, true, "All user notes");
                }
            }
        }
        return new ServiceResponse<>(null, false, "Error during getting user notes occured");
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<NoteDTO> decryptUserNote(@PathVariable Long id, @RequestBody String notePassword, HttpServletRequest request){
        Optional<User> owner;
        Optional<Note> note;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    owner = userRepository.findByName(userName);
                    note = noteRepository.findById(id);
                    if (owner.isEmpty() || note.isEmpty()){
                        return new ServiceResponse<>(null, false, "Error during decrypting note occured");
                    }
                    Note foundNote = note.get();
                    if (foundNote.getOwner() == owner.get()  && Objects.equals(foundNote.getPassword(), notePassword)){
                        NoteDTO noteResponse = noteService.decrypt(foundNote);
                        return new ServiceResponse<>(noteResponse, true, "Decrypted note");
                    }
                }
            }
        }
        return new ServiceResponse<>(null, false, "Error during decrypting note occured");
    }

    // Endpoint do dodawania nowej notatki
    @PostMapping
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<Note> addNote(@RequestBody NoteDTO noteDTO, HttpServletRequest request) {
        Optional<User> owner;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    owner = userRepository.findByName(userName);
                    if (owner.isEmpty()){
                        return new ServiceResponse<>(null, false, "Error during adding note occured");
                    }
                    Note note;
                    try{
                        note = this.noteDTOConverterService.convertToNote(noteDTO, owner.get().getId());
                    } catch (Exception e) {
                        return new ServiceResponse<>(null,false,"Cannot parse item");
                    }
                    if (note == null || note.getOwner() == null || note.getTitle() == null || note.getContent() == null || note.getIsPublic() == null || note.getPassword() == null) {
                        return new ServiceResponse<>(null, false, "Body is missing");
                    }
                    ServiceResponse<Note> response = noteService.addNote(note);
                    return response;
                }
            }
        }
        return new ServiceResponse<>(null, false, "Error during adding note occured");
    }
}

