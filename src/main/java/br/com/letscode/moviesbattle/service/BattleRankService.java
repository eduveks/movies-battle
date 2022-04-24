package br.com.letscode.moviesbattle.service;

import br.com.letscode.moviesbattle.data.repository.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BattleRankService {
    private final UserRepository userRepository;

    public Rank load(int page) {
        var users = userRepository.findRank(PageRequest.of(page - 1, 10));
        var total = userRepository.count();
        var debugInfo = new StringBuilder("\n\n\t"+ EmojiParser.parseToUnicode(":checkered_flag:") +" RANK\n\n");
        try {
            return new Rank(
                    total,
                    users.stream().map(
                            (u) -> {
                                debugInfo.append(String.format("\t%s\t%d\n", u.getUsername(), u.getScore()));
                                return new RankRecord(
                                        u.getUsername(),
                                        u.getScore()
                                );
                            }
                    ).collect(Collectors.toList())
            );
        } finally {
            log.info(debugInfo.toString());
        }
    }

    public record Rank(long size, List<RankRecord> records) {}
    public record RankRecord(String username, int score) {}
}
