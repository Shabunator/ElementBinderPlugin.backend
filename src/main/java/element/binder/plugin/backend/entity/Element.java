package element.binder.plugin.backend.entity;

import element.binder.plugin.backend.configuration.ApplicationContextProvider;
import element.binder.plugin.backend.converter.JsonStringListConverter;
import element.binder.plugin.backend.service.MinioService;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
@Table(name = "elements")
public class Element {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "inner_project_id", nullable = false)
    private InnerProject innerProject;

    @Column(name = "images_url", columnDefinition = "jsonb")
    @Convert(converter = JsonStringListConverter.class)
    private List<String> imagesUrl;

    @Column(name = "name")
    private String name;

    @Column(name = "article")
    private String article;

    @Column(name = "size")
    private String size;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "price")
    private Double price;

    @Column(name = "create_date")
    private Instant createDate;

    @PrePersist
    private void onCreate() {
        createDate = Instant.now();
    }

    @PreRemove
    public void preRemove() {
        MinioService minioService = ApplicationContextProvider.getBean(MinioService.class);
        String projectName = innerProject.getProject().getName();
        String innerProjectFolderName = innerProject.getName();
        String folderPath = projectName + "/" + innerProjectFolderName;
        String pathToFile = folderPath + "/" + name;
        minioService.deleteFile(pathToFile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Element element = (Element) o;
        return Objects.equals(id, element.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
