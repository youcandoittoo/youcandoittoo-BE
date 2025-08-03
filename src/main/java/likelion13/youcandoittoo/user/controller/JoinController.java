package likelion13.youcandoittoo.user.controller;

import likelion13.youcandoittoo.user.dto.JoinDto;
import likelion13.youcandoittoo.user.service.JoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {

    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinDto joinDto) {

        return joinService.joinProcess(joinDto);
    }
}
