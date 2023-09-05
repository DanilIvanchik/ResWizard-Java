package com.reswizard.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Entity
@Data
@Table(name = "Resume")
public class Resume {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @NotNull(message = "Enter resume title.")
    @Column(name = "title")
    private String title;

    @NotNull(message = "Add your resume in any format.")
    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @JsonManagedReference
    private Person owner;

    public Resume() {

    }

    public Resume(String title, String path, Person person) {
        this.title = title;
        this.path = path;
        this.owner = person;
    }
}
