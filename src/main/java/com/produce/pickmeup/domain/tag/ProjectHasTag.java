package com.produce.pickmeup.domain.tag;


import com.produce.pickmeup.domain.project.Project;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "project_has_tag")
@NoArgsConstructor
public class ProjectHasTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag projectTag;

    @Builder
    public ProjectHasTag(Project project, Tag tag) {
        this.project = project;
        this.projectTag = tag;
    }
}
