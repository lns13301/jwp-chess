package chess.service;

import chess.dao.ChessLogDao;
import chess.dao.DBConnection;
import chess.domain.ChessGame;
import chess.domain.board.Board;
import chess.dto.BoardDto;
import chess.dto.BoardStatusDto;
import chess.dto.MovablePositionDto;
import chess.dto.MoveRequestDto;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class ChessService {
    private static final String END_TRUE = "true";

    private final ChessLogDao chessLogDao;

    public ChessService() {
        this.chessLogDao = new ChessLogDao(new DBConnection());
    }

    public BoardDto loadRoom(String roomNumber) {
        return start(loadChessGame(roomNumber));
    }

    private ChessGame loadChessGame(String roomNumber) {
        List<String> commands = chessLogDao.applyCommand(roomNumber);
        ChessGame chessgame = new ChessGame();
        chessgame.settingBoard();

        for (String command : commands) {
            chessgame.move(command);
        }

        return chessgame;
    }

    public BoardDto move(MoveRequestDto moveRequestDto) throws SQLException {
        ChessGame chessgame = loadChessGame(moveRequestDto.getRoomId());

        try {
            BoardDto boardDto = movePiece(chessgame, moveRequestDto);
            chessLogDao.addLog(moveRequestDto.getRoomId(), moveRequestDto.getTarget(), moveRequestDto.getDestination());
            return boardDto;
        } catch (Exception e) {
            return start(chessgame);
        }
    }

    private BoardDto start(ChessGame chessgame) {
        Board board = chessgame.getBoard();
        return new BoardDto(board, chessgame.turn());
    }

    private BoardDto movePiece(ChessGame chessgame, MoveRequestDto moveRequestDto) {
        chessgame.move(moveRequestDto.getTarget(), moveRequestDto.getDestination());
        if (chessgame.isBeforeEnd()) {
            return new BoardDto(chessgame.getBoard(), chessgame.turn());
        }
        return new BoardDto(chessgame.getBoard(), chessgame.turn().name(), END_TRUE);
    }

    public List<String> movablePosition(MovablePositionDto movablePositionDto) {
        return loadChessGame(movablePositionDto.getRoomId()).findMovablePosition(movablePositionDto.getTarget());
    }

    public BoardStatusDto boardStatusDto(String roomId) {
        return new BoardStatusDto(loadChessGame(roomId).boardStatus());
    }

    public void deleteRoom(String roomNumber) {
        chessLogDao.deleteLog(roomNumber);
    }
}
