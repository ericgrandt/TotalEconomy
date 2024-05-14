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
        Job job = jobData.getJob(jobReward.getJobId()).orElseThrow();
        JobExperience jobExperience = jobData.getJobExperience(request.accountId().toString(), jobReward.getJobId()).orElseThrow();

        boolean willLevelUp = jobExperience.addExperience(jobReward.getExperience());

        int result = jobData.updateJobExperience(jobExperience);
        if (result <= 0) {
            return new AddExperienceResponse(job.getJobName(), false);
        }

        return new AddExperienceResponse(job.getJobName(), willLevelUp);
    }
}
