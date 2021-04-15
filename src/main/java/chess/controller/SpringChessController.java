package chess.controller;

import chess.dto.BoardDto;
import chess.dto.BoardStatusDto;
import chess.dto.MovablePositionDto;
import chess.dto.MoveRequestDto;
import chess.service.ChessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SpringChessController {
    private final ChessService chessService;

    public SpringChessController(ChessService chessService) {
        this.chessService = chessService;
    }

    @GetMapping("/")
    public String mainPage() {
        return "main";
    }

    @PostMapping("/start")
    public String startGame(@RequestParam("room") String id, Model model) {
        model.addAttribute("roomId", id);
        return "index";
    }

    @GetMapping("/create/{id}")
    @ResponseBody
    public ResponseEntity<BoardDto> createRoom(@PathVariable("id") String id) {
        return ResponseEntity.ok(chessService.loadRoom(id));
    }

    @PostMapping("/move")
    @ResponseBody
    public ResponseEntity<BoardDto> move(@RequestBody MoveRequestDto moveRequestDto) {
        try {
            return ResponseEntity.ok(chessService.move(moveRequestDto));
        } catch (Exception e) {
            return ResponseEntity.ok(chessService.loadRoom(moveRequestDto.getRoomId()));
        }
    }

    @PostMapping("/movable")
    @ResponseBody
    public ResponseEntity<List<String>> movablePosition(@RequestBody MovablePositionDto movablePositionDto) {
        return ResponseEntity.ok(chessService.movablePosition(movablePositionDto));
    }

    @PostMapping("/score")
    @ResponseBody
    public ResponseEntity<BoardStatusDto> score(@RequestBody MovablePositionDto movablePositionDto) throws JsonProcessingException {
        return ResponseEntity.ok(chessService.boardStatusDto(movablePositionDto.getRoomId()));
    }

    @GetMapping("/clear/{id}")
    public String clear(@PathVariable String id) {
        chessService.deleteRoom(id);
        return "redirect:/";
    }
}