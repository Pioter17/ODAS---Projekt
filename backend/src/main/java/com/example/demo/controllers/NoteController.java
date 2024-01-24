package com.example.demo.controllers;

import com.example.demo.dtos.NoteDTO;
import com.example.demo.models.Note;
import com.example.demo.models.User;
import com.example.demo.other.ServiceResponse;
import com.example.demo.repositories.NoteRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.units.qual.N;
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
    private final NoteService noteService;
    private final NoteDTOConverterService noteDTOConverterService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public NoteController(
            NoteRepository noteRepository,
            NoteService noteService,
            NoteDTOConverterService noteDTOConverterService,
            JwtService jwtservice,
            UserDetailsService userDetailsService
    ) {
        this.noteRepository = noteRepository;
        this.noteService = noteService;
        this.noteDTOConverterService = noteDTOConverterService;
        this.jwtService = jwtservice;
        this.userDetailsService = userDetailsService;
    }

    // Endpoint do pobierania wszystkich notatek (publicznych)
    @GetMapping
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<List<NoteDTO>> getPublicNotes() {
        List<NoteDTO> finalList = noteService.getAllPublic();
        return new ServiceResponse<>(finalList, true, "All public notes");
    }

    @GetMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<Note> getNoteById(@PathVariable Long id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                Note note = noteService.getById(id, userName);
                if (note != null){
                    return new ServiceResponse<>(note, true, "Note");
                }
            }
        }
        return new ServiceResponse<>(null, false, "Error occured");
    }

    @GetMapping("/user")
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<List<Note>> getUserNotes(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                List<Note> userNotes = noteService.getUserNotes(userName);
                if(userNotes != null){
                    return new ServiceResponse<>(userNotes, true, "All user notes");
                }
            }
        }
        return new ServiceResponse<>(null, false, "Error during getting user notes occured");
    }

    @PostMapping("/decrypt/{id}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<NoteDTO> decryptUserNote(@PathVariable Long id, @RequestBody String notePassword, HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                NoteDTO response = noteService.decrypt(notePassword, id, userName);
                return new ServiceResponse<>(response, true, "Decrypted");
            }
        }
        return new ServiceResponse<>(null, false, "Error during decrypting note occured");
    }

    // Endpoint do dodawania nowej notatki
    @PostMapping
    @CrossOrigin(origins = "http://localhost:4200")
    public ServiceResponse<NoteDTO> addNote(@RequestBody NoteDTO noteDTO, HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String userName = jwtService.extractUsername(jwt);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                return noteService.addNote(noteDTO, userName);
            }
        }
        return new ServiceResponse<>(null, false, "Error during adding note occured");
    }
}

