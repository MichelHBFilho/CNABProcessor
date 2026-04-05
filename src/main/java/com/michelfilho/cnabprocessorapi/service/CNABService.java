package com.michelfilho.cnabprocessorapi.service;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CNABService {
    private final Path path;
    private final JobOperator jobOperator;
    private final Job job;

    public CNABService(JobOperator jobOperator,
                       Job job,
                       @Value("${file.upload-dir}") String fileUploadDir) {
        this.path = Paths.get(fileUploadDir);
        this.jobOperator = jobOperator;
        this.job = job;
    }

    public void process(MultipartFile file) throws Exception {
        var fileName = StringUtils.cleanPath(file.getOriginalFilename());
        var targetLocation = path.resolve(fileName);
        file.transferTo(targetLocation);

        JobParameters params = new JobParametersBuilder()
                .addJobParameter(
                        "cnab",
                        file.getOriginalFilename(),
                        String.class,
                        true)
                .addJobParameter(
                        "cnabFile",
                        "file:" + targetLocation.toString(),
                        String.class,
                        false
                ).toJobParameters();

        jobOperator.start(job, params);
    }

}
