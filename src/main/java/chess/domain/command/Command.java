package chess.domain.command;

import chess.domain.ChessGame;
import chess.exception.GameIsNotStartException;
import chess.exception.NoSuchCommandException;

import java.util.Arrays;
import java.util.function.BiConsumer;

public enum Command {
    START("start", (chessgame, input) -> chessgame.start()),
    MOVE("move", ChessGame::move),
    STATUS("status", (chessgame, input) -> chessgame.status()),
    END("end", (chessgame, input) -> chessgame.end());

    private static final String DELIMITER = " ";

    private final String command;
    private final BiConsumer<ChessGame, String> biConsumer;

    Command(final String command, final BiConsumer<ChessGame, String> biConsumer) {
        this.command = command;
        this.biConsumer = biConsumer;
    }

    public static Command findCommand(String input) throws GameIsNotStartException {
        return Arrays.stream(values()).sequential()
                .filter(value -> value.command.equals(input.split(DELIMITER)[0]))
                .findAny()
                .orElseThrow(NoSuchCommandException::new);
    }

    public void execute(ChessGame chessgame, String input) {
        biConsumer.accept(chessgame, input);
    }
}
