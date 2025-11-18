package likelion13.youcandoittoo.resume.repo;

import likelion13.youcandoittoo.resume.enums.InputType;
import likelion13.youcandoittoo.resume.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    Page<Resume> findAllByEmail(String email, Pageable pageable);

    Page<Resume> findAllByEmailAndInputType(String email, InputType inputType, Pageable pageable);

    boolean existsByResumeIdAndEmail(Long resumeId, String email);
}
