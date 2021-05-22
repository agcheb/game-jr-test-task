package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    Optional<Player> getById(Long id);

    boolean deleteById(Long id);

    Player insert(String name,
                  String title,
                  Race race,
                  Profession profession,
                  Long birthday,
                  Boolean banned,
                  Integer experience);

    Player update(Long id,
                  String name,
                  String title,
                  Race race,
                  Profession profession,
                  Long birthday,
                  Boolean banned,
                  Integer experience);


    List<Player> getFiltered(
            String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned,
            Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel,
            PlayerOrder order, Integer pageNumber,Integer pageSize);

    Integer getFilteredCount(
            String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned,
            Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel);
}
