package com.ecoride.notification.repository;

import com.ecoride.notification.domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

    Optional<Template> findByCode(String code);
}
