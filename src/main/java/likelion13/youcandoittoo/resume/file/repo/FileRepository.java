package likelion13.youcandoittoo.resume.file.repo;

import likelion13.youcandoittoo.resume.file.entity.File;
import likelion13.youcandoittoo.resume.file.entity.FileTargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByTargetTypeAndTargetId(FileTargetType targetType, Long targetId);

    void deleteByTargetTypeAndTargetId(FileTargetType targetType, Long targetId);
}
