package com.example.demo.services;

import com.example.demo.dtos.NoteDTO;
import com.example.demo.models.Note;
import com.example.demo.other.ServiceResponse;
import com.example.demo.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Autowired
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public ServiceResponse<Note> addNote(Note note) {
        if (note.getId() != null) {
            Optional<Note> noteById = noteRepository.findById(note.getId());
            if (noteById.isPresent()) {
                return new ServiceResponse<>(null, false, "Note is already in db");
            }
        }
        try {
            if (note.getIsPublic()){
                note.setIv(null);
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

        try {
            SecretKeySpec keySpec = generateKeyFromPassword(note.getPassword());
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

    public NoteDTO decrypt(Note note) {
        try {
            SecretKeySpec keySpec = generateKeyFromPassword(note.getPassword());
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(note.getIv());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decodedBytes = Base64.getDecoder().decode(note.getContent());
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new NoteDTO(note.getTitle(), new String(decryptedBytes, StandardCharsets.UTF_8), note.getIsPublic(), note.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
}
