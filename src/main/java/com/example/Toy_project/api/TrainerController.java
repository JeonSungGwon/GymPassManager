//package com.example.Toy_project.api;
//
//import com.example.Toy_project.dto.TrainerDto;
//import com.example.Toy_project.repository.TrainerRepository;
//import com.example.Toy_project.service.TrainerService;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/trainer")
//public class TrainerController {
//
//    private final TrainerRepository trainerRepository;
//    private final TrainerService trainerService;
//
//    public TrainerController(TrainerRepository trainerRepository, TrainerService trainerService){
//        this.trainerRepository=trainerRepository;
//        this.trainerService = trainerService;
//    }
//
//    @PostMapping
//    public TrainerDto createTrainer(@RequestBody TrainerDto trainerDto){
//        return trainerService.createTrainer(trainerDto);
//    }
//
//    @GetMapping
//    public List<TrainerDto> getAllTrainer(){
//        return trainerService.getAllTrainer();
//    }
//
//}
