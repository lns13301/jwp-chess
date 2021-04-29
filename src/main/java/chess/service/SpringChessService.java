package chess.service;

import chess.dao.SpringChessLogDao;
import chess.dao.SpringChessRoomDao;
import chess.domain.ChessGame;
import chess.domain.board.Board;
import chess.dto.*;
import chess.exception.IllegalRoomException;
import chess.exception.InvalidMoveException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SpringChessService {
    private static final String END_TRUE = "true";

    private final SpringChessRoomDao springChessRoomDao;
    private final SpringChessLogDao springChessLogDao;

    public SpringChessService(SpringChessRoomDao springChessRoomDao, SpringChessLogDao springChessLogDao) {
        this.springChessRoomDao = springChessRoomDao;
        this.springChessLogDao = springChessLogDao;
    }

    public Long createRoom(RoomDto roomDto) {
        return springChessRoomDao.add(roomDto);
    }

    public BoardDto loadRoom(String id) {
        return start(loadChessGame(id));
    }

    public List<RoomDto> loadAllRoom() {
        return springChessRoomDao.findAllRoom();
    }

    private ChessGame loadChessGame(String id) {
        validateRoom(id);
        List<CommandDto> commands = springChessLogDao.find(id);
        ChessGame chessgame = new ChessGame();

        chessgame.settingBoard();

        for (CommandDto command : commands) {
            chessgame.move(command.getTarget(), command.getDestination());
        }

        return chessgame;
    }

    private void validateRoom(String name) {
        if (Objects.isNull(name)) {
            throw new IllegalRoomException("[ERROR] 방 이름은 공백이 될 수 없습니다.");
        }
    }

    public BoardDto move(MoveRequestDto moveRequestDto) {
        ChessGame chessgame = loadChessGame(moveRequestDto.getRoomId());

        return movePiece(chessgame, moveRequestDto);
    }

    private BoardDto start(ChessGame chessgame) {
        Board board = chessgame.getBoard();
        return new BoardDto(board, chessgame.turn());
    }

    private BoardDto movePiece(ChessGame chessgame, MoveRequestDto moveRequestDto) {
        if (!chessgame.move(moveRequestDto.getTarget(), moveRequestDto.getDestination())) {
            throw new InvalidMoveException();
        }

        springChessLogDao.add(moveRequestDto);

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

    public String findRoomById(String id) {
        return springChessLogDao.findRoomById(id);
    }

    public void deleteRoom(String roomNumber) {
        springChessLogDao.delete(roomNumber);
        springChessRoomDao.delete(roomNumber);
    }
}
