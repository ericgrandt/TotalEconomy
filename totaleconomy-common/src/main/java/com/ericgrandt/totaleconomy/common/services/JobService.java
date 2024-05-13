package com.ericgrandt.totaleconomy.common.services;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.AddExperienceResponse;

import java.util.NoSuchElementException;

public class JobService {
    private final JobData jobData;

    public JobService(final JobData jobData) {
        this.jobData = jobData;
    }

    public AddExperienceResponse addExperience(AddExperienceRequest request) throws NoSuchElementException {
        JobReward jobReward = jobData.getJobReward(request.action(), request.materialName()).orElseThrow();
        Job job = jobData.getJob(request.jobId()).orElseThrow();
        JobExperience jobExperience = jobData.getJobExperience(request.accountId(), request.jobId()).orElseThrow();
        // job.setJobExperience(jobExperience)
        return new AddExperienceResponse(job.getJobName(), false);
    }
}
