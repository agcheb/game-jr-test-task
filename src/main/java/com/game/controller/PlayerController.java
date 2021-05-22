package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> getById(@PathVariable Long id) {
        if (id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Player> resultOpt = playerService.getById(id);
        return resultOpt.map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/rest/players")
    public ResponseEntity<List<Player>> getFiltered(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "3") Integer pageSize
    ) {
        List<Player> result = playerService.getFiltered(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping(value = "/rest/players/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return playerService.deleteById(id)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/rest/players/count")
    public ResponseEntity<Integer> getFiltered(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel
    ) {
        Integer result = playerService.getFilteredCount(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/rest/players")
    public ResponseEntity<Player> create(@RequestBody Map<String, Object> allParams) {
        String name = (String) allParams.get("name");
        String title = (String) allParams.get("title");
        Object raceObj = allParams.get("race");
        Race race = raceObj == null ? null : Race.valueOf((String) raceObj);
        Object professionObj = allParams.get("profession");
        Profession profession = professionObj == null ? null : Profession.valueOf((String) professionObj);
        Integer experience = (Integer) allParams.get("experience");
        Long birthday = (Long) allParams.get("birthday");
        Object bannedObj = allParams.get("banned");
        Boolean banned = bannedObj != null && (Boolean) bannedObj;

        if (!validateCreateRq(name, title, race, profession, birthday, experience)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player player = playerService.insert(name, title, race, profession, birthday, banned, experience);
        return player != null ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/rest/players/{id}")
    public ResponseEntity<Player> update(@PathVariable Long id,@RequestBody Map<String, Object> allParams) {

        Object idObj = allParams.get("id");
        String idStr = idObj == null ? null : idObj.toString();
        Long idJ = idStr == null ? null : Long.parseLong(idStr);
        String name = (String) allParams.get("name");
        String title = (String) allParams.get("title");
        Object raceObj = allParams.get("race");
        Race race = raceObj == null ? null : Race.valueOf((String) raceObj);
        Object professionObj = allParams.get("profession");
        Profession profession = professionObj == null ? null : Profession.valueOf((String) professionObj);
        Integer experience = (Integer) allParams.get("experience");
        Long birthday = (Long) allParams.get("birthday");
        Boolean banned = (Boolean) allParams.get("banned");

        if (!validateUpdateRq(id, name, title, birthday, experience)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Player player = playerService.update(id, name, title, race, profession, birthday, banned, experience);
        return player != null ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private boolean validateCreateRq(String name, String title, Race race, Profession profession, Long birthday, Integer experience) {
        LocalDate start = LocalDate.of(2000, 1, 1);
        LocalDate end = LocalDate.of(3001, 1, 1);
        if (race == null || profession == null || birthday == null) {
            return false;
        }
        LocalDate ld = LocalDateTime.ofInstant(new Date(birthday).toInstant(), ZoneId.systemDefault()).toLocalDate();
        return  name != null && !name.isEmpty() && name.length() <= 12 &&
                title != null && !title.isEmpty() && title.length() <= 30 &&
                ld.getYear() >= start.getYear() && ld.getYear() < end.getYear() &&
                experience >= 0 && experience <= 10_000_000;
    }
    private boolean validateUpdateRq(Long id, String name, String title, Long birthday, Integer experience) {
        boolean result = true;
        if (id == null || id < 1) {
            return false;
        }
        if (birthday != null) {
            LocalDate start = LocalDate.of(2000, 1, 1);
            LocalDate end = LocalDate.of(3001, 1, 1);
            LocalDate ld = LocalDateTime.ofInstant(new Date(birthday).toInstant(), ZoneId.systemDefault()).toLocalDate();
            result = ld.getYear() >= start.getYear() && ld.getYear() < end.getYear();
        }
        if (name != null) {
            result = result && !name.isEmpty() && name.length() <= 12;
        }

        if (title != null) {
            result = result && !title.isEmpty() && title.length() <= 30;
        }
        if (experience != null) {
            result = result && experience >= 0 && experience <= 10_000_000;
        }
        return result;
    }

}
