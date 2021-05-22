package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p from Player p WHERE " +
            "(:name IS NULL or p.name like %:name%) AND " +
            "(:title IS NULL or p.title like %:title%) AND " +
            "(:race IS NULL or p.race = :race) AND " +
            "(:profession IS NULL or p.profession = :profession) AND " +
            "(:after IS NULL or p.birthday >= :after) AND " +
            "(:before IS NULL or p.birthday <= :before) AND " +
            "(:banned IS NULL or p.banned = :banned) AND " +
            "(:minExperience IS NULL or p.experience >= :minExperience) AND "+
            "(:maxExperience IS NULL or p.experience <= :maxExperience) AND "+
            "(:minLevel IS NULL or p.level >= :minLevel) AND "+
            "(:maxLevel IS NULL or p.level <= :maxLevel)"
    )
    Page<Player> findFiltered(@Param("name") String name,
                              @Param("title") String title,
                              @Param("race") Race race,
                              @Param("profession") Profession profession,
                              @Param("after") Date after,
                              @Param("before") Date before,
                              @Param("banned") Boolean banned,
                              @Param("minExperience") Integer minExperience,
                              @Param("maxExperience") Integer maxExperience,
                              @Param("minLevel") Integer minLevel,
                              @Param("maxLevel") Integer maxLevel,
                              Pageable pageable
                              );


    @Query("SELECT count(p) from Player p WHERE " +
            "(:name IS NULL or p.name like %:name%) AND " +
            "(:title IS NULL or p.title like %:title%) AND " +
            "(:race IS NULL or p.race = :race) AND " +
            "(:profession IS NULL or p.profession = :profession) AND " +
            "(:after IS NULL or p.birthday >= :after) AND " +
            "(:before IS NULL or p.birthday <= :before) AND " +
            "(:banned IS NULL or p.banned = :banned) AND " +
            "(:minExperience IS NULL or p.experience >= :minExperience) AND "+
            "(:maxExperience IS NULL or p.experience <= :maxExperience) AND "+
            "(:minLevel IS NULL or p.level >= :minLevel) AND "+
            "(:maxLevel IS NULL or p.level <= :maxLevel)"
    )
    Integer findFilteredCount(@Param("name") String name,
                              @Param("title") String title,
                              @Param("race") Race race,
                              @Param("profession") Profession profession,
                              @Param("after") Date after,
                              @Param("before") Date before,
                              @Param("banned") Boolean banned,
                              @Param("minExperience") Integer minExperience,
                              @Param("maxExperience") Integer maxExperience,
                              @Param("minLevel") Integer minLevel,
                              @Param("maxLevel") Integer maxLevel
    );
}
