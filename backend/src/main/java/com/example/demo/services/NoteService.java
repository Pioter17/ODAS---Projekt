package com.example.demo.services;

import com.example.demo.dtos.NoteDTO;
import com.example.demo.models.Note;
import com.example.demo.models.User;
import com.example.demo.other.ServiceResponse;
import com.example.demo.repositories.NoteRepository;
import com.example.demo.repositories.UserRepository;
import org.aspectj.weaver.ast.Not;
import org.checkerframework.checker.units.qual.N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteDTOConverterService noteDTOConverterService;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Autowired
    public NoteService(NoteRepository noteRepository, UserRepository userRepository, NoteDTOConverterService noteDTOConverterService) {
        this.noteRepository = noteRepository;
        this.noteDTOConverterService = noteDTOConverterService;
        this.userRepository = userRepository;
    }

    public List<NoteDTO> getAllPublic() {
        List<Note> list = noteRepository.findByIsPublicTrue();
        List<NoteDTO> finalList = new ArrayList<>();
        list.forEach(note -> finalList.add(noteDTOConverterService.convertToNoteDTO(note)));
        finalList.forEach(noteDTO -> noteDTO.setContent(sanitizeHtml(noteDTO.getContent())));
        return finalList;
    }

    public Note getById(Long id, String userName){
        Optional<Note> note = noteRepository.findById(id);
        if(note.isEmpty()){
            return null;
        }
        Optional<User> user = userRepository.findByName(userName);
        if(user.isEmpty()){
            return null;
        }
        if (Objects.equals(user.get().getId(), note.get().getOwner().getId()) || note.get().getIsPublic()){
            Note resultNote = note.get();
            resultNote.setOwner(null);
            resultNote.setContent(sanitizeHtml(resultNote.getContent()));
            return resultNote;
        }
        return null;

    }

    public List<Note> getUserNotes(String userName){
        Optional<User> owner;
        owner = userRepository.findByName(userName);
        if (owner.isEmpty()){
            return null;
        }
        List<Note> userNotes = owner.get().getNotes();
        userNotes.forEach(note -> {
            note.setOwner(null);
        });
        return userNotes;
    }

    public ServiceResponse<NoteDTO> addNote(NoteDTO noteDTO, String userName) {
        Optional<User> owner;
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
        if (note == null ||
                note.getOwner() == null ||
                note.getTitle() == null || note.getIsPublic() == null ||
                (note.getIsPublic() == false && noteDTO.getPassword() == null)) {
            return new ServiceResponse<>(null, false, "Body is missing");
        }
        if (note.getId() != null) {
            Optional<Note> noteById = noteRepository.findById(note.getId());
            if (noteById.isPresent()) {
                return new ServiceResponse<>(null, false, "Note is already in db");
            }
        }
        try {
            if (note.getIsPublic()){
                note.setIv(null);
                note.setContent(sanitizeHtml(note.getContent()));
                noteRepository.save(note);
                return new ServiceResponse<>(noteDTOConverterService.convertToNoteDTO(note), true, "Note added");
            } else {
                note.setContent(sanitizeHtml(note.getContent()));
                Note newNote = encryptNoteContent(note, noteDTO.getPassword());
                if (newNote != null){
                    noteRepository.save(newNote);
                    return new ServiceResponse<>(noteDTOConverterService.convertToNoteDTO(newNote), true, "Note added");
                }
                return new ServiceResponse<>(null, false, "Error during adding note");
            }
        } catch (Exception e) {
            return new ServiceResponse<>(null, false, "Error during adding note");
        }
    }

    public NoteDTO decrypt(String password, Long id, String userName) {
        Optional<Note> note;
        Optional<User> owner;
        owner = userRepository.findByName(userName);
        note = noteRepository.findById(id);
        if (owner.isEmpty() || note.isEmpty()){
            return null;
        }
        Note foundNote = note.get();
        if (foundNote.getOwner() == owner.get()){
            try {
                SecretKeySpec keySpec = generateKeyFromPassword(password);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                IvParameterSpec ivSpec = new IvParameterSpec(foundNote.getIv());
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

                byte[] decodedBytes = Base64.getDecoder().decode(foundNote.getContent());
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return new NoteDTO(foundNote.getTitle(), new String(decryptedBytes, StandardCharsets.UTF_8), foundNote.getIsPublic(),password);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private Note encryptNoteContent(Note note, String password){
        Note noteWithEncryptedContent = note;

        try {
            SecretKeySpec keySpec = generateKeyFromPassword(password);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivSpec = generateIV();
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(note.getContent().getBytes(StandardCharsets.UTF_8));
            noteWithEncryptedContent.setContent(Base64.getEncoder().encodeToString(encryptedBytes));
            noteWithEncryptedContent.setIv(ivSpec.getIV());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return noteWithEncryptedContent;
    }

    private SecretKeySpec generateKeyFromPassword(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(keyBytes, "AES");
    }

    private IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private String sanitizeHtml(String input) {
        Whitelist whitelist = Whitelist.basicWithImages();
        whitelist.addTags("b", "i", "h1", "h2", "h3", "h4", "h5", "img", "a");
        whitelist.addAttributes("a", "href");
        whitelist.addAttributes("img", "src");

        String cleanedHtml = Jsoup.clean(input, whitelist);

        return cleanedHtml;
    }
}
