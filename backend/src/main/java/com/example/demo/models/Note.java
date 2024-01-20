package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "note")
public class Note {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "director_id")
        private User owner;

        private String title;

        private String content;

        private Boolean isPublic;

        private String password;

        public Note(User owner, String title, String content, Boolean isPublic, String password) {
                this.owner = owner;
                this.title = title;
                this.content = content;
                this.isPublic = isPublic;
                this.password = password;
        }
}
