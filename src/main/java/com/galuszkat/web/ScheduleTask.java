package com.galuszkat.web;

import com.galuszkat.DataConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleTask {

    private List<String> images = new ArrayList<>();

    @Autowired
    private SimpMessagingTemplate template;

    public ScheduleTask() {
        Thread thread = new Thread(() -> new DataConsumer().consume(this::updateImages));
        thread.start();
    }

    private void updateImages(List<String> newImages) {
        System.out.println("Before update: " + images.size());
        images.addAll(newImages);
        System.out.println("After update: " + images.size());
    }

    // this will send a message to an endpoint on which a client can subscribe
    @Scheduled(fixedRate = 500)
    public void trigger() {
        if (images.isEmpty()) {
            return;
        }

        System.out.println("Before send: " + images.size());
        template.convertAndSend("/topic/message", images.get(0));
        images.remove(0);
        System.out.println("After send: " + images.size());
    }

}