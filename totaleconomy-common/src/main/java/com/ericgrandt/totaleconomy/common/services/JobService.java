package com.ericgrandt.totaleconomy.common.services;

import com.ericgrandt.totaleconomy.common.data.JobData;
import com.ericgrandt.totaleconomy.common.domain.JobReward;
import com.ericgrandt.totaleconomy.common.models.AddExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetAllJobExperienceRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobExperienceResponse;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardRequest;
import com.ericgrandt.totaleconomy.common.models.GetJobRewardResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class JobService {
    private final JobData jobData;

    public JobService(final JobData jobData) {
        this.jobData = jobData;
    }

    public GetJobRewardResponse getJobReward(GetJobRewardRequest request) throws NoSuchElementException {
        JobReward jobReward = jobData.getJobReward(request).orElseThrow();;
        return new GetJobRewardResponse(
            jobReward.getJobId(),
            jobReward.getMoney(),
            jobReward.getExperience()
        );
    }

    public List<GetJobExperienceResponse> getAllJobExperience(GetAllJobExperienceRequest request) {
        return new ArrayList<>();
    }

    public void addExperience(AddExperienceRequest request) {
        jobData.updateJobExperience(request);
    }
}
