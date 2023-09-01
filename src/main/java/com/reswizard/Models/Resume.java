package com.reswizard.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.File;

@Entity
@Table(name = "Resume")
public class Resume {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotNull(message = "Enter resume title.")
    @Column(name = "title")
    private String title;

    @NotNull(message = "Add your resume in pdf format.")
    @Column(name = "file_resume")
    private File resume;

    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    @JsonManagedReference
    private Person owner;
}
