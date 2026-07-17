package com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.repository;

import java.util.List;
import java.util.Optional;

import com.NTFOODS.Api_tanty.modules.users.domain.enums.UserRole;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaEntity;
import org.springframework.stereotype.Component;

import com.NTFOODS.Api_tanty.modules.users.domain.aggregate.UserAggregate;
import com.NTFOODS.Api_tanty.modules.users.domain.repository.UserRepository;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.PhoneNumber;
import com.NTFOODS.Api_tanty.modules.users.domain.valueobject.UserMatricule;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.jpa.UserJpaRepository;
import com.NTFOODS.Api_tanty.modules.users.infrastructure.persistence.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository jpaRepository;

  @Override
  public Optional<UserAggregate> findByMatricule(UserMatricule matricule) {
    // BUG CORRIGÉ (compilation) : l'ancienne version appelait jpaRepository.findByMatricule(String, Pageable)
    // qui n'existe pas dans l'interface, ce qui empêchait tout le module Users de compiler.
    // BUG CORRIGÉ (exécution) : findByMatricule renvoyait Optional via un "findBy" unique,
    // qui lève NonUniqueResultException dès qu'un matricule est dupliqué en base (ce qui
    // arrivait systématiquement tant que save() ci-dessous créait un nouvel utilisateur à
    // chaque appel). On utilise désormais une liste et on choisit le meilleur candidat.
    return pickBest(jpaRepository.findAllByMatricule(matricule.value())).map(UserMapper::toDomain);
  }

  @Override
  public Optional<UserAggregate> findByPhone(PhoneNumber phone) {
    return pickBest(jpaRepository.findAllByPhone(phone.getNumber())).map(UserMapper::toDomain);
  }

  @Override
  public void save(UserAggregate user) {
    // BUG CORRIGÉ : UserMapper.toEntity générait systématiquement un nouvel UUID,
    // ce qui transformait chaque mise à jour (ex: verrouillage de compte, changement de rôle)
    // en une INSERTION d'un nouvel utilisateur au lieu d'une mise à jour. On réutilise
    // désormais l'UUID existant s'il y en a un.
    Optional<UserJpaEntity> existing = pickBest(jpaRepository.findAllByMatricule(user.getMatricule().value()));
    UserJpaEntity entity = UserMapper.toEntity(user);
    existing.ifPresent(e -> entity.setId(e.getId()));
    jpaRepository.save(entity);
  }

  /**
   * Choisit le meilleur candidat parmi des entités partageant un même matricule/téléphone
   * (doublons hérités d'un bug de persistance désormais corrigé) : priorité au compte ACTIVE
   * le plus récent (UUID le plus élevé à défaut d'horodatage de création en base), sinon le
   * premier trouvé. Ne supprime pas les doublons - un nettoyage SQL manuel reste nécessaire
   * en base pour les éliminer définitivement.
   */
  private Optional<UserJpaEntity> pickBest(List<UserJpaEntity> candidates) {
    if (candidates.isEmpty()) return Optional.empty();
    if (candidates.size() == 1) return Optional.of(candidates.get(0));
    return candidates.stream()
        .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
        .max(java.util.Comparator.comparing(u -> u.getId().toString()))
        .or(() -> candidates.stream().max(java.util.Comparator.comparing(u -> u.getId().toString())));
  }

  @Override
  public long count() {
    return jpaRepository.count();
  }

  @Override
  public List<UserAggregate> findByRole(UserRole role) {
    return jpaRepository.findByRole(role.name()).stream().map(UserMapper::toDomain).toList();
  }
}
