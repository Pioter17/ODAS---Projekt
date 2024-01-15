package com.example.demo.services;

import com.example.demo.dtos.MovieDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieDTOConverterService {
    private final DirectorRepository directorRepository;

    @Autowired
    public MovieDTOConverterService(MovieRepository movieRepository, DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public Movie convert(MovieDTO movieDTO) {
        if(movieDTO.getDirector_id()==null) {
            System.out.println("pierwszyif");
            throw new IllegalArgumentException("Missing director ID");
        }
        Optional<Director> director = directorRepository.findById(movieDTO.getDirector_id());
        if(director.isEmpty()){
            System.out.println("drugiif");
            throw new IllegalArgumentException("Wrong director ID");
        }
        Director director2 = directorRepository.findById(movieDTO.getDirector_id()).orElse(null);
        System.out.println(director2.getName() + " " + director2.getNationality() + " " + director2.getAge());
        Movie movie = new Movie(movieDTO.getName(), director.get(), movieDTO.getProducer(), movieDTO.getRating(), movieDTO.getLength());
        return movie;
    }
}
