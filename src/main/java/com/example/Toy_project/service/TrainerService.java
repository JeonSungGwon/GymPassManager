package com.example.Toy_project.service;

import com.example.Toy_project.dto.PTSubscriptionRequestDTO;
import com.example.Toy_project.dto.TrainerDto;
import com.example.Toy_project.entity.PTSubscription;
import com.example.Toy_project.entity.Trainer;
import com.example.Toy_project.repository.TrainerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Component
public class TrainerService {
    private final TrainerRepository trainerRepository;
    private final ModelMapper modelMapper;

    public TrainerService(TrainerRepository trainerRepository, ModelMapper modelMapper){
        this.trainerRepository = trainerRepository;
        this.modelMapper = modelMapper;
    }

    public TrainerDto createTrainer(TrainerDto trainerDto){
        Trainer trainer = modelMapper.map(trainerDto,Trainer.class);
        System.out.println(trainer.getName());
        Trainer savedTrainer = trainerRepository.save(trainer);
        TrainerDto savedDto = modelMapper.map(savedTrainer,TrainerDto.class);
        return savedDto;
    }

    public List<TrainerDto> getAllTrainer(){
        List<Trainer> trainers = trainerRepository.findAll();
        return trainers.stream()
                .map(trainer -> modelMapper.map(trainer,TrainerDto.class))
                .collect(Collectors.toList());
    }

}
