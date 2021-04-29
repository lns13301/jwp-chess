package chess.controller;

import chess.domain.ChessGame;
import chess.domain.command.Command;
import chess.exception.GameIsNotStartException;
import chess.view.InputView;
import chess.view.OutputView;

public class ChessController {
    public void run(ChessGame chessgame) {
        OutputView.printManual();
        while (chessgame.isBeforeEnd()) {
            playGame(chessgame);
        }
    }

    public void playGame(ChessGame chessgame) {
        try {
            String inputCommand = InputView.inputCommand();
            Command.findCommand(inputCommand).execute(chessgame, inputCommand);
            checkGameStart(chessgame);
            OutputView.printBoard(chessgame.getBoard());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkGameStart(ChessGame chessgame) {
        if (chessgame.isBeforeStart()) {
            throw new GameIsNotStartException();
        }
    }
}
