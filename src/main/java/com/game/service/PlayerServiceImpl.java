package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Optional<Player> getById(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Player insert(String name,
                         String title,
                         Race race,
                         Profession profession,
                         Long birthday,
                         Boolean banned,
                         Integer experience) {
        Player player = new Player(name, title, race, profession, experience, new Date(birthday), banned);
        int level = countLevel(experience);
        player.setLevel(level);
        player.setUntilNextLevel(countUntilNextLevel(experience, level));
        return playerRepository.saveAndFlush(player);
    }

    @Override
    public Player update(Long id, String name, String title, Race race, Profession profession,
                         Long birthday, Boolean banned, Integer experience) {
        Optional<Player> playerOpt = playerRepository.findById(id);

        if (!playerOpt.isPresent()) {
            return null;
        } else {
            Player player = playerOpt.get();
            if (name != null) {
                player.setName(name);
            }
            if (title != null) {
                player.setTitle(title);
            }
            if (race != null) {
                player.setRace(race);
            }
            if (profession != null) {
                player.setProfession(profession);
            }
            if (birthday != null) {
                player.setBirthday(new Date(birthday));
            }
            if (banned != null) {
                player.setBanned(banned);
            }
            if (experience != null) {
                player.setExperience(experience);
                int level = countLevel(experience);
                player.setLevel(level);
                player.setUntilNextLevel(countUntilNextLevel(experience, level));
            }
            return playerRepository.saveAndFlush(player);
        }
    }

    @Override
    public List<Player> getFiltered(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel, PlayerOrder order, Integer pageNumber, Integer pageSize) {
        Date dateBefore = before == null ? null : new Date(before);
        Date dateAfter = after == null ? null : new Date(after);
        Page<Player> filtered = playerRepository.findFiltered(name, title, race, profession, dateAfter, dateBefore, banned, minExperience, maxExperience, minLevel, maxLevel, PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName())));
        return filtered.toList();
    }

    @Override
    public Integer getFilteredCount(String name, String title, Race race, Profession profession, Long after, Long before, Boolean banned, Integer minExperience, Integer maxExperience, Integer minLevel, Integer maxLevel) {
        Date dateBefore = before == null ? null : new Date(before);
        Date dateAfter = after == null ? null : new Date(after);
        return playerRepository.findFilteredCount(name, title, race, profession, dateAfter, dateBefore, banned, minExperience, maxExperience, minLevel, maxLevel);
    }

    private static int countLevel(int exp) {
        return (int) (Math.sqrt(2500 + 200 * exp) - 50 ) / 100;
    }

    private static int countUntilNextLevel(int exp, int lvl) {
        return 50 * (lvl + 1) * (lvl + 2) - exp;
    }
}
