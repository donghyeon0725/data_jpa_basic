package study.data_jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.data_jpa.domain.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findTeamsByName(String name);

    Team findTeamByName(String name);

    Optional<Team> findOptTeamByName(String name);
}
