package com.university.parent.client;

import com.university.parent.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "MENTOR-SERVICE", configuration = FeignConfig.class)
public interface MentorClient {

    @GetMapping("/mentor/progress/{rollNumber}")
    Object getStudentProgress(@PathVariable String rollNumber);

    @GetMapping("/mentor/attendance/{rollNumber}")
    List<Object> getStudentAttendance(@PathVariable("rollNumber") String rollNumber);
}
