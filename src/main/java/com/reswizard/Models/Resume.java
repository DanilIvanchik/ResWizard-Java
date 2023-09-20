package com.reswizard.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.reswizard.Util.Languages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    @NotNull(message = "Add your resume pdf or docx.")
    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @JsonManagedReference
    private Person owner;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Languages language;

    public Resume(@NotNull(message = "Enter resume title.") String title, @NotNull(message = "Add your resume in any format.") String path, Person owner, Languages language) {
        this.title = title;
        this.path = path;
        this.owner = owner;
        this.language = language;
    }
    public Resume() {

    }

}
