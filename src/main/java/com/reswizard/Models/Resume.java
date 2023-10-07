package com.reswizard.Models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.reswizard.Util.Languages;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Resumes")
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
    private User owner;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Languages language;

    public Resume(@NotNull(message = "Enter resume title.") String title, @NotNull(message = "Add your resume in any format.") String path, User owner, Languages language) {
        this.title = title;
        this.path = path;
        this.owner = owner;
        this.language = language;
    }
    public Resume() {

    }

    @Override
    public String toString() {
        return "Resume{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", language=" + language +
                '}';
    }
}
