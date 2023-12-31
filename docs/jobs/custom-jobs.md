---
layout: default
title: Creating Custom Jobs
parent: Jobs
nav_order: 1
---

# Creating Custom Jobs

If you're looking to add a new job to your server, you can do so by inserting data into your database. There are plans to make this process simpler from within game in the future.

## Create the Job

To create a new job, you'll need to insert a row into the `te_job` table. The `INSERT` query will look something like this (be sure to take note of the `id` that is generated):

```sql
INSERT INTO te_job (job_name) VALUES ('My Job');
```

This will create the job in the database, though in order to gain experience and money you'll need to add some rewards.

## Create the Rewards

Once you have a job created, you'll need to create some rewards in order for users to gain experience and money. Before doing this, make sure you have the `id` of the job you created as well as the `id` of the action(s) from the `te_job_action` table. With that information, `INSERT` rows into the table for each reward you want to associate with your new job.

For example, if you wanted to add a reward for stone, the query would look something like this:

```sql
INSERT INTO te_job_reward (job_id, job_action_id, currency_id, material, money, experience)
VALUES (YOUR_JOB_ID, TE_JOB_ACTION_ID, 1, 'stone', 0.10, 1);
```

For a list of supported job actions, either consult the rows in the database (especially if you need the `id`) or the [documentation](https://ericgrandt.github.io/TotalEconomy/jobs/#job-action).