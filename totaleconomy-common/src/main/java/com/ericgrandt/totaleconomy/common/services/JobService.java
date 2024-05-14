package com.ericgrandt.totaleconomy.common.services;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.Job;
import com.ericgrandt.totaleconomy.common.domain.JobExperience;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.AddExperienceResponse;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import java.util.NoSuchElementException;

public class JobService {
    private final JobData jobData;

    public JobService(final JobData jobData) {
        this.jobData = jobData;
    }

    public GetJobRewardResponse getJobReward(GetJobRewardRequest request) throws NoSuchElementException {
        JobReward jobReward = jobData.getJobReward(request.action(), request.material()).orElseThrow();;
        return new GetJobRewardResponse(
            jobReward.getJobId(),
            jobReward.getMoney(),
            jobReward.getExperience()
        );
    }

    public AddExperienceResponse addExperience(AddExperienceRequest request) throws NoSuchElementException {
        Job job = jobData.getJob(request.jobId().toString()).orElseThrow();
        JobExperience jobExperience = jobData.getJobExperience(
            request.accountId().toString(),
            request.jobId().toString()
        ).orElseThrow();

        boolean willLevelUp = jobExperience.addExperience(request.experience());

        int result = jobData.updateJobExperience(jobExperience);
        if (result <= 0) {
            return new AddExperienceResponse(job.getJobName(), false);
        }

        return new AddExperienceResponse(job.getJobName(), willLevelUp);
    }
}
